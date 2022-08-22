package org.ccs.opendfl.mysql.dflsystem.biz.impl;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.DesensitizedUtil;
import com.github.pagehelper.PageHelper;
import org.ccs.opendfl.core.constants.CacheTimeType;
import org.ccs.opendfl.core.exception.*;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.base.BaseService;
import org.ccs.opendfl.mysql.base.ISelfInject;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserBiz;
import org.ccs.opendfl.mysql.dflsystem.mapper.DflUserMapper;
import org.ccs.opendfl.mysql.dflsystem.po.DflUserPo;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * DflUserBiz
 * 业务实现
 *
 * @author chenjh
 * @date 2022-5-3 20:24:48
 */
@Service(value = "dflUserBiz")
public class DflUserBiz extends BaseService<DflUserPo> implements IDflUserBiz, ISelfInject {
    @Autowired
    private DflUserMapper mapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RedisTemplate redisTemplate;

    static Logger logger = LoggerFactory.getLogger(DflUserBiz.class);

    @Override
    public Mapper<DflUserPo> getMapper() {
        return mapper;
    }

    private IDflUserBiz _self;

    @Override
    public void setSelf(Object o) {
        this._self = (IDflUserBiz) o;
    }

    @Override
    public Example createConditions(DflUserPo entity, Map<String, Object> otherParams) {
        Example example = new Example(entity.getClass());
        Example.Criteria criteria = example.createCriteria();
        searchCondition(entity, otherParams, criteria);
        addFilters(criteria, otherParams);
        return example;
    }

    private void searchCondition(DflUserPo entity, Map<String, Object> otherParams, Example.Criteria criteria) {
        String startTime = (String) otherParams.get("startTime");
        if (StringUtil.isNotEmpty(startTime)) {
            criteria.andGreaterThanOrEqualTo("createTime", startTime);
        }
        String endTime = (String) otherParams.get("endTime");
        if (StringUtil.isNotEmpty(endTime)) {
            criteria.andLessThanOrEqualTo("createTime", endTime);
        }

        if (entity.getIfDel() != null) {
            criteria.andEqualTo("ifDel", entity.getIfDel());
        }
        this.addEqualByKey(criteria, "id", otherParams);
        this.addEqualByKey(criteria, "status", otherParams);

        if (StringUtil.isNotEmpty(entity.getUsername())) {
            criteria.andLike("username", "%" + entity.getUsername() + "%");
        }
        if (StringUtil.isNotEmpty(entity.getNickname())) {
            criteria.andLike("nickname", "%" + entity.getNickname() + "%");
        }
    }

    @Override
    public MyPageInfo<DflUserPo> findPageBy(DflUserPo entity, MyPageInfo<DflUserPo> pageInfo, Map<String, Object> otherParams) {
        if (entity == null) {
            entity = new DflUserPo();
        }
        Example example = createConditions(entity, otherParams);
        if (StringUtil.isNotEmpty(pageInfo.getOrderBy()) && StringUtil.isNotEmpty(pageInfo.getOrder())) {
            example.setOrderByClause(StringUtil.camelhumpToUnderline(pageInfo.getOrderBy()) + " " + pageInfo.getOrder());
        }
        PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
        List<DflUserPo> list = this.getMapper().selectByExample(example);
        //不返回密码
        list.forEach(t -> {
            t.setPwd(null);
        });
        return new MyPageInfo<>(list);
    }

    @Override
    public Map<Integer, UserVo> getUserMapByIds(List<Integer> userIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        Example example = new Example(DflUserPo.class);
        example.selectProperties("id,username,nickname,ifDel,status".split(","));
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", userIds);
        List<DflUserPo> userList = this.mapper.selectByExample(example);
        Map<Integer, UserVo> userMap = new HashMap<>(userList.size());
        UserVo vo = null;
        for (DflUserPo userPo : userList) {
            vo = DflUserPo.toUserVo(userPo);
            userMap.put(userPo.getId(), vo);
        }
        return userMap;
    }

    @Override
    public DflUserPo getUserByUsername(String username) {
        ValidateUtils.notNull(username, "username is null");
        DflUserPo search = new DflUserPo();
        search.setUsername(username);
        search.setIfDel(0);
        return desensitized(this.mapper.selectOne(search));
    }

    @Override
    public DflUserPo findUserByTelephone(String telephone) {
        ValidateUtils.notNull(telephone, "telephone is null");
        DflUserPo search = new DflUserPo();
        search.setTelephone(telephone);
        search.setIfDel(0);
        return desensitized(this.mapper.selectOne(search));
    }

    @Override
    public DflUserPo getUserByEmail(String email) {
        ValidateUtils.notNull(email, "email is null");
        DflUserPo search = new DflUserPo();
        search.setEmail(email);
        search.setIfDel(0);
        return desensitized(this.mapper.selectOne(search));
    }

    @Override
    public DflUserPo getUserByNickName(String nickname) {
        ValidateUtils.notNull(nickname, "nickname is null");
        DflUserPo search = new DflUserPo();
        search.setNickname(nickname);
        search.setIfDel(0);
        return desensitized(this.mapper.selectOne(search));
    }

    @Override
    @Cacheable(value = CacheTimeType.CACHE30S, key = "'opendfl:getUserIdByNickName:'+#nickname")
    public Integer getUserIdByNickName(String nickname) {
        DflUserPo user = getUserByNickName(nickname);
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    private DflUserPo desensitized(DflUserPo user) {
        if (user != null) {
            user.setPwd(null);
            user.setTelephone(DesensitizedUtil.mobilePhone(user.getTelephone()));
            user.setEmail(DesensitizedUtil.mobilePhone(user.getEmail()));
        }
        return user;
    }

    @Override
    public UserVo loginUser(loginType type, String account, String loginPwd) {
        ValidateUtils.notNull(account, type + ":account is null");
        DflUserPo search = new DflUserPo();
        search.setIfDel(0);
        if (loginType.USERNAME == type) {
            search.setUsername(account);
        } else if (loginType.TELEPHONE == type) {
            search.setTelephone(account);
        } else if (loginType.EMAIL == type) {
            search.setEmail(account);
        } else {
            throw new FailedException("invalid loginType:" + type);
        }
        DflUserPo user = this.mapper.selectOne(search);
        if (user == null) {
            logger.warn("----loginUser--type={} account={} not found", type, account);
            throw new PasswordInvalidException("invalid username or password");
        } else if (user.getStatus() == null || user.getStatus() != 1) {
            logger.warn("----loginUser--type={} account={} id={} invalid", type, account, user.getId());
            throw new FailedException("User account invalid");
        } else if (!this.passwordEncoder.matches(loginPwd, user.getPwd())) {
            logger.warn("----loginUser--type={} account={} id={} password error", type, account, user.getId());
            throw new PasswordInvalidException("invalid username or password");
        } else {
            return DflUserPo.toUserVo(user);
        }
    }

    @Override
    public Integer saveDflUser(DflUserPo entity) {
        if (entity.getStatus() == null) {
            entity.setStatus(1);
        }
        if (entity.getCreateTime() == null) {
            entity.setCreateTime(new Date());
        }
        entity.setModifyTime(new Date());
        if (entity.getIfDel() == null) {
            entity.setIfDel(0);
        }
        int v = this.mapper.insert(entity);
        return v;
    }

    @Override
    public Integer updateDflUser(DflUserPo entity) {
        entity.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(entity);
        return v;
    }

    @Override
    public Integer deleteDflUser(Integer id, Integer operUser, String remark) {
        DflUserPo po = new DflUserPo();
        po.setId(id);
        // 0未删除,1已删除
        po.setIfDel(1);
        po.setModifyUser(operUser);
        po.setRemark(remark);
        po.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(po);
        return v;
    }

    public static final Integer passwordErrorIntv = 180;
    public static final Integer passwordErrorRetryCount = 10;
    public static final String REDIS_KEY_PASSWORD = "_pwd_user";

    @Override
    public int updatePassword(Integer userId, String oldPassword, String newPassword) {
        if (CharSequenceUtil.isEmpty(oldPassword)) {
            throw new ParamNullException("oldPassword旧密码为空");
        }
        if (CharSequenceUtil.isEmpty(newPassword)) {
            throw new ParamNullException("password新密码为空");
        }
        if (oldPassword.equals(newPassword)) {
            throw new ParamErrorException("oldPassword=newPassword新旧密码不能相同");
        }
        if (newPassword.length() < 6) {
            throw new ParamErrorException("密码长度不够");
        }

        DflUserPo exist = this.findById(userId);
        if (exist == null) {
            logger.warn("----changePassword--userId={}", userId);
            throw new DataNotExistException("userId=" + userId);
        }

        String newEncrypted = this.passwordEncoder.encode(newPassword);

        String redisKey = REDIS_KEY_PASSWORD + "_" + userId;
        Long retryCount = redisTemplate.opsForValue().increment(redisKey);
        if (retryCount > passwordErrorRetryCount) {
            throw new ParamNullException("password error frequency limit");
        }
        if (!this.passwordEncoder.matches(oldPassword, exist.getPwd())) {
            if (retryCount.longValue() == 1) {
                redisTemplate.expire(redisKey, passwordErrorIntv, TimeUnit.SECONDS);
            }
            throw new PasswordInvalidException("旧密码无效");
        }
        redisTemplate.delete(redisKey);
        DflUserPo user = new DflUserPo();
        user.setId(userId);
        user.setPwd(newEncrypted);
        user.setModifyTime(new Date());
        int v = this.updateByPrimaryKeySelective(user);
        return v;
    }

    @Override
    public int changePasswordMgr(Integer userId, String newPassword, String remark, Integer operId) {
        ValidateUtils.notNull(newPassword, "密码不能为空");
        ValidateUtils.notNull(remark, "remark不能为空");
        DflUserPo userPo = this.findById(userId);
        if (userPo == null) {
            logger.warn("----changePasswordMgr--userId={} not found", userId);
            throw new DataNotExistException("userId=" + userId);
        }

        DflUserPo update = new DflUserPo();
        update.setId(userId);
        update.setRemark(remark);
        String newEncrypted = this.passwordEncoder.encode(newPassword);
        update.setPwd(newEncrypted);
        update.setModifyUser(operId);
        update.setModifyTime(new Date());
        return this.updateByPrimaryKeySelective(update);
    }
}