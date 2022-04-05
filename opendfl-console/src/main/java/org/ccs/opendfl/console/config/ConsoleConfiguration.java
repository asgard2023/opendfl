package org.ccs.opendfl.console.config;

import lombok.Data;
import org.ccs.opendfl.console.config.vo.RolePermitVo;
import org.ccs.opendfl.console.config.vo.UserVo;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 控制台管理配置信息
 * 角色，用户，权限
 *
 * @author chenjh
 */
@Configuration
@ConfigurationProperties(prefix = "console")
@Component
@Data
public class ConsoleConfiguration {
    private String redisPrefix;
    private Character ifConsole='0';
    private String consoleWhiteIp= FrequencyConstant.NONE;
    /**
     * token有效期1小时
     */
    private Integer tokenExpire=3600;
    private List<RolePermitVo> rolePermits;
    private List<UserVo> users;

    private static List<RolePermitVo> defaultRolePermitList;
    private static List<UserVo> defaultUserList;
    static {
        defaultRolePermitList=new ArrayList<>();
        defaultRolePermitList.add(new RolePermitVo("admin", 1, 1, 1));
        defaultRolePermitList.add(new RolePermitVo("user", 1, 0, 0));

        defaultUserList=new ArrayList<>();
        defaultUserList.add(new UserVo("admin", "admin", "admin"));
        defaultUserList.add(new UserVo("user", "user", "user"));
    }

    /**
     * 角色权限表
     * 如果系统完全没配，自动用默认配置
     * @return 配置的所有角色权限表
     */
    public List<RolePermitVo> getRolePermitResults() {
        if(CollectionUtils.isEmpty(rolePermits)){
            return defaultRolePermitList;
        }
        return rolePermits;
    }

    /**
     * 用户表
     * 如果完全没配用户，自动用默认用户
     * @return 配置的所有用户
     */
    public List<UserVo> getUserResults() {
        if(CollectionUtils.isEmpty(users)){
            return defaultUserList;
        }
        return users;
    }
}
