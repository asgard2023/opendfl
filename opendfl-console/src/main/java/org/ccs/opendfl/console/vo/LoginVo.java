package org.ccs.opendfl.console.vo;

import lombok.Data;
import org.ccs.opendfl.console.config.vo.UserVo;

@Data
public class LoginVo {
    private String access_token;
    private UserVo user;
}
