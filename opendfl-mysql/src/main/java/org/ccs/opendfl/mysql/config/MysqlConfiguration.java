package org.ccs.opendfl.mysql.config;

import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author chenjh
 */
@Configuration
@Data
public class MysqlConfiguration {
    /**
     * token有效期1小时
     */
    private Integer tokenExpire = 3600;
    /**
     * 是否开始userId转数字进行压缩
     * 原理是自动生成对应的用户数据
     */
    private Integer userIdToNum=1;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
