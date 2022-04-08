package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.exception.ParamNullException;

/**
 * 用户管理，预留
 * 用于用户处理
 *
 * @author chenjh
 */
public interface IUserBiz {
    /**
     * 用户编码与用户Id转换
     * @param userCode
     * @return
     */
    default String getUserId(String userCode){
        return null;
    }

    default void checkUser(String userId){
        if(userId==null){
            new ParamNullException("userId");
        }
    }
}
