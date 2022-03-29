package org.ccs.opendfl.core.biz;

public interface IUserBiz {
    default String getUserId(String userCode){
        return null;
    }
}
