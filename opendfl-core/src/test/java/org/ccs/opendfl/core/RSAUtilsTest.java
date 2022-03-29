package org.ccs.opendfl.core;

import org.ccs.opendfl.core.utils.RSAUtils;
import org.junit.jupiter.api.Test;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;

public class RSAUtilsTest {
    @Test
    public void testUuid(){
        String token= UUID.randomUUID().toString().replaceAll("-", "");
        System.out.println(token);
    }

    @Test
    void test2() throws Exception {
        Map<String, Object> map = RSAUtils.getKeys();
        //生成公钥和私钥
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");

        //模
        String modulus = publicKey.getModulus().toString();
        //公钥指数
        String public_exponent = publicKey.getPublicExponent().toString();
        //私钥指数
        String private_exponent = privateKey.getPrivateExponent().toString();
        //明文
        String ming = "123456789";
        //使用模和指数生成公钥和私钥
        RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
        RSAPrivateKey priKey = RSAUtils.getPrivateKey(modulus, private_exponent);
        //加密后的密文
        String mi = RSAUtils.encryptByPublicKey(ming, pubKey);
        System.err.println(mi);
        //解密后的明文
        ming = RSAUtils.decryptByPrivateKey(mi, priKey);
        System.err.println(ming);

    }
    @Test
    void test() throws Exception {
        Map<String, Object> map = RSAUtils.getKeys();
        // 生成公钥和私钥
        RSAPublicKey publicKey = (RSAPublicKey) map.get("public");
        RSAPrivateKey privateKey = (RSAPrivateKey) map.get("private");

        // 模
        String modulus = publicKey.getModulus().toString();
        System.out.println("pubkey modulus=" + modulus);
        // 公钥指数
        String public_exponent = publicKey.getPublicExponent().toString();
        System.out.println("pubkey exponent=" + public_exponent);
        // 私钥指数
        String private_exponent = privateKey.getPrivateExponent().toString();
        System.out.println("private exponent=" + private_exponent);
        // 明文
        String ming = "yangzhen";
        // 使用模和指数生成公钥和私钥
        RSAPublicKey pubKey = RSAUtils.getPublicKey(modulus, public_exponent);
        RSAPrivateKey priKey = RSAUtils.getPrivateKey(modulus, private_exponent);
        // 加密后的密文
        String mi = RSAUtils.encryptByPublicKey(ming, pubKey);
        System.err.println("mi=" + mi);
        // 解密后的明文
        String ming2 = RSAUtils.decryptByPrivateKey(mi, priKey);
        System.err.println("ming2=" + ming2);
    }
}
