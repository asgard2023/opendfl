package org.ccs.opendfl.mysql.core.biz;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.locks.biz.IRequestLockConfigBiz;
import org.ccs.opendfl.locks.config.RequestLockConfiguration;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestLockVo;
import org.ccs.opendfl.mysql.constant.CommonStatus;
import org.ccs.opendfl.mysql.core.vo.RequestLockConfigMysqlVo;
import org.ccs.opendfl.mysql.dflcore.biz.IDflLocksBiz;
import org.ccs.opendfl.mysql.dflcore.po.DflLocksPo;
import org.ccs.opendfl.mysql.dflsystem.constant.SystemConfigCodes;
import org.ccs.opendfl.mysql.dflsystem.utils.SystemConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式锁配置管理
 * 本地缓存10秒
 * 如果数据无效，将不起作用，使用原来注解上的值作为默认
 *
 * @author chenjh
 */
@Service("requestLockConfigMysqlBiz")
@Slf4j
public class RequestLockConfigMysqlBiz implements IRequestLockConfigBiz {
    @Autowired
    private IDflLocksBiz dflLocksBiz;
    @Autowired
    private RequestLockConfiguration requestLockConfiguration;

    private static final Map<String, Long> loadSysconfigTimeMap = new ConcurrentHashMap<>();
    private static final Map<String, RequestLockConfigMysqlVo> sysconfigLimitMap = new ConcurrentHashMap<>();

    private static Long recoverLockConfigTime = 0L;

    /**
     * 把系统参数的配置覆盖到frequencyConfiguration的默认配置
     * 也做频率限制，以减少调用次数
     *
     * @param curTime
     */
    private void recoverFLockConfig(Long curTime) {
        if (curTime - recoverLockConfigTime > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            if(recoverLockConfigTime==0){
                log.info("-----recoverFLockConfig--");
            }
            recoverLockConfigTime = curTime;
            try {
                Integer ifActive= SystemConfig.getByCache(SystemConfigCodes.LOCK_IF_ACTIVE);
                requestLockConfiguration.setIfActive((""+ifActive).charAt(0));
                requestLockConfiguration.setRedisPrefix(SystemConfig.getByCache(SystemConfigCodes.LOCK_REDIS_PREFIX));
            } catch (Exception e) {
                log.warn("-----recoverFLockConfig--error={}", e.getMessage(), e);
            }
//        frequencyConfiguration.setRedisPrefix(SystemConfig.getByCache(SystemConfigCodes.FREQUENCY_REDIS_PREFIX));
        }
    }

    /**
     * 10秒一次，从application-requestlock.yml配置文件读取配置
     *
     * @param requestLockVo RequestLockVo
     * @param curTime       Long
     */
    @Override
    public void loadLockConfig(RequestLockVo requestLockVo, Long curTime) {
        String key = requestLockVo.getName();
        RequestLockConfigMysqlVo lockConfigVo = sysconfigLimitMap.get(key);
        //缓存10秒刷一次，以免cloud模式配置有变更
        Long time = loadSysconfigTimeMap.get(key);
        if (time == null || curTime - time > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            loadSysconfigTimeMap.put(key, curTime);
            recoverFLockConfig(curTime);
            DflLocksPo locksPo = dflLocksBiz.getLockByCode(requestLockVo.getName());
            if (locksPo != null) {
                lockConfigVo = new RequestLockConfigMysqlVo();
                lockConfigVo.setName(locksPo.getName());
                lockConfigVo.setTime(locksPo.getTime());
                lockConfigVo.setAttrName(locksPo.getAttrName());
                lockConfigVo.setErrMsg(locksPo.getErrMsg());
                lockConfigVo.setStatus(locksPo.getStatus());
                sysconfigLimitMap.put(key, lockConfigVo);
            } else {
                locksPo = new DflLocksPo();
                locksPo.setCode(requestLockVo.getName());
                locksPo.setTime(requestLockVo.getTime());
                locksPo.setAttrName(requestLockVo.getAttrName());
                locksPo.setLockType(requestLockVo.getLockType());
                locksPo.setErrMsg(requestLockVo.getErrMsg());
                locksPo.setUri(requestLockVo.getRequestUri());
                locksPo.setStatus(1);
                this.dflLocksBiz.saveDflLocks(locksPo);
            }
        }
        //如果数据状态无效，则用原来的默认值
        if (lockConfigVo != null && lockConfigVo.getStatus() == CommonStatus.VALID.getStatus()) {
            requestLockVo.setSysconfig(true);
            if (StringUtils.isNotBlank(lockConfigVo.getAttrName())) {
                requestLockVo.setAttrName(lockConfigVo.getAttrName());
            }
            if (lockConfigVo.getTime() != null) {
                requestLockVo.setTime(lockConfigVo.getTime());
            }
            requestLockVo.setErrMsg(lockConfigVo.getErrMsg());
        }
    }
}
