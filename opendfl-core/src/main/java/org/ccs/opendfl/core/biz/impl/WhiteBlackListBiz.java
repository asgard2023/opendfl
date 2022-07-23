package org.ccs.opendfl.core.biz.impl;

import org.ccs.opendfl.core.biz.IWhiteBlackListBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 黑白名单管理
 *
 * @author chenjh
 */
@Service(value = "whiteBlackListBiz")
public class WhiteBlackListBiz implements IWhiteBlackListBiz {

    @Override
    public void loadInit(){

    }

    @Autowired
    private FrequencyConfiguration frequencyConfiguration;


    /**
     * 全局黑名单
     *
     * @return 黑名单配置
     */
    @Override
    public WhiteBlackConfigVo getBlackConfig() {
        return frequencyConfiguration.getBlack();
    }

    /**
     * 全局白名单
     *
     * @return 白名单配置
     */
    @Override
    public WhiteBlackConfigVo getWhiteConfig() {
        return frequencyConfiguration.getWhite();
    }

    /**
     * 功能用户白名单
     *
     * @return 白名单用户
     */
    @Override
    public Map<String, String> getWhiteCodeUsers() {
        return this.frequencyConfiguration.getWhiteCodeUsers();
    }


}
