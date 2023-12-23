package org.ccs.opendfl.mysql.controller;

import cn.hutool.core.text.CharSequenceUtil;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IRsaBiz;
import org.ccs.opendfl.core.exception.BaseException;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitfrequency.Frequencys;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.mysql.dflsystem.biz.IDflUserLoginBiz;
import org.ccs.opendfl.mysql.utils.AuditLogUtils;
import org.ccs.opendfl.mysql.vo.LoginVo;
import org.ccs.opendfl.mysql.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private IDflUserLoginBiz dflUserLoginBiz;

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
        UserVo user = dflUserLoginBiz.getUserByToken(token);
        ValidateUtils.notNull(user, "token invalid");
        return ResultData.success(user);
    }

    /**
     * clientIdRsa和将公钥传到前端
     *
     * @author chenjh
     */

    @GetMapping(value = "/rsaKey")
    public ResultData getRsaKey(HttpServletRequest request, @RequestParam(value = "funcCode", required = false) String funcCode) {
        String clientIdRsa = request.getSession().getId();
        try {
            // 将公钥传到前端
            Map<String, String> map = rsaBiz.generateRSAKey(clientIdRsa, funcCode);
            map.put("clientIdRsa", clientIdRsa);
            map.put("funcCode", funcCode);
            return ResultData.success(map);
        }catch (BaseException e){
            throw e;
        }
        catch (Exception e) {
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

    @Frequencys({
            @Frequency(time = 5, limit = 4, name = "frequencyLogin", attrName = "username"),
            @Frequency(time = 3600, limit = 30, name = "frequencyLogin", attrName = "username")
    })
    public ResultData login(UserVo user, @RequestParam(value = "clientIdRsa", required = false) String clientIdRsa, HttpServletRequest request) {
        ValidateUtils.notNull(clientIdRsa, "clientIdRsa is null");
        String username = user.getUsername();
        String pwd = user.getPwd();
        String ip = RequestUtils.getIpAddress(request);
        if (CharSequenceUtil.isNotEmpty(clientIdRsa)) {
            username = rsaBiz.checkRSAKey(clientIdRsa, username);
            pwd = rsaBiz.checkRSAKey(clientIdRsa, pwd);
        }
        log.info("----login--username={} ip={}", username, ip);
        UserVo loginedUser = null;
        try {
            loginedUser = dflUserLoginBiz.loginUser(username, pwd);
            if (loginedUser != null) {
                String token = UUID.randomUUID().toString().replaceAll("-", "");
                AuditLogUtils.addAuditLog(request, loginedUser, "loginOk", username, null);
                loginedUser.setPwd(null);
                dflUserLoginBiz.saveUserByToken(token, loginedUser);
                LoginVo login = new LoginVo();
                login.setAccess_token(token);
                login.setUser(loginedUser);
                return ResultData.success(login);
            } else {
                AuditLogUtils.addAuditLog(request, loginedUser, "loginFail", username, null);
                log.warn("---login-username={} ip={}", username, ip);
                throw new FailedException("登入失败");
            }
        } catch (Exception e) {
            AuditLogUtils.addAuditLog(request, loginedUser, "loginFail", username, e.toString(), null);
            throw e;
        }

    }
}
