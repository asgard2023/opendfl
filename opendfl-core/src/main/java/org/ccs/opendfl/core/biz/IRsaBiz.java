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
     * @param clientId
     * @param funcCode
     * @return
     */
    Map<String, String> generateRSAKey(String clientId, String funcCode);

    /**
     * 解密
     *
     * @param clientId
     * @param password
     * @return
     */
    String checkRSAKey(String clientId, String password);
}
