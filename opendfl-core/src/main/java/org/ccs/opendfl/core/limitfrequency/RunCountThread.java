package org.ccs.opendfl.core.limitfrequency;

import org.ccs.opendfl.core.biz.IOutLimitCountBiz;
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
    private final IOutLimitCountBiz outLimitCountBiz;

    public RunCountThread(IRunCountBiz runCountBiz, IOutLimitCountBiz outLimitCountBiz) {
        this.runCountBiz = runCountBiz;
        this.outLimitCountBiz = outLimitCountBiz;
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
                    //每个接口调用次数写到redis
                    runCountBiz.saveRunCount();
                    //每个接口调用超限次数写到redis
                    outLimitCountBiz.saveLimitCount();
                    logger.debug("----saveRunCountJob----runTime={}", System.currentTimeMillis()-time);
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