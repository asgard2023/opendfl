package org.ccs.opendfl.mysql.dflsystem.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.config.MysqlConfiguration;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserBiz;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserLoginBiz;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserRoleBiz;
import org.ccs.opendfl.mysql.dflsystem.po.DflUserRolePo;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DflUserLoginBiz implements IDflUserLoginBiz {
    @Autowired
    private RedisTemplate<String, Object> redisTemplateJson;
    public static final String REDIS_FREQUENCY_LOGIN_TOKEN = "freqLoginToken:";
    @Autowired
    private IDflUserBiz dflUserBiz;
    @Autowired
    private IDflUserRoleBiz dflUserRoleBiz;
    @Autowired
    private MysqlConfiguration mysqlConfiguration;

    @Override
    public UserVo loginUser(String username, String pwd) {
        UserVo userVo = this.dflUserBiz.loginUser(IDflUserBiz.loginType.USERNAME, username, pwd);
        if (userVo == null) {
            throw new FailedException("invalid username or password");
        }

        log.info("-----loginUser--username={}", username);
        List<DflUserRolePo> userRoles = getRolePermit(userVo.getId());
        String roles = userRoles.stream().map(t -> t.getRole().getCode()).collect(Collectors.joining());
        userVo.setRole(roles);
        if (CollectionUtils.isEmpty(userRoles)) {
            log.info("-----loginUser--userRoles={} not found");
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
        redisTemplateJson.opsForValue().set(redisKey, loginedUser, mysqlConfiguration.getTokenExpire(), TimeUnit.SECONDS);
    }

    @Override
    public List<DflUserRolePo> getUserPermitByToken(String token) {
        UserVo userVo = getUserByToken(token);
        ValidateUtils.notNull(userVo, "token invalid");
        return getRolePermit(userVo.getId());
    }

    @Override
    public List<DflUserRolePo> getRolePermit(Integer userId) {
        ValidateUtils.notNull(userId, "userId not null");
        return dflUserRoleBiz.findUserRoles(userId);
    }
}
