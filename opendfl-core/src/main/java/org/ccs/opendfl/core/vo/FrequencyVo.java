package org.ccs.opendfl.core.vo;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.limitcount.Frequency;
import org.ccs.opendfl.core.limitcount.Frequency2;
import org.ccs.opendfl.core.limitcount.Frequency3;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.StringUtils;

import java.util.Objects;

@Data
public class FrequencyVo implements Cloneable {
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
    private Long createTime;
    public static final FrequencyVo instance=new FrequencyVo();

    /**
     * 用于提高创建对象的性能
     * @return
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
        vo.setLimitType("uriConfig");
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
        if(StringUtils.isBlank(vo.getAttrName())){
            vo.setAttrName(RequestParams.USER_ID);
        }
        return vo;
    }
    public static FrequencyVo toFrequencyVo(Frequency frequency, FrequencyVo vo){
        if(frequency==null){
            return null;
        }
        vo.setLimitType("frequency");
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
        if(StringUtils.isBlank(vo.getAttrName())){
            vo.setAttrName(RequestParams.USER_ID);
        }
        vo.sysconfig=frequency.sysconfig();
        return vo;
    }

    /**
     *
     * @param frequency
     * @param vo 复用已有对象
     * @return
     */
    public static FrequencyVo toFrequencyVo(Frequency2 frequency, FrequencyVo vo){
        if(frequency==null){
            return null;
        }
        vo.setLimitType("frequency2");
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
        if(StringUtils.isBlank(vo.getAttrName())){
            vo.setAttrName(RequestParams.USER_ID);
        }
        return vo;
    }

    /**
     *
     * @param frequency
     * @param vo 复用已有对象
     * @return
     */
    public static FrequencyVo toFrequencyVo(Frequency3 frequency, FrequencyVo vo){
        if(frequency==null){
            return null;
        }
        vo.setLimitType("frequency3");
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
        if(StringUtils.isBlank(vo.getAttrName())){
            vo.setAttrName(RequestParams.USER_ID);
        }
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
//                ", errMsg='" + errMsg + '\'' +
//                ", errMsgEn='" + errMsgEn + '\'' +
                ", sysconfig=" + sysconfig +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, time, limit, userIpCount, ipUserCount);
    }

    @Override
    public FrequencyVo clone(){
        FrequencyVo obj = null;
        try{
            obj = (FrequencyVo)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
