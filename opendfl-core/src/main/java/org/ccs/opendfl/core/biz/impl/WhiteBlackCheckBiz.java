package org.ccs.opendfl.core.biz.impl;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IUserBiz;
import org.ccs.opendfl.core.biz.IWhiteBlackCheckBiz;
import org.ccs.opendfl.core.biz.IWhiteBlackListBiz;
import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.constants.WhiteBlackCheckType;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(value = "whiteBlackCheckBiz")
@Slf4j
public class WhiteBlackCheckBiz implements IWhiteBlackCheckBiz {
    @Autowired
    private IUserBiz userBiz;
    @Autowired
    private IWhiteBlackListBiz whiteBlackListBiz;

    public void setWhiteBlackListBiz(IWhiteBlackListBiz whiteBlackListBiz) {
        this.whiteBlackListBiz = whiteBlackListBiz;
    }

    private static final Map<String, Long> whiteCodeMap = new ConcurrentHashMap<>();
    private static final Map<String, String> whiteUserIdsMap = new ConcurrentHashMap<>();

    /**
     * 用于已经触发频率限制时，如果在白名单中则不限制
     *
     * @param frequency FrequencyVo
     * @param curTime   currentTime
     * @param userId    userId
     * @return boolean
     */
    @Override
    public boolean checkWhiteUserId(FrequencyVo frequency, Long curTime, String userId) {
        String whiteCode = frequency.getWhiteCode();
        if (whiteCode == null || StringUtils.equals(FrequencyConstant.NONE, whiteCode) || userId == null) {
            return false;
        }
        Long lastTime = whiteCodeMap.get(whiteCode);
        String whiteUserIds = whiteUserIdsMap.get(whiteCode);
        if (lastTime == null || curTime - lastTime > FrequencyConstant.LOAD_CONFIG_INTERVAL) {
            whiteCodeMap.put(whiteCode, curTime);
            String whiteUserIdsNew = getWhiteCodeUserId(whiteCode);
            whiteUserIdsNew = userCodeToUserId(whiteUserIdsNew);
            if (whiteUserIds == null || !StringUtils.equals(whiteUserIds, whiteUserIdsNew)) {
                whiteUserIds = whiteUserIdsNew;
                whiteUserIdsMap.put(whiteCode, whiteUserIds);
            }
        }
        if (StringUtils.isBlank(whiteUserIds)) {
            return false;
        }
        return isIncludeId(userId, whiteUserIds);
    }

    public String getWhiteCodeUserId(String whiteCode) {
        return whiteBlackListBiz.getWhiteCodeUsers().get(whiteCode);
    }

    @Override
    public boolean isIncludeId(String userId, String whiteUserIds) {
        if (StringUtils.isBlank(userId)) {//userId无效忽略
            return false;
        }
        if (FrequencyConstant.NONE.equals(whiteUserIds)) {//白名单无效，忽略
            return false;
        } else {
            return whiteUserIds.contains(userId + ",");//是否含白名单
        }
    }

    @Override
    public boolean isIncludeWhiteId(String id, WhiteBlackCheckType checkType) {
        boolean check = false;
        WhiteBlackConfigVo whiteConfig = whiteBlackListBiz.getWhiteConfig();
        if (WhiteBlackCheckType.USER == checkType) {
            check = this.isIncludeId(id, whiteConfig.getUsers());
        } else if (WhiteBlackCheckType.IP == checkType) {
            check = this.isIncludeId(id, whiteConfig.getIps());
        } else if (WhiteBlackCheckType.ORIGIN == checkType) {
            //如果白名单ifOriginRequire!=1表示origin非必填，origin为空默认通过
            if (!StringUtils.ifYes(whiteConfig.getIfOriginRequire()) && StringUtils.isEmpty(id)) {
                check = true;
            } else if (StringUtils.isBlank(whiteConfig.getOrigins())) {
                check = true;
            } else {
                check = this.isIncludeId(id, whiteConfig.getOrigins());
            }
        }
        return check;
    }

    @Override
    public boolean isIncludeBlackId(String id, WhiteBlackCheckType checkType) {
        boolean check = false;
        WhiteBlackConfigVo blackConfig = whiteBlackListBiz.getBlackConfig();
        if (WhiteBlackCheckType.USER == checkType) {
            check = this.isIncludeId(id, blackConfig.getUsers());
        } else if (WhiteBlackCheckType.IP == checkType) {
            check = this.isIncludeId(id, blackConfig.getIps());
        } else if (WhiteBlackCheckType.DEVICE == checkType) {
            if (StringUtils.ifYes(blackConfig.getIfDeviceIdRequire()) && StringUtils.isEmpty(id)) {
                return true;
            }
            check = this.isIncludeId(id, blackConfig.getDeviceIds());
        }
        return check;
    }


    private String userCodeToUserId(final String whiteUserIds) {
        if (FrequencyConstant.NONE.equals(whiteUserIds)) {
            return whiteUserIds;
        }

        String[] userIds = whiteUserIds.split(",");
        StringBuilder resultIds = new StringBuilder();
        for (String userCode : userIds) {
            if (StringUtils.isBlank(userCode)) {
                continue;
            }
            String userCodeId = userBiz.getUserId(userCode);
            if (userCodeId != null) {
                resultIds.append(userCodeId);
            } else {
                resultIds.append(userCode);
            }
            resultIds.append(",");
        }
        return resultIds.toString();
    }
}
