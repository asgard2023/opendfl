package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.vo.FrequencyVo;

/**
 * 黑名单，白名单管理
 *
 * @author chenjh
 */
public interface IWhiteBlackListBiz {
    WhiteBlackConfigVo getBlackConfig();

    WhiteBlackConfigVo getWhiteConfig();

    boolean checkWhiteUserId(FrequencyVo frequency, Long curTime, String userId);

    boolean isIncludeId(String userId, String whiteUserIds);
}
