package org.ccs.opendfl.mysql.dfllogs.vo;

import lombok.Data;
import org.ccs.opendfl.mysql.dfllogs.po.DflLogUserPo;

import javax.persistence.Transient;
import java.util.Date;

@Data
public class DflOutLimitLogCountVo {
    private String uri;
    private Integer uriId;
    private String account;
    private String limitType;

    /**
     * 时间
     */
    private Integer timeSecond;

    /**
     * 限制数
     */
    private Integer limitCount;
    /**
     * 用户数
     */
    private Integer userCount;

    /**
     *
     */
    private Long uid;
    @Transient
    private DflLogUserPo user;

    private Integer rowCount;
    private Integer ipCount;
    private Integer reqCountMax;
    private Integer maxRunTime;
    private Date createTimeMax;
    private Date createTimeMin;
}
