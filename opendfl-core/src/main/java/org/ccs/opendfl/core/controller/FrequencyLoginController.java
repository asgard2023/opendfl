package org.ccs.opendfl.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IRsaBiz;
import org.ccs.opendfl.core.config.vo.UserVo;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.limitcount.Frequency;
import org.ccs.opendfl.core.limitcount.Frequency2;
import org.ccs.opendfl.core.limitcount.FrequencyLoginUtils;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.core.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/frequencyLogin")
@Slf4j
public class FrequencyLoginController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private IRsaBiz rsaBiz;

    @GetMapping("/login")
    public String getlogin() {
        return "/login";
    }

    /*返回頁面*/
    @GetMapping("/index")
    public String index() {
        return "/index";
    }

    @GetMapping("/user")
    public Object getUser(HttpServletRequest request) {
        String token = RequestUtils.getToken(request);
        if(token == null){
            token=request.getParameter("token");
        }
        UserVo user = FrequencyLoginUtils.getUserByToken(token);
        ValidateUtils.notNull(user, "token invalid");
        return ResultData.success(user);
    }

    /**
     * @return
     * @Title: generateRSAKey
     * @Description: 生成公钥和私钥
     * @author chenjh
     */
    @ResponseBody
    @GetMapping(value = "/rsaKey")
    public ResultData generateRSAKey(HttpServletRequest request, @RequestParam(value = "funcCode", required = false) String funcCode) {
        String clientIdRsa = request.getSession().getId();
        try {
//            String funcCode = request.getParameter("funcCode");
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
     * @param user
     * @param clientIdRsa
     * @param mv
     * @param request
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    @Frequency(time = 5, limit = 4, name = "FrequencyLogin")
    @Frequency2(time = 3600, limit = 30, name = "FrequencyLogin")
    public Object login(UserVo user, @RequestParam(value = "clientIdRsa", required = false) String clientIdRsa, ModelAndView mv, HttpServletRequest request) {
        ValidateUtils.notNull(clientIdRsa, "clientIdRsa is null");
        String username = user.getUsername();
        String pwd = user.getPwd();
        if (StringUtils.isNotEmpty(clientIdRsa)) {
            username = rsaBiz.checkRSAKey(clientIdRsa, username);
            pwd = rsaBiz.checkRSAKey(clientIdRsa, pwd);
        }
        UserVo loginedUser = FrequencyLoginUtils.loginUser(username, pwd);
        if (loginedUser != null) {
            String token=UUID.randomUUID().toString().replaceAll("-", "");
            loginedUser.setPwd(null);
            FrequencyLoginUtils.setUserByToken(token, loginedUser);
            LoginVo login = new LoginVo();
            login.setAccess_token(token);
            login.setUser(loginedUser);
            return ResultData.success(login);
        } else {
            throw new FailedException("登入失败");
        }
    }
}
