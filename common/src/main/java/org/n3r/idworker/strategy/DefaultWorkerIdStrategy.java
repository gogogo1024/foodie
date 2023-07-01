package org.n3r.idworker.strategy;

import org.n3r.idworker.WorkerIdStrategy;
import org.n3r.idworker.utils.HttpReq;
import org.n3r.idworker.utils.Ip;
import org.n3r.idworker.utils.Props;
import org.n3r.idworker.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

public class DefaultWorkerIdStrategy implements WorkerIdStrategy {
    public static final WorkerIdStrategy instance = new DefaultWorkerIdStrategy();
    static long workerIdBits = 10L;
    static long maxWorkerId = ~(-1L << workerIdBits); // 1023
    static Random random = new SecureRandom();
    private final Properties props =
            Props.tryProperties("idworker-client.properties", Utils.DOT_ID_WORKERS);
    private final String idWorkerServerUrl =
            props.getProperty("server.address", "http://id.worker.server:18001");
    String userName = System.getProperty("user.name");


    String ipDotUsername = Ip.ip + "." + userName;
    String ipDotLock = ipDotUsername + ".lock.";
    int workerIdIndex = ipDotLock.length();
    long workerId;
    FileLock fileLock;

    Logger logger = LoggerFactory.getLogger(DefaultWorkerIdStrategy.class);
    private boolean inited;

    private void init() {
        workerId = findAvailWorkerId();
        if (workerId >= 0) {
            destroyFileLockWhenShutdown();
            startSyncThread();
        } else {
            syncWithWorkerIdServer();
            workerId = findAvailWorkerId();
            if (workerId < 0) workerId = increaseWithWorkerIdServer();
        }

        if (workerId < 0) workerId = tryToCreateOnIp();
        if (workerId < 0) {
            logger.warn("DANGEROUS!!! Try to use random worker id.");
            workerId = tryToRandomOnIp(); // Try avoiding! it could cause duplicated
        }

        if (workerId < 0) {
            logger.warn("the world may be ended!");
            throw new RuntimeException("the world may be ended");
        }
    }

    private long tryToRandomOnIp() {
        long avaiWorkerId = -1L;
        long tryTimes = -1;

        while (avaiWorkerId < 0 && ++tryTimes < maxWorkerId) {
            long wid = Ip.lip & random.nextInt((int) maxWorkerId);

            avaiWorkerId = checkAvail(wid);
        }
        return avaiWorkerId;
    }

    private long tryToCreateOnIp() {
        long wid = Ip.lip & maxWorkerId;

        return checkAvail(wid);
    }

    private long increaseWithWorkerIdServer() {
        String incId = HttpReq.get(idWorkerServerUrl)
                .req("/inc")
                .param("ipu", ipDotUsername)
                .exec();
        if (incId == null || incId.trim().isEmpty()) return -1L;

        long lid = Long.parseLong(incId);

        return checkAvail(lid);
    }

    private long checkAvail(long lid) {
        long availWorkerId = -1L;
        try {
            File idWorkerHome = Utils.createIdWorkerHome();
            new File(idWorkerHome, ipDotLock + String.format("%04d", lid)).createNewFile();
            availWorkerId = findAvailWorkerId();
        } catch (IOException e) {
            logger.warn("checkAvail error", e);
        }

        return availWorkerId;
    }

    private void startSyncThread() {
        new Thread() {
            @Override
            public void run() {
                syncWithWorkerIdServer();
            }
        }.start();
    }

    private void syncWithWorkerIdServer() {
        String syncIds = HttpReq.get(idWorkerServerUrl)
                .req("/sync")
                .param("ipu", ipDotUsername)
                .param("ids", buildWorkerIdsOfCurrentIp())
                .exec();
        if (syncIds == null || syncIds.trim().isEmpty()) return;

        String[] syncIdsArr = syncIds.split(",");
        File idWorkerHome = Utils.createIdWorkerHome();
        for (String syncId : syncIdsArr) {
            try {
                new File(idWorkerHome, ipDotLock + syncId).createNewFile();
            } catch (IOException e) {
                logger.warn("create workerid lock file error", e);
            }
        }
    }

    private String buildWorkerIdsOfCurrentIp() {
        StringBuilder sb = new StringBuilder();
        File idWorkerHome = Utils.createIdWorkerHome();
        for (File lockFile : Objects.requireNonNull(idWorkerHome.listFiles())) {
            if (!lockFile.getName().startsWith(ipDotLock)) continue;

            String workerId = lockFile.getName().substring(workerIdIndex);
            if (!workerId.matches("\\d\\d\\d\\d")) continue;

            if (sb.length() > 0) sb.append(',');
            sb.append(workerId);
        }

        return sb.toString();
    }

    private void destroyFileLockWhenShutdown() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> fileLock.destroy()));
    }

    private long findAvailWorkerId() {
        File idWorkerHome = Utils.createIdWorkerHome();

        for (File lockFile : Objects.requireNonNull(idWorkerHome.listFiles())) {
            // check the format like 192.168.0.1.lock.0001
            // 192.168.0.1.myServer.lock.0001
            if (!lockFile.getName().startsWith(ipDotLock)) continue;

            String workerId = lockFile.getName().substring(workerIdIndex);
            if (!workerId.matches("\\d\\d\\d\\d")) continue;

            FileLock fileLock = new FileLock(lockFile);
            if (!fileLock.tryLock()) {
                fileLock.destroy();
                continue;
            }

            this.fileLock = fileLock;
            return Long.parseLong(workerId);
        }

        return -1;
    }

    @Override
    public void initialize() {
        if (inited) return;
        init();
        this.inited = true;
    }

    @Override
    public long availableWorkerId() {
        return workerId;
    }

    @Override
    public void release() {
        if (fileLock != null) fileLock.destroy();
        inited = false;
    }
}
