package org.ccs.opendfl.core.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IRequestLockConfigBiz;
import org.ccs.opendfl.core.config.RequestLockConfiguration;
import org.ccs.opendfl.core.config.vo.RequestLockConfigVo;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式锁配置管理
 *
 * @author chenjh
 */
@Service("requestLockConfigBiz")
@Slf4j
public class RequestLockConfigBiz implements IRequestLockConfigBiz {
    @Autowired
    private RequestLockConfiguration requestLockConfiguration;

    private static final Map<String, Long> loadSysconfigTimeMap = new ConcurrentHashMap<>();
    private static final Map<String, RequestLockConfigVo> sysconfigLimitMap = new ConcurrentHashMap<>();


    /**
     * 10秒一次，从application-requestlock.yml配置文件读取配置
     *
     * @param requestLockVo RequestLockVo
     * @param curTime       Long
     */
    @Override
    public void loadLockConfig(RequestLockVo requestLockVo, Long curTime) {
        String key = requestLockVo.getName();
        RequestLockConfigVo lockConfigVo = sysconfigLimitMap.get(key);
        //缓存10秒刷一次，以免cloud模式配置有变更
        Long time = loadSysconfigTimeMap.get(key);
        if (time == null || curTime - time > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            loadSysconfigTimeMap.put(key, curTime);
            List<RequestLockConfigVo> lockConfigVos = requestLockConfiguration.getLockConfigs();
            if (lockConfigVos == null) {
                return;
            }
            for (RequestLockConfigVo lockConfig : lockConfigVos) {
                if (StringUtils.equals(requestLockVo.getName(), lockConfig.getName())) {
                    lockConfigVo = lockConfig;
                    sysconfigLimitMap.put(key, lockConfigVo);
                }
            }
        }
        if (lockConfigVo != null) {
            requestLockVo.setSysconfig(true);
            if (StringUtils.isNotBlank(lockConfigVo.getAttrName())) {
                requestLockVo.setAttrName(lockConfigVo.getAttrName());
            }
            if (lockConfigVo.getTime() != null) {
                requestLockVo.setTime(lockConfigVo.getTime());
            }
        }
    }
}
