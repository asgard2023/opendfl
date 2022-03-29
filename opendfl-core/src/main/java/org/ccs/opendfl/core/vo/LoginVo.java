package org.ccs.opendfl.core.vo;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.UserVo;

@Data
public class LoginVo {
    private String access_token;
    private UserVo user;
}
