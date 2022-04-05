package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.core.vo.FrequencyVo;

/**
 * 黑白名单检查
 *
 * @author chenjh
 */
public interface IWhiteBlackCheckBiz {

    /**
     * 功能白名单检查
     *
     * @param frequency FrequencyVo
     * @param curTime   currentTime
     * @param userId    userId
     * @return boolean
     */
    boolean checkWhiteUserId(FrequencyVo frequency, Long curTime, String userId);

    /**
     * 检查userId是否在whiteUserIds里
     * @param userId       userId
     * @param whiteUserIds whiteUserIds
     * @return boolean
     */
    boolean isIncludeId(String userId, String whiteUserIds);

    /**
     * 全局白名单检查
     *
     * @param id        userId or ip
     * @param checkType WhiteBlackCheckType
     * @return boolean
     */
    boolean isIncludeWhiteId(String id, WhiteBlackCheckType checkType);

    /**
     * 全局黑名单检查
     *
     * @param id        userId or ip
     * @param checkType WhiteBlackCheckType
     * @return boolean
     */
    boolean isIncludeBlackId(String id, WhiteBlackCheckType checkType);
}
