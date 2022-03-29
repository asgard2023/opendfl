package org.ccs.opendfl.core.biz;

import java.util.Map;

public interface IRsaBiz {
    /**
     * 生成密钥
     * @param clientId
     * @param funcCode
     * @return
     */
    public Map<String, String> generateRSAKey(String clientId, String funcCode);

    /**
     * 解密
     * @param clientId
     * @param password
     * @return
     */
    public String checkRSAKey(String clientId, String password);
}
