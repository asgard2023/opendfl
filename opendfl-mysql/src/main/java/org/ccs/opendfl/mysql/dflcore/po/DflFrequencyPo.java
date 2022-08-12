package org.ccs.opendfl.mysql.dflcore.po;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import org.ccs.opendfl.core.config.vo.LimitUriConfigVo;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.utils.CommUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;

/**
 * @Version V1.0
 * @Title: DflFrequencyPo
 * @Description: 频率限制配置表 实体
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:43:11
 */
@Data
@Table(name = "dfl_frequency")
@XmlRootElement(name = "dflFrequency")
@JsonInclude(Include.ALWAYS)
public class DflFrequencyPo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static LimitUriConfigVo toConfigVo(DflFrequencyPo frequencyPo) {
        LimitUriConfigVo uriConfigVo = new LimitUriConfigVo();
        uriConfigVo.setStatus(frequencyPo.getStatus());
        uriConfigVo.setUri(frequencyPo.getUri());
        uriConfigVo.setTime(frequencyPo.getTime());
        uriConfigVo.setLimit(frequencyPo.getLimitCount());
        uriConfigVo.setAliasName(frequencyPo.getAlias());
        uriConfigVo.setAttrName(frequencyPo.getAttrName());
        uriConfigVo.setWhiteCode(frequencyPo.getWhiteCode());
        uriConfigVo.setFreqLimitType(FreqLimitType.parse(frequencyPo.getFreqLimitType()));
        uriConfigVo.setErrMsg(frequencyPo.getErrMsg());
        uriConfigVo.setErrMsgEn(frequencyPo.getErrMsgEn());
        uriConfigVo.setMethod(frequencyPo.getMethod());

        if (uriConfigVo.getTime() == null) {
            uriConfigVo.setTime(0);
        }
        if (uriConfigVo.getLimit() == null) {
            uriConfigVo.setLimit(0);
        }
        uriConfigVo.setErrMsg((String) CommUtils.nvl(uriConfigVo.getErrMsg(), ""));
        uriConfigVo.setErrMsgEn((String) CommUtils.nvl(uriConfigVo.getErrMsgEn(), ""));
        return uriConfigVo;
    }

    /**
     *
     */
    @Id
    @Column(name = "id")
    private Integer id;

    /**
     *
     */
    @Column(name = "uri")
    private String uri;

    @Column(name = "method")
    private String method;

    /**
     *
     */
    @Column(name = "uri_id")
    private Integer uriId;

    /**
     * 编码
     */
    @Column(name = "code")
    private String code;

    /**
     * 名称
     */
    @Column(name = "name")
    private String name;

    /**
     * 别名，用于多个接口共用一个限制
     */
    @Column(name = "alias")
    private String alias;

    /**
     * 时间间隔
     */
    @Column(name = "time")
    private Integer time;

    /**
     * 限制次数
     */
    @Column(name = "limit_count")
    private Integer limitCount;

    /**
     * 一个用户允许IP个数
     */
    @Column(name = "freq_limit_type")
    private Integer freqLimitType;

    /**
     * 白名单编码
     */
    @Column(name = "white_code")
    private String whiteCode;

    /**
     * 限制类型
     */
    @Column(name = "limit_type")
    private String limitType;

    /**
     * 限制属性名
     */
    @Column(name = "attr_name")
    private String attrName;

    /**
     * 是否需要登入
     */
    @Column(name = "need_login")
    private Integer needLogin;

    /**
     * 异常消息
     */
    @Column(name = "err_msg")
    private String errMsg;

    /**
     * 异常消息en
     */
    @Column(name = "err_msg_en")
    private String errMsgEn;

    /**
     * 是否删除
     */
    @Column(name = "if_del")
    private Integer ifDel;

    /**
     * 状态:是否有效0无效，1有效
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "modify_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifyTime;

    /**
     * 创建人
     */
    @Column(name = "create_user")
    private Integer createUser;

    /**
     * 修改人
     */
    @Column(name = "modify_user")
    private Integer modifyUser;

    private String remark;

}