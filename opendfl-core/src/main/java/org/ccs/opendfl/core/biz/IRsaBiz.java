package org.ccs.opendfl.core.biz;

import java.util.Map;

/**
 * 用于参数加密处理
 *
 * @author chenjh
 */
public interface IRsaBiz {
    /**
     * 生成密钥
     *
     * @param clientId 用户唯一性id,sessionId,手机或邮箱等
     * @param funcCode 功能编码
     * @return rsa信息
     */
    Map<String, String> generateRSAKey(String clientId, String funcCode);

    /**
     * 解密
     *
     * @param clientId 用户唯一性id,sessionId,手机或邮箱等
     * @param password 用户密码
     * @return String 解密密码
     */
    String checkRSAKey(String clientId, String password);
}
