package org.ccs.opendfl.mysql.core.vo;

import lombok.Data;
import org.ccs.opendfl.locks.config.vo.RequestLockConfigVo;

@Data
public class RequestLockConfigMysqlVo extends RequestLockConfigVo {
    private Integer status;
}
