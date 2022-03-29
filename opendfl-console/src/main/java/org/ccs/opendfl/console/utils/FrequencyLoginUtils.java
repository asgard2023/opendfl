package org.ccs.opendfl.core.limitcount;

import org.ccs.opendfl.core.config.ConsoleConfiguration;
import org.ccs.opendfl.core.config.vo.RolePermitVo;
import org.ccs.opendfl.core.config.vo.UserVo;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FrequencyLoginUtils {
    private FrequencyLoginUtils() {

    }

    private static RedisTemplate<String, Object> redisTemplateJson;
    private static ConsoleConfiguration consoleConfiguration;

    @Resource(name = "redisTemplateJson")
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplateJson) {
        FrequencyLoginUtils.redisTemplateJson = redisTemplateJson;
    }

    @Autowired
    public void setConsoleConfiguration(ConsoleConfiguration consoleConfiguration) {
        FrequencyLoginUtils.consoleConfiguration = consoleConfiguration;
    }


    public static UserVo loginUser(String username, String pwd) {
        if (!StringUtils.ifYes(consoleConfiguration.getIfConsole())) {
            throw new FailedException("console close");
        }
        List<UserVo> users = consoleConfiguration.getUserResults();
        UserVo userVo = null;
        for (UserVo user : users) {
            if (StringUtils.equals(user.getUsername(), username) && StringUtils.equals(user.getPwd(), pwd)) {
                userVo = user;
                break;
            }
        }
        if (userVo == null) {
            throw new FailedException("invalid username or password");
        }

        RolePermitVo rolePermitVo = getRolePermit(userVo.getRole());
        if (rolePermitVo == null) {
            throw new FailedException("Role not found");
        }
        return userVo.clone();
    }

    public static final String REDIS_FREQUENCY_LOGIN_TOKEN = "freqLoginToken:";

    public static UserVo getUserByToken(String token) {
        ValidateUtils.notNull(token, "token is null");
        return (UserVo) redisTemplateJson.opsForValue().get(REDIS_FREQUENCY_LOGIN_TOKEN + token);
    }

    public static void setUserByToken(String token, UserVo loginedUser) {
        ValidateUtils.notNull(token, "token is null");
        String redisKey = FrequencyLoginUtils.REDIS_FREQUENCY_LOGIN_TOKEN + token;
        redisTemplateJson.opsForValue().set(redisKey, loginedUser, 1, TimeUnit.HOURS);
    }

    public static RolePermitVo getUserPermitByToken(String token) {
        UserVo userVo = getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        return getRolePermit(userVo.getRole());
    }

    public static RolePermitVo getRolePermit(String roleCode) {
        List<RolePermitVo> roles = consoleConfiguration.getRolePermitResults();
        RolePermitVo rolePermitVo = null;
        for (RolePermitVo role : roles) {
            if (StringUtils.equals(roleCode, role.getRole())) {
                rolePermitVo = role;
                break;
            }
        }
        return rolePermitVo;
    }
}
