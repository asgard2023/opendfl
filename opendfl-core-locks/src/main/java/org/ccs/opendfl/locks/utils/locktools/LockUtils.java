package org.ccs.opendfl.locks.utils.locktools;

import org.ccs.opendfl.locks.config.RequestLockConfiguration;
import org.ccs.opendfl.core.constants.DataSourceType;
import org.ccs.opendfl.core.limitlock.RequestLock;
import org.ccs.opendfl.core.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 锁工具类
 * @author chenjh
 */
@Component
public class LockUtils {
    private static RequestLockConfiguration requestLockConfiguration;

    @Autowired
    public void setRequestLockConfiguration(RequestLockConfiguration requestLockConfiguration) {
        LockUtils.requestLockConfiguration = requestLockConfiguration;
    }

    public static String getLockKey(RequestLock reqLimit, String dataId) {
        if (StringUtils.equals(DataSourceType.REDIS.getType(), reqLimit.lockType().getSource())) {
            return getLockRedisKey(reqLimit.name(), dataId);
        }
        //zk,etcd用path模式
        return "/" + requestLockConfiguration.getRedisPrefix() + "/" + reqLimit.lockType().getType() + "/" + reqLimit.name() + "/" + dataId;
    }

    public static String getLockRedisKey(String name, String dataId) {
        return requestLockConfiguration.getRedisPrefix() + ":" + name + ":" + dataId;
    }
}
