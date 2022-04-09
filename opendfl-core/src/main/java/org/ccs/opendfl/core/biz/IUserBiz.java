package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.NeedLoginException;
import org.ccs.opendfl.core.utils.StringUtils;

/**
 * 用户管理，预留
 * 用于用户处理
 *
 * @author chenjh
 */
public interface IUserBiz {
    /**
     * 用户编码与用户Id转换
     *
     * @param userCode 用户编码
     * @return String 用户ID
     */
    default String getUserId(String userCode) {
        return null;
    }

    /**
     * 检查用户登入
     * @param userId 登入的用户ID
     */
    default void checkUser(String userId) {
        if (StringUtils.isBlank(userId)) {
            BaseException exception= new NeedLoginException("userId is empty");
            exception.setTitle("limit:needLogin");
            throw exception;
        }
    }
}
