package org.ccs.opendfl.core.limitfrequency;

import org.ccs.opendfl.core.biz.IRunCountBiz;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程任务管理
 *
 *  @author chenjh
 */
@Service
public class RunCountTask {
    static Logger logger = LoggerFactory.getLogger(RunCountTask.class);
    private Thread cacheThread = null;
    private RunCountThread runCountThread = null;
    private AtomicInteger aCounter = new AtomicInteger();
    private boolean isLog = true;
    @Autowired
    private IRunCountBiz runCountBiz;

    @PostConstruct
    public void init() {
        if (runCountThread == null) {
            runCountThread = new RunCountThread(runCountBiz);
        }
        if (cacheThread == null) {
            cacheThread = new Thread(runCountThread);
        }
        this.start();
    }

    public void start() {
        cacheThread.start();
    }

    public void notifyRun() {
        Thread.State state = cacheThread.getState();

        try {
            if (state == Thread.State.WAITING || state == Thread.State.TIMED_WAITING) {
                this.runCountThread.notifyRun();
            }
        } catch (Exception e) {
            logger.error("-----notifyRun--error={}", e.getMessage(), e);
        }
        if (isLog) {
            int count = aCounter.incrementAndGet();
            if (count < 30 || count % 20 == 0) {
                logger.debug("----notifyRun--count={} state={}/{}", count, state, cacheThread.getState());
                if (count > 1000) {
                    isLog = false;
                }
            }
        }
    }

}