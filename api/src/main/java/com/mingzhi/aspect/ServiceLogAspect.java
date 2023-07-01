package com.mingzhi.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLogAspect {
    public static final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around("execution(* com.mingzhi.service.impl..*.*(..))")
    public Object recordTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("START=== {}.{}===",
                joinPoint.getTarget().getClass(),
                joinPoint.getSignature().getName()
        );
        long begin = System.currentTimeMillis();
        Object rst = joinPoint.proceed();
        long end = System.currentTimeMillis();
        long takeTime = end - begin;
        if (takeTime > 3000) {
            logger.error("End === useTime:{}ms ===", takeTime);
        }
        if (takeTime > 2000) {
            logger.warn("End === useTime:{}ms ===", takeTime);
        } else {
            logger.info("End === useTime:{}ms ===", takeTime);
        }
        return rst;
    }
}
