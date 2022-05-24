package org.ccs.opendfl.mysql.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVo implements Cloneable, Serializable {
    private Integer id;
    private String nickname;
    private String username;
    private String pwd;
    private String role;

    @Override
    public UserVo clone() {
        UserVo obj = null;
        try {
            obj = (UserVo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return obj;
    }
}
