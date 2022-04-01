package org.ccs.opendfl.core.limitfrequency;


import org.ccs.opendfl.core.biz.IUserBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FrequencyConstant;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FrequencyWhiteCodeUtils {
    private FrequencyWhiteCodeUtils() {

    }

    private static IUserBiz userBiz;
    private static FrequencyConfiguration frequencyConfiguration;

    @Autowired
    public void setUserBiz(IUserBiz userBiz) {
        FrequencyWhiteCodeUtils.userBiz = userBiz;
    }
    @Autowired
    public void setFrequencyConfiguration(FrequencyConfiguration frequencyConfiguration) {
        FrequencyWhiteCodeUtils.frequencyConfiguration = frequencyConfiguration;
    }

    public static String getUserId(String userCode) {
        return userBiz.getUserId(userCode);
    }

    private static Map<String, Long> whiteCodeMap = new ConcurrentHashMap<>();
    private static Map<String, String> whiteUserIdsMap = new ConcurrentHashMap<>();

    /**
     * 用于已经触发频率限制时，如果在白名单中则不限制
     *
     * @param frequency
     * @param curTime
     * @param userId
     * @return
     */
    public static boolean checkWhiteUserId(FrequencyVo frequency, Long curTime, String userId) {
        String whiteCode = frequency.getWhiteCode();
        if (whiteCode == null || StringUtils.equals(FrequencyConstant.NONE, whiteCode) || userId == null) {
            return false;
        }
        Long lastTime = whiteCodeMap.get(whiteCode);
        String whiteUserIds = whiteUserIdsMap.get(whiteCode);
        if (lastTime == null || curTime - lastTime > 10000 || whiteUserIds == null) {
            whiteCodeMap.put(whiteCode, curTime);
            String whiteUserIdsNew = getWhiteCodeUserId(whiteCode);
            whiteUserIdsNew = userCodeToUserId(whiteUserIdsNew);
            if (whiteUserIds == null || !StringUtils.equals(whiteUserIds, whiteUserIdsNew)) {
                whiteUserIds = whiteUserIdsNew;
                whiteUserIdsMap.put(whiteCode, whiteUserIds);
            }
        }
        return isWhiteId(userId, whiteUserIds);
    }

    private static String getWhiteCodeUserId(String whiteCode) {
        return frequencyConfiguration.getWhiteCodeUsers().get(whiteCode);
    }

    public static boolean isWhiteId(String userId, String whiteUserIds) {
        if (StringUtils.isBlank(userId)) {//userId无效忽略
            return false;
        }
        if ("none".equals(whiteUserIds)) {//白名单无效，忽略
            return false;
        } else {
            return whiteUserIds.contains(userId + ",");//是否含白名单
        }
    }


    private static String userCodeToUserId(final String whiteUserIds) {
        if ("none".equals(whiteUserIds)) {
            return whiteUserIds;
        }

        String[] userIds = whiteUserIds.split(",");
        StringBuilder resultIds = new StringBuilder();
        for (String userCode : userIds) {
            if (StringUtils.isBlank(userCode)) {
                continue;
            }
            String userCodeId = getUserId(userCode);
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
