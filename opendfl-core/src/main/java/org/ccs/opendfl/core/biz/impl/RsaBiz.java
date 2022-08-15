package org.ccs.opendfl.core.biz.impl;

import org.ccs.opendfl.core.biz.IRsaBiz;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.utils.RSAUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class RsaBiz implements IRsaBiz {
    private final Logger log = LoggerFactory.getLogger(RsaBiz.class);
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, String> generateRSAKey(String clientId, String funcCode) {
        try {
            // 获取公钥和私钥
            Map<String, Object> keys = RSAUtils.getKeys();
            RSAPublicKey publicKey = (RSAPublicKey) keys.get("public");
            RSAPrivateKey privateKey = (RSAPrivateKey) keys.get("private");
            // 保存私钥到 redis，也可以保存到数据库
            redisTemplate.opsForValue().set(clientId, privateKey, 30, TimeUnit.SECONDS);
            // 将公钥传到前端
            Map<String, String> map = new HashMap<>(8);
            // 注意返回modulus和exponent以16为基数的BigInteger的字符串表示形式
            map.put("modulus", publicKey.getModulus().toString(16));
            map.put("exponent", publicKey.getPublicExponent().toString(16));
            return map;
        } catch (Exception e) {
            log.error("----generateRSAKey--clientId={}", clientId, e);
            throw new FailedException("生成密钥失败:"+e.getMessage());
        }
    }

    @Override
    public String checkRSAKey(String clientId, String password) {
        return RSAUtils.checkRSAKey(redisTemplate, clientId, password);
    }
}
