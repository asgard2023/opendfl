package org.ccs.opendfl.console.biz;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.console.config.ConsoleConfiguration;
import org.ccs.opendfl.console.config.vo.RolePermitVo;
import org.ccs.opendfl.console.config.vo.UserVo;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户登入，token查询
 *
 * @author chenjh
 */
@Service(value = "frequencyLoginRedisBiz")
@Slf4j
public class FrequencyLoginRedisBiz implements IFrequencyLoginBiz {
    @Autowired
    private RedisTemplate<String, Object> redisTemplateJson;
    @Autowired
    private ConsoleConfiguration consoleConfiguration;
    public static final String REDIS_FREQUENCY_LOGIN_TOKEN = "freqLoginToken:";

    @Override
    public UserVo loginUser(String username, String pwd) {
        if (!StringUtils.ifYes(consoleConfiguration.getIfConsole())) {
            log.warn("-----loginUser--ifConsole={}", consoleConfiguration.getIfConsole());
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

        log.warn("-----loginUser--username={}", username);
        RolePermitVo rolePermitVo = getRolePermit(userVo.getRole());
        if (rolePermitVo == null) {
            throw new FailedException("Role not found");
        }
        return userVo.clone();
    }

    @Override
    public UserVo getUserByToken(String token) {
        ValidateUtils.notNull(token, "token is null");
        return (UserVo) redisTemplateJson.opsForValue().get(REDIS_FREQUENCY_LOGIN_TOKEN + token);
    }

    @Override
    public void saveUserByToken(String token, UserVo loginedUser) {
        ValidateUtils.notNull(token, "token is null");
        ValidateUtils.notNull(loginedUser, "user is null");
        String redisKey = REDIS_FREQUENCY_LOGIN_TOKEN + token;
        redisTemplateJson.opsForValue().set(redisKey, loginedUser, consoleConfiguration.getTokenExpire(), TimeUnit.SECONDS);
    }

    @Override
    public RolePermitVo getUserPermitByToken(String token) {
        UserVo userVo = getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        return getRolePermit(userVo.getRole());
    }

    @Override
    public RolePermitVo getRolePermit(String roleCode) {
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
