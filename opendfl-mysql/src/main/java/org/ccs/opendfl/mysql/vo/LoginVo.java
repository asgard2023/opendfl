package org.ccs.opendfl.mysql.vo;

import lombok.Data;

@Data
public class LoginVo {
    private String access_token;
    private UserVo user;
    private Integer tokenExpireTime;
}
