package org.ccs.opendfl.core.vo;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.constants.FrequencyType;
import org.ccs.opendfl.core.limitfrequency.*;
import org.springframework.beans.BeanUtils;

import java.util.Objects;

@Data
public class FrequencyVo {
    private String name;
    private String aliasName;
    private String requestUri;
    private String limitType;
    private int time=0;
    private int limit=0;
    private int userIpCount=0;
    private int ipUserCount=0;
    private String attrName;
    private String whiteCode;
    private String errMsg;
    private String errMsgEn;
    private boolean sysconfig;
    private boolean resource;
    private boolean needLogin;
    private Long createTime;
    public static final FrequencyVo instance=new FrequencyVo();

    /**
     * 用于提高创建对象的性能
     * @return 得先用户缓存队解（instance）
     */
    public static FrequencyVo newInstance(){
        return new FrequencyVo();
    }
    public static FrequencyVo toVo(String name, int time){
        FrequencyVo vo=new FrequencyVo();
        vo.setName(name);
        vo.setTime(time);
        return vo;
    }
    public static FrequencyVo toFrequencyVo(FrequencyVo frequency, LimitUriConfigVo uriConfigVo){
        FrequencyVo vo=frequency;
        if(frequency==null){
            vo=FrequencyVo.newInstance();
        }
        vo.setLimitType(FrequencyType.URI_CONFIG.getType());
        vo.name= uriConfigVo.getUri();
        vo.aliasName= uriConfigVo.getAliasName();
        vo.time= uriConfigVo.getTime();
        vo.limit= uriConfigVo.getLimit();
        vo.ipUserCount= uriConfigVo.getIpUser();
        vo.userIpCount= uriConfigVo.getUserIp();
        vo.errMsg= uriConfigVo.getErrMsg();
        vo.errMsgEn= uriConfigVo.getErrMsgEn();
        vo.whiteCode= uriConfigVo.getWhiteCode();
        vo.attrName= uriConfigVo.getAttrName();
        vo.resource=uriConfigVo.isResource();
        return vo;
    }
    public static FrequencyVo toFrequencyVo(Frequency frequency, FrequencyVo vo){
        if(frequency==null){
            return null;
        }
        vo.setLimitType(FrequencyType.FREQUENCY.getType());
        vo.name=frequency.name();
        vo.aliasName=frequency.aliasName();
        vo.time=frequency.time();
        vo.limit=frequency.limit();
        vo.ipUserCount= frequency.ipUserCount();
        vo.userIpCount= frequency.userIpCount();
        vo.errMsg=frequency.errMsg();
        vo.errMsgEn=frequency.errMsgEn();
        vo.whiteCode=frequency.whiteCode();
        vo.attrName=frequency.attrName();
        vo.sysconfig=frequency.sysconfig();
        vo.needLogin= frequency.needLogin();
        vo.resource=frequency.resource();
        return vo;
    }

    /**
     *
     * @param frequency 频率限制
     * @param vo 复用已有对象
     * @return 转vo对应
     */
    public static FrequencyVo toFrequencyVo(Frequency2 frequency, FrequencyVo vo){
        if(frequency==null){
            return null;
        }
        vo.setLimitType(FrequencyType.FREQUENCY2.getType());
        vo.name=frequency.name();
        vo.aliasName=frequency.aliasName();
        vo.time=frequency.time();
        vo.limit=frequency.limit();
        vo.ipUserCount= frequency.ipUserCount();
        vo.userIpCount= frequency.userIpCount();
        vo.errMsg=frequency.errMsg();
        vo.errMsgEn=frequency.errMsgEn();
        vo.whiteCode=frequency.whiteCode();
        vo.attrName=frequency.attrName();
        vo.sysconfig=frequency.sysconfig();
        vo.resource=frequency.resource();
        vo.needLogin= frequency.needLogin();
        return vo;
    }

    /**
     *
     * @param frequency 频率限制
     * @param vo 复用已有对象
     * @return 转vo对应
     */
    public static FrequencyVo toFrequencyVo(Frequency3 frequency, FrequencyVo vo){
        if(frequency==null){
            return null;
        }
        vo.setLimitType(FrequencyType.FREQUENCY3.getType());
        vo.name=frequency.name();
        vo.aliasName=frequency.aliasName();
        vo.time=frequency.time();
        vo.limit=frequency.limit();
        vo.ipUserCount= frequency.ipUserCount();
        vo.userIpCount= frequency.userIpCount();
        vo.errMsg=frequency.errMsg();
        vo.errMsgEn=frequency.errMsgEn();
        vo.whiteCode=frequency.whiteCode();
        vo.attrName=frequency.attrName();
        vo.sysconfig=frequency.sysconfig();
        vo.resource=frequency.resource();
        vo.needLogin= frequency.needLogin();
        return vo;
    }

    /**
     *
     * @param frequency 频率限制
     * @param vo 复用已有对象
     * @return 转vo对应
     */
    public static FrequencyVo toFrequencyVo(Frequency4 frequency, FrequencyVo vo){
        if(frequency==null){
            return null;
        }
        vo.setLimitType(FrequencyType.FREQUENCY3.getType());
        vo.name=frequency.name();
        vo.aliasName=frequency.aliasName();
        vo.time=frequency.time();
        vo.limit=frequency.limit();
        vo.ipUserCount= frequency.ipUserCount();
        vo.userIpCount= frequency.userIpCount();
        vo.errMsg=frequency.errMsg();
        vo.errMsgEn=frequency.errMsgEn();
        vo.whiteCode=frequency.whiteCode();
        vo.attrName=frequency.attrName();
        vo.sysconfig=frequency.sysconfig();
        vo.resource=frequency.resource();
        vo.needLogin= frequency.needLogin();
        return vo;
    }

    /**
     *
     * @param frequency 频率限制
     * @param vo 复用已有对象
     * @return 转vo对应
     */
    public static FrequencyVo toFrequencyVo(Frequency5 frequency, FrequencyVo vo){
        if(frequency==null){
            return null;
        }
        vo.setLimitType(FrequencyType.FREQUENCY3.getType());
        vo.name=frequency.name();
        vo.aliasName=frequency.aliasName();
        vo.time=frequency.time();
        vo.limit=frequency.limit();
        vo.ipUserCount= frequency.ipUserCount();
        vo.userIpCount= frequency.userIpCount();
        vo.errMsg=frequency.errMsg();
        vo.errMsgEn=frequency.errMsgEn();
        vo.whiteCode=frequency.whiteCode();
        vo.attrName=frequency.attrName();
        vo.sysconfig=frequency.sysconfig();
        vo.resource=frequency.resource();
        vo.needLogin= frequency.needLogin();
        return vo;
    }


    @Override
    public String toString() {
        return "FrequencyVo{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", limit=" + limit +
                ", userIpCount=" + userIpCount +
                ", ipUserCount=" + ipUserCount +
//                ", pageReachLog=" + pageReachLog +
//                ", freqAttrName='" + freqAttrName + '\'' +
                ", whiteCode=" + whiteCode +
                ", errMsg='" + errMsg + '\'' +
                ", errMsgEn='" + errMsgEn + '\'' +
                ", resource=" + resource +
                ", sysconfig=" + sysconfig +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, time, limit, userIpCount, ipUserCount, errMsg, errMsgEn);
    }


    public FrequencyVo toCopy(){
        FrequencyVo obj = new FrequencyVo();
        BeanUtils.copyProperties(this, obj);
        return obj;
    }
}
