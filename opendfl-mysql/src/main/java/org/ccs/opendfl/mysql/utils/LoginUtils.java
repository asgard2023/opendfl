package org.ccs.opendfl.mysql.utils;

import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserLoginBiz;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginUtils {
    private static IDflUserLoginBiz dflUserLoginBiz;
    @Autowired
    public void setDflUserLoginBiz(IDflUserLoginBiz dflUserLoginBiz){
        LoginUtils.dflUserLoginBiz = dflUserLoginBiz;
    }

    public static UserVo getUserByToken(String token) {
        return dflUserLoginBiz.getUserByToken(token);
    }


}
