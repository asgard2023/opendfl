package org.ccs.opendfl.console.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.console.biz.IFrequencyLoginBiz;
import org.ccs.opendfl.console.config.vo.UserVo;
import org.ccs.opendfl.console.vo.LoginVo;
import org.ccs.opendfl.core.biz.IRsaBiz;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitfrequency.Frequency2;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

/**
 * 用户登入
 *
 * @author chenjh
 */
@RestController
@RequestMapping("/frequencyLogin")
@Slf4j
public class FrequencyLoginController {

    @Autowired
    private IRsaBiz rsaBiz;

    @Resource(name = "frequencyLoginRedisBiz")
    private IFrequencyLoginBiz frequencyLoginBiz;

    @GetMapping("/login")
    public String getlogin() {
        return "/login";
    }

    @GetMapping("/index")
    public String index() {
        return "/index";
    }

    @GetMapping("/user")
    public ResultData getUser(HttpServletRequest request) {
        String token = RequestUtils.getToken(request);
        if (token == null) {
            token = request.getParameter("token");
        }
        UserVo user = frequencyLoginBiz.getUserByToken(token);
        ValidateUtils.notNull(user, "token invalid");
        return ResultData.success(user);
    }

    /**
     * clientIdRsa和将公钥传到前端
     *
     * @author chenjh
     */
    @ResponseBody
    @GetMapping(value = "/rsaKey")
    public ResultData getRsaKey(HttpServletRequest request, @RequestParam(value = "funcCode", required = false) String funcCode) {
        String clientIdRsa = request.getSession().getId();
        try {
            // 将公钥传到前端
            Map<String, String> map = rsaBiz.generateRSAKey(clientIdRsa, funcCode);
            map.put("clientIdRsa", clientIdRsa);
            map.put("funcCode", funcCode);
            return ResultData.success(map);
        } catch (Exception e) {
            log.error("----generateRSAKey--clientIdRsa={}", clientIdRsa, e);
            throw new FailedException(e.getMessage());
        }
    }

    /**
     * 用于登录（支持参数加密）
     *
     * @param user        登入对象
     * @param clientIdRsa clientIdRsa
     * @return ResultData
     */
    @PostMapping("/login")
    @ResponseBody
    @Frequency(time = 5, limit = 4, name = "frequencyLogin", attrName = "username")
    @Frequency2(time = 3600, limit = 30, name = "frequencyLogin", attrName = "username")
    public ResultData login(UserVo user, @RequestParam(value = "clientIdRsa", required = false) String clientIdRsa, HttpServletRequest request) {
        ValidateUtils.notNull(clientIdRsa, "clientIdRsa is null");
        String username = user.getUsername();
        String pwd = user.getPwd();
        String ip = RequestUtils.getIpAddress(request);
        if (StringUtils.isNotEmpty(clientIdRsa)) {
            username = rsaBiz.checkRSAKey(clientIdRsa, username);
            pwd = rsaBiz.checkRSAKey(clientIdRsa, pwd);
        }
        log.info("----login--username={} ip={}", username, ip);
        UserVo loginedUser = frequencyLoginBiz.loginUser(username, pwd);
        if (loginedUser != null) {
            String token = UUID.randomUUID().toString().replaceAll("-", "");
            loginedUser.setPwd(null);
            frequencyLoginBiz.saveUserByToken(token, loginedUser);
            LoginVo login = new LoginVo();
            login.setAccess_token(token);
            login.setUser(loginedUser);
            return ResultData.success(login);
        } else {
            log.warn("---login-username={} ip={}", username, ip);
            throw new FailedException("登入失败");
        }
    }
}
