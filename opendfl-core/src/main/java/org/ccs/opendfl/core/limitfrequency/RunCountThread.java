package org.ccs.opendfl.core.limitfrequency;

import org.ccs.opendfl.core.biz.IRunCountBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行时间同步到redis
 *
 * @author chenjh
 */
public class RunCountThread implements Runnable {
    static Logger logger = LoggerFactory.getLogger(RunCountThread.class);
    private volatile static long saveTime = 0L;
    private Object lock = new Object();
    private final IRunCountBiz runCountBiz;

    public RunCountThread(IRunCountBiz runCountBiz) {
        this.runCountBiz = runCountBiz;
    }

    /**
     * 把runTime数据写到redis
     */
    public void saveRunCountJob() {
        Long time = System.currentTimeMillis();
        if (time - saveTime > 10000) {
            synchronized (lock) {
                saveTime = time;
                try {
                    logger.debug("----saveRunCountJob----");
                    runCountBiz.saveRunCount();
                    lock.wait();
                } catch (InterruptedException e) {
                    logger.error("----saveRunCountJob error={}", e.getMessage(), e);
                }
            }
        }
    }

    public void notifyRun() {
        synchronized (lock) {
            this.lock.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                saveRunCountJob();
            } catch (Exception e) {
                logger.error("----saveRunCountJob error={}", e.getMessage(), e);
            }
        }
    }
}