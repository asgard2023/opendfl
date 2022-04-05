package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.vo.FrequencyVo;

import java.util.Map;

/**
 * 黑名单，白名单管理
 *
 * @author chenjh
 */
public interface IWhiteBlackListBiz {
    /**
     * 全局黑名单
     * @return WhiteBlackConfigVo
     */
    WhiteBlackConfigVo getBlackConfig();

    /**
     * 全局白名单
     * @return WhiteBlackConfigVo
     */
    WhiteBlackConfigVo getWhiteConfig();

    /**
     * 功能用户白名单
     * @return Map<String, String>
     */
    Map<String, String> getWhiteCodeUsers();
}
