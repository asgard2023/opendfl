package org.ccs.opendfl.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IFrequencyDataBiz;
import org.ccs.opendfl.core.captcha.biz.IVerificateBiz;
import org.ccs.opendfl.core.captcha.constant.CaptchaType;
import org.ccs.opendfl.core.exception.ParamNullException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.FrequencyVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 重置频率限制
 *
 * @author chenjh
 */
@RestController
@RequestMapping("/frequencyReset")
@Slf4j
public class FrequencyResetController {
    @Autowired
    private IVerificateBiz verificateBiz;
    @Resource(name = "frequencyDataRedisBiz")
    private IFrequencyDataBiz frequencyDataBiz;

    /**
     * 图形验证码
     *
     * @param request  httpRequest
     * @param clientId sessionId
     * @param response httpResponse
     * @param type     功能类型
     * @throws Exception 异常
     */
    @RequestMapping(value = "/imageCaptcha", method = {RequestMethod.GET, RequestMethod.POST})
    public void getImageCaptcha(HttpServletRequest request, HttpServletResponse response
            , @RequestParam(value = "clientId", required = false) String clientId
            , @RequestParam(value = "type", required = false) String type) throws Exception {
        if (StringUtils.isEmpty(clientId)) {
            throw new ParamNullException("clientId is null");
        }

        CaptchaType captchaType = CaptchaType.parse(type);
        if (captchaType == null) {
            captchaType = CaptchaType.randomType();
        }
        try {
            // 设置响应的类型格式为图片格式
            //macid = AESEncryption.decrypt(macid);
            response.setContentType("image/png");
            //禁止图像缓存。
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            verificateBiz.captchaOutputStream(captchaType, clientId, response);
        } catch (IOException e) {
            log.error("getImageCaptcha--failed, error: {}.", e.getMessage());
        }
    }

    /**
     * 重置频率限制
     *
     * @param request    httpRequest
     * @param clientId   sessionId
     * @param type       功能类型
     * @param funcCode   功能编码
     * @param verifyCode 验证码值
     * @param frequency  频率限制对象
     * @return 返回结果
     */
    @RequestMapping(value = "/resetLimits", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultData resetLimits(HttpServletRequest request
            , @RequestParam(value = "clientId", required = false) String clientId
            , @RequestParam(value = "verifyCode", required = false) String verifyCode
            , FrequencyVo frequency) {
        if (StringUtils.isEmpty(clientId)) {
            throw new ParamNullException("clientId is null");
        }
        this.verificateBiz.checkVerificationCode(clientId, verifyCode);

        String ip = RequestUtils.getIpAddress(request);
        ip = "" + RequestUtils.getIpConvertNum(ip);
        String userId = request.getParameter(RequestParams.USER_ID);


        //查出用户所有的限制
        List<FrequencyVo> list = frequencyDataBiz.limitUsers(userId);
        List<String> limitInfoList = new ArrayList<>();
        if (StringUtils.isNotBlank(frequency.getName())) {
            String evictKey = frequencyDataBiz.freqIpUserEvict(frequency, ip);
            limitInfoList.add(evictKey);
            //找出name对应的限制时间
            List<Integer> timeList = list.stream().filter(t -> t.getName().equals(frequency.getName())).map(FrequencyVo::getTime).distinct().collect(Collectors.toList());
            log.info("----resetLimits--name={} time={} userId={}", frequency.getName(), timeList, userId);
            List<String> infoList = frequencyDataBiz.freqEvictList(frequency.getName(), timeList, userId);
            limitInfoList.addAll(infoList);
            evictKey = frequencyDataBiz.freqUserIpEvict(frequency, userId);
            limitInfoList.add(evictKey);
        } else {
            for (FrequencyVo frequencyVo : list) {
                String evictKey = frequencyDataBiz.freqIpUserEvict(frequencyVo, ip);
                limitInfoList.add(evictKey);
                List<Integer> timeList = list.stream().map(FrequencyVo::getTime).distinct().collect(Collectors.toList());
                List<String> infoList = frequencyDataBiz.freqEvictList(frequencyVo.getName(), timeList, userId);
                limitInfoList.addAll(infoList);
                evictKey = frequencyDataBiz.freqUserIpEvict(frequencyVo, userId);
                limitInfoList.add(evictKey);
            }
        }
        limitInfoList = limitInfoList.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        log.info("----resetLimits--name={} userId={} evictsize={}", frequency.getName(), userId, limitInfoList.size());
        return ResultData.success(limitInfoList);
    }

    /**
     * 用于支持在页面显示图片验证码，而不是后台生成
     *
     * @param request  httpRequest
     * @param clientId sessionId
     * @return 验证码信息
     */
    @RequestMapping(value = "captchaCode", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultData getImageCaptchaCode(HttpServletRequest request
            , @RequestParam(value = "clientId", required = false) String clientId) {
        if (StringUtils.isEmpty(clientId)) {
            throw new ParamNullException("clientId is null");
        }
        String captchaCode = verificateBiz.setVerficationCode(clientId);
        return ResultData.success(captchaCode);
    }

    @GetMapping("/resetTicket")
    public ResultData getResetTicket(HttpServletRequest request, @RequestParam(value = "funcCode", required = false) String funcCode
            , @RequestParam(value = "type", required = false) String type) {
        String clientId = request.getSession().getId();
        String userId = request.getParameter(RequestParams.USER_ID);
        log.info("----getResetTicket--funcCode={} type={} userId={}", funcCode, type, userId);
        Map<String, String> map = new HashMap<>(8);
        map.put("clientId", clientId);
        map.put("funcCode", funcCode);
        map.put("type", type);
        return ResultData.success(map);
    }
}
