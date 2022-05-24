package org.ccs.opendfl.mysql.core.biz;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IFrequencyConfigBiz;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.constants.FrequencyLimitType;
import org.ccs.opendfl.core.utils.CommUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.ccs.opendfl.core.vo.RequestVo;
import org.ccs.opendfl.mysql.constant.CommonStatus;
import org.ccs.opendfl.mysql.core.vo.FrequencyMysqlVo;
import org.ccs.opendfl.mysql.dflcore.biz.IDflFrequencyBiz;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 频率限制配置查询-redis
 * 缓存10秒
 * 如果数据无效，将不起作用，使用原来注解上的值作为默认，如果是uri配置没有注解，则没有限制
 *
 * @author chenjh
 */
@Service(value = "frequencyConfigMysqlBiz")
@Slf4j
public class FrequencyConfigMysqlBiz implements IFrequencyConfigBiz {
    @Autowired
    private IDflFrequencyBiz dflFrequencyBiz;

    /**
     * 有修改日志一下
     *
     * @param frequency FrequencyVo
     */
    private boolean checkChange(FrequencyVo frequencyExist, FrequencyVo frequency) {
        boolean isChanged = false;
        if (frequencyExist == null || frequencyExist.hashCode() != frequency.hashCode()) {
            isChanged = true;
        }
        if (isChanged) {
            log.info("----checkChange--name={} time={} limit={} ipUser={} userIp={} errorMsg={}"
                    , frequency.getName(), frequency.getTime(), frequency.getLimit(), frequency.getIpUserCount(), frequency.getUserIpCount(), frequency.getErrMsg());
        }
        return isChanged;
    }

    /**
     * 主要按接口缓存，理论上接口数不会太多，用Map做持久缓存，不会占太多内存
     */
    private static Map<String, Long> loadSysconfigTimeMap = new ConcurrentHashMap<>(128);
    private static Map<String, FrequencyMysqlVo> sysconfigLimitMap = new ConcurrentHashMap<>(64);
    private static Map<String, List<LimitUriConfigVo>> uriLimitConfigsMap = new ConcurrentHashMap<>(128);

    @Override
    public void limitBySysconfigLoad(FrequencyVo frequency, Long curTime) {
        //只支持注解模式，uriConfig模式不处理
        if (StringUtils.equals(FrequencyLimitType.URI_CONFIG.getType(), frequency.getLimitType())) {
            return;
        }
        String key = frequency.getName() + ":" + frequency.getTime();
        Long time = loadSysconfigTimeMap.get(key);
        FrequencyMysqlVo frequencyExist = sysconfigLimitMap.get(key);
        //缓存10秒，10秒加载一次
        if (time == null || curTime - time > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            loadSysconfigTimeMap.put(key, curTime);
            FrequencyMysqlVo frequencyNew = limitBySysconfig(frequency);
            if (frequencyNew != null && checkChange(frequencyExist, frequencyNew)) {
                //因为FrequencyVo是可以共用的，存起来也可以发生变化，所以这里缓存时clone一下
                sysconfigLimitMap.put(key, frequencyNew);
            }
            frequencyExist = frequencyNew;
        }

        if (frequencyExist != null) {
            if(frequencyExist.getStatus()!=null && frequencyExist.getStatus()==CommonStatus.VALID.getStatus()){
                frequency.setLimit(frequencyExist.getLimit());
                frequency.setIpUserCount(frequencyExist.getIpUserCount());
                frequency.setUserIpCount(frequencyExist.getUserIpCount());
                frequency.setErrMsg(frequencyExist.getErrMsg());
                frequency.setErrMsgEn(frequencyExist.getErrMsgEn());
                frequency.setSysconfig(frequencyExist.isSysconfig());
            }
        }
    }

    /**
     * 次数限制读取系统参数
     *
     * @param frequency FrequencyVo
     */
    private FrequencyMysqlVo limitBySysconfig(FrequencyVo frequency) {
        String aliasName = null;

        if (!"".equals(frequency.getAliasName())) {
            aliasName = frequency.getAliasName();
        }
        String key = (String) CommUtils.nvl(aliasName, frequency.getName());
        DflFrequencyPo frequencyPo = dflFrequencyBiz.getFrequencyByCode(key, frequency.getTime());
        if (frequencyPo != null) {
            FrequencyMysqlVo frequencyMysql=FrequencyMysqlVo.copy(frequency);
            //如果数据状态无效，则用原来的默认值
            frequencyMysql.setLimit(frequencyPo.getLimitCount());
            frequencyMysql.setIpUserCount(frequencyPo.getIpUserCount());
            frequencyMysql.setUserIpCount(frequencyPo.getUserIpCount());
            frequencyMysql.setErrMsg(frequencyPo.getErrMsg());
            frequencyMysql.setErrMsgEn(frequencyPo.getErrMsgEn());
            frequencyMysql.setStatus(frequencyPo.getStatus());
            return frequencyMysql;

        } else {
            //首次加载时自动保存
            autoCreateFrequency(frequency);
        }

        return null;
    }

    private void autoCreateFrequency(FrequencyVo frequency) {
        DflFrequencyPo entity = new DflFrequencyPo();
        entity.setCode(frequency.getName());
        entity.setIpUserCount(frequency.getIpUserCount());
        entity.setUserIpCount(frequency.getUserIpCount());
        entity.setLimitCount(frequency.getLimit());
        entity.setTime(frequency.getTime());
        entity.setAlias(frequency.getAliasName());
        entity.setAttrName(frequency.getAttrName());
        entity.setErrMsg(frequency.getErrMsg());
        entity.setErrMsgEn(frequency.getErrMsgEn());
        entity.setLimitType(frequency.getLimitType());
        entity.setNeedLogin(0);
        if (frequency.isNeedLogin()) {
            entity.setNeedLogin(1);
        }
        entity.setWhiteCode(frequency.getWhiteCode());
        entity.setUri(frequency.getRequestUri());
        entity.setIfDel(0);
        entity.setStatus(1);
        this.dflFrequencyBiz.saveDflFrequency(entity);
    }

    private void autoCreateFrequencyByRequest(RequestVo requestVo, String requestUri) {
        DflFrequencyPo entity = new DflFrequencyPo();
        entity.setUri(requestUri);
        entity.setMethod(requestVo.getMethod());
        entity.setLimitType(FrequencyLimitType.URI_CONFIG.getType());
        entity.setIfDel(0);
        entity.setStatus(1);
        entity.setNeedLogin(0);
        this.dflFrequencyBiz.saveDflFrequency(entity);
    }

    /**
     * 次数限制读取系统参数
     *
     * @param requestVo FrequencyVo
     */
    @Override
    public void limitBySysconfigUri(RequestVo requestVo) {
        String requestUri = requestVo.getRequestUri();
//        if(StringUtils.equals("/error", requestUri)){
//            requestVo.setLimitRequests(Collections.emptyList());
//            return;
//        }
        Long curTime = System.currentTimeMillis();
        String key = "uri:" + requestVo.getRequestUri();
        String keyLoad = "uriLoad:" + requestVo.getRequestUri();
        Long time = loadSysconfigTimeMap.get(key);
        List<LimitUriConfigVo> list = uriLimitConfigsMap.get(requestUri);
        if (time == null || curTime - time > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            loadSysconfigTimeMap.put(key, curTime);
            Long loadTime = loadSysconfigTimeMap.get(keyLoad);
            Long maxUpdateTime = dflFrequencyBiz.getFrequencyByUriMaxUpdateTime(requestUri);
            if (maxUpdateTime == null) {
                maxUpdateTime = 0L;
            }
            loadSysconfigTimeMap.put(key, curTime);
            //检查数据是否有变化，如果有变化才重新加载到内存
            if (loadTime == null || (loadTime.longValue() != maxUpdateTime.longValue())) {
                log.info("-----limitBySysconfigUri--uri={} maxUpdateTime={} reload config by modify_time changed loadTime={}", requestUri, maxUpdateTime, System.currentTimeMillis() - curTime);
                loadSysconfigTimeMap.put(keyLoad, maxUpdateTime);
                list = getLimitUriConfigVos(requestVo, requestUri);
                uriLimitConfigsMap.put(requestUri, list);
            }
        }
        if (list == null) {
            list = Collections.emptyList();
        }
        requestVo.setLimitRequests(list);
    }

    private List<LimitUriConfigVo> getLimitUriConfigVos(RequestVo requestVo, String requestUri) {
        List<DflFrequencyPo> dflFrequencyPoList = dflFrequencyBiz.getFrequencyByUri(requestUri);
        if (CollectionUtils.isEmpty(dflFrequencyPoList)) {
            //首次加载时自动保存
            autoCreateFrequencyByRequest(requestVo, requestUri);
            return Collections.emptyList();
        }
        List<LimitUriConfigVo> list = new ArrayList<>();
        for (DflFrequencyPo dflFrequencyPo : dflFrequencyPoList) {
            //如果数据状态无效，该项不起作用
            if (dflFrequencyPo.getStatus() != CommonStatus.VALID.getStatus()) {
                continue;
            }
            if (dflFrequencyPo.getTime() == null || dflFrequencyPo.getTime() == 0) {
                continue;
            }
            boolean isSameMethod = StringUtils.equals(dflFrequencyPo.getMethod(), requestVo.getMethod());
            boolean isEmptyMethod = StringUtils.isBlank(dflFrequencyPo.getMethod());
            //uriConfig支持请求方法,如GET/POST
            if (isEmptyMethod || !isEmptyMethod && isSameMethod) {
                LimitUriConfigVo uriConfigVo = DflFrequencyPo.toConfigVo(dflFrequencyPo);
                list.add(uriConfigVo);
            }
        }
        return list;
    }


}
