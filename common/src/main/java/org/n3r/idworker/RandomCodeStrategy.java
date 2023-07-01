package org.n3r.idworker;

public interface RandomCodeStrategy {
    void init();

    int prefix();

    Integer next();

    void release();

}
