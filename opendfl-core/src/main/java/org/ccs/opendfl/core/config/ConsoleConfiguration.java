package org.ccs.opendfl.core.config;

import lombok.Data;
import org.ccs.opendfl.core.config.vo.RolePermitVo;
import org.ccs.opendfl.core.config.vo.UserVo;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 控制台管理配置信息
 * 角色，用户，权限
 */
@Configuration
@ConfigurationProperties(prefix = "console")
@Component
@Data
public class ConsoleConfiguration {
    private String redisPrefix;
    private Character ifConsole='0';
    private String consoleWhiteIp="none";
    private Integer initLogCount=100;
    private Map<String, String> whiteCodeUsers;
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
     * @return
     */
    public List<RolePermitVo> getRolePermitResults() {
        if(rolePermits==null||rolePermits.size()==0){
            return defaultRolePermitList;
        }
        return rolePermits;
    }

    /**
     * 用户表
     * 如果完全没配用户，自动用默认用户
     * @return
     */
    public List<UserVo> getUserResults() {
        if(users==null||users.size()==0){
            return defaultUserList;
        }
        return users;
    }
}
