package org.ccs.opendfl.mysql.base;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.mysql.utils.LoginUtils;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author cjh
 */
@Slf4j
public abstract class BaseController {
    /**
     * 得到request对象
     *
     * @return
     */
    public HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();
    }

    static final String PATTERN_TRIM_EXP = "\t|\r|\n";
    static final Pattern PATTERN_TRIM = Pattern.compile(PATTERN_TRIM_EXP);

    public Map<String, Object> createAllParams(HttpServletRequest request) {
        Map<String, String[]> properties = request.getParameterMap();
        Map<String, Object> returnMap = new HashMap<>(properties.size());
        Iterator<Map.Entry<String, String[]>> entries = properties.entrySet().iterator();
        Map.Entry<String, String[]> entry;
        String name = "";
        String value = "";

        while (entries.hasNext()) {
            entry = entries.next();
            name = entry.getKey();
            String[] valueObj = entry.getValue();
            if (null == valueObj) {
                value = "";
            } else {
                String[] values = valueObj;
                for (int i = 0; i < values.length; i++) {
                    value = values[i] + ",";
                }
                value = value.substring(0, value.length() - 1);
            }
            Matcher m = PATTERN_TRIM.matcher(value.trim());
            value = m.replaceAll("");
            returnMap.put(name, value);
        }
        return returnMap;
    }


    /**
     * 获取当前登录的用户ID
     *
     * @return
     */
    public Integer getCurrentUserId() {
        UserVo userVo = getCurrentUser();
        Integer id = -1;
        if (userVo != null) {
            id = userVo.getId();
        }
        return id;
    }


    /**
     * 获取当前登录的用户名
     *
     * @return
     */
    public UserVo getCurrentUser() {
        String token = RequestUtils.getToken(getRequest());
        try {
            if (token == null) {
                log.warn("----getCurrentUser--token is null");
                return null;
            }
            return LoginUtils.getUserByToken(token);
        } catch (Exception e) {
//            e.printStackTrace();
            log.warn("----getCurrentUser--token={} invalid error={}", token, e.getMessage());
            return null;
        }
    }

    /**
     * 获取当前登录的用户名
     *
     * @return
     */
    public String getCurrentUsername() {
        String token = RequestUtils.getToken(getRequest());
        UserVo userVo = LoginUtils.getUserByToken(token);
        String name = null;
        if (userVo != null) {
            name = userVo.getUsername();
        }
        return name;
    }

    public void pageSortBy(MyPageInfo pageInfo) {
        String sort = this.getRequest().getParameter("sort");
        String page = this.getRequest().getParameter("page");
        String rows = this.getRequest().getParameter("rows");
        if (StringUtils.isNotBlank(sort)) {
            pageInfo.setOrderBy(sort);
        }
        if (StringUtils.isNotBlank(page)) {
            pageInfo.setPageNum(Integer.parseInt(page));
        }
        if (StringUtils.isNotBlank(rows)) {
            pageInfo.setPageSize(Integer.parseInt(rows));
        }
    }

    public Integer getPageSize() {
        return RequestUtils.getInt(this.getRequest().getParameter("rows"), 10);
    }

}
