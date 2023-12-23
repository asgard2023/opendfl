package org.ccs.opendfl.core.controller;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.core.biz.IMaxRunTimeBiz;
import org.ccs.opendfl.core.config.FrequencyConfiguration;
import org.ccs.opendfl.core.constants.FreqLimitType;
import org.ccs.opendfl.core.exception.FailedException;
import org.ccs.opendfl.core.exception.ResultData;
import org.ccs.opendfl.core.limitfrequency.Frequency;
import org.ccs.opendfl.core.limitfrequency.Frequencys;
import org.ccs.opendfl.core.utils.RequestParams;
import org.ccs.opendfl.core.utils.RequestUtils;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.utils.ValidateUtils;
import org.ccs.opendfl.core.vo.MaxRunTimeVo;
import org.ccs.opendfl.core.vo.RequestTestVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 频率限制支持
 *
 * @author chenjh
 */
@RestController
@RequestMapping("/frequencyTest")
@Slf4j
public class FrequencyTestController {
    @Value("${spring.redis.host}")
    private String redisHost;
    @Autowired
    private IMaxRunTimeBiz maxRunTimeBiz;
    @Autowired
    private FrequencyConfiguration frequencyConfiguration;

    /**
     * 找出second时间内执行最慢的近count个接口
     * 可用于做监控告警，比如5秒内执行时间最长的接口
     *
     * @param request HttpServletRequest
     * @param second  单位秒
     * @param count   接口个数
     * @return 测试数据
     */
    @GetMapping("/monitor")
    public ResultData monitor(HttpServletRequest request, @RequestParam(name = "second", defaultValue = "30") Integer second
            , @RequestParam(name = "count", defaultValue = "20") Integer count) {
        log.info("----monitor--userId={}", request.getParameter(RequestParams.USER_ID));
        if (!StringUtils.ifYes(frequencyConfiguration.getRunTimeMonitor())) {
            throw new FailedException("feature is not open, see frequency.runTimeMonitor");
        }
        List<MaxRunTimeVo> list = this.maxRunTimeBiz.getNewlyMaxRunTime(second, count);
        return ResultData.success(list);
    }

    @GetMapping("/serverTime")
    public Object serverTime(HttpServletRequest request) {
        log.info("----serverTime--userId={} {}", request.getParameter(RequestParams.USER_ID), redisHost);
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreq")
    @Frequencys({
            @Frequency(time = 5, limit = 10, name = "serverTimeFreq", log = true),
            @Frequency(time = 3600, limit = 20, name = "serverTimeFreq", log = true)
    })
    public Object serverTimeFreq(HttpServletRequest request) {
        log.info("----serverTimeFreq--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqAttr")
    @Frequencys({@Frequency(time = 5, limit = 50, name = "serverTimeFreqAttr", attrName = "dataId")
            , @Frequency(time = 3600, limit = 100, name = "serverTimeFreqAttr", attrName = "dataId")})
    public Object serverTimeFreqAttr(HttpServletRequest request) {
        log.info("----serverTimeFreqAttr--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqAttrCheck")
    @Frequencys({
            @Frequency(time = 5, limit = 50, name = "serverTimeFreqAttrCheck", attrName = "dataId")
            , @Frequency(time = 3600, limit = 100, name = "serverTimeFreqAttrCheck", attrName = "dataId")
    })
    public Object serverTimeFreqAttrCheck(HttpServletRequest request) {
        log.info("----serverTimeFreqAttrCheck--userId={}", request.getParameter(RequestParams.USER_ID));
        String dataId = request.getParameter("dataId");
        ValidateUtils.notNull(dataId, "dataId is null");
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqResIp")
    @Frequencys({
            @Frequency(time = 60, limit = 10, freqLimitType = FreqLimitType.RES_IP, attrName = "dataId", name = "serverTimeFreqResIp", log = true)
    })
    public Object serverTimeFreqResIp(HttpServletRequest request) {
        String dataId = request.getParameter("dataId");
        log.info("----serverTimeFreqResIp--userId={} dataId={}", request.getParameter(RequestParams.USER_ID), dataId);
        ValidateUtils.notNull(dataId, "dataId is null");
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqResUser")
    @Frequencys({@Frequency(time = 60, limit = 10, freqLimitType = FreqLimitType.RES_USER, attrName = "dataId", name = "serverTimeFreqResUser", log = true)
    })
    public Object serverTimeFreqResUser(HttpServletRequest request) {
        String dataId = request.getParameter("dataId");
        log.info("----serverTimeFreqResUser--userId={} dataId={}", request.getParameter(RequestParams.USER_ID), dataId);
        ValidateUtils.notNull(dataId, "dataId is null");
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqRes")
    @Frequencys({
            @Frequency(time = 60, limit = 10, freqLimitType = FreqLimitType.RES_IP, attrName = "dataId", name = "serverTimeFreqRes"),
            @Frequency(time = 60, limit = 10, freqLimitType = FreqLimitType.RES_USER, attrName = "dataId", name = "serverTimeFreqRes", log = true)
    })
    public Object serverTimeFreqRes(HttpServletRequest request) {
        String dataId = request.getParameter("dataId");
        log.info("----serverTimeFreqRes--userId={} dataId={}", request.getParameter(RequestParams.USER_ID), dataId);
        ValidateUtils.notNull(dataId, "dataId is null");
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqLimitIp")
    @Frequency(time = 5, limit = 10, name = "serverTimeFreqLimitIp", freqLimitType = FreqLimitType.LIMIT_IP)
    public Object serverTimeFreqLimitIp(HttpServletRequest request) {
        log.info("----serverTimeFreqLimitIp--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeNeedLogin")
    @Frequencys({
            @Frequency(time = 5, limit = 5, needLogin = true, name = "serverTimeNeedLogin"),
            @Frequency(time = 3600, needLogin = true, limit = 100, name = "serverTimeNeedLogin")
    })
    public Object serverTimeNeedLogin(HttpServletRequest request) {
        log.info("----serverTimeNeedLogin--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreq120")
    @Frequencys({
            @Frequency(time = 120, limit = 5, name = "serverTimeFreq120"),
            @Frequency(time = 3600, limit = 100, name = "serverTimeFreq120")
    })
    public Object serverTimeFreq120(HttpServletRequest request) {
        log.info("----serverTimeFreq120--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqIpUser")
    @Frequency(time = 5, limit = 5, freqLimitType = FreqLimitType.IP_USER, name = "serverTimeFreqIpUser")
    public Object serverTimeFreqIpUser(HttpServletRequest request) {
        log.info("----serverTimeFreqIpUser--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqUserIp")
    @Frequency(time = 5, limit = 10, freqLimitType = FreqLimitType.USER_IP, name = "serverTimeFreqUserIp")
    public Object serverTimeFreqUserIp(HttpServletRequest request) {
        log.info("----serverTimeFreqUserIp--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqIp")
    @Frequencys({
            @Frequency(time = 30, limit = 10, freqLimitType = FreqLimitType.IP_USER, name = "serverTimeFreqIp"),
            @Frequency(time = 30, limit = 10, freqLimitType = FreqLimitType.USER_IP, name = "serverTimeFreqIp")
    })
    public Object serverTimeFreqIp(HttpServletRequest request) {
        log.info("----serverTimeFreqIp--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeFreqIpUv")
    @Frequency(time = 30, limit = 10, freqLimitType = FreqLimitType.IP_USER, name = "serverTimeFreqIp")
    public Object serverTimeFreqIpUv(HttpServletRequest request) {
        log.info("----serverTimeFreqIp--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeLimitIp")
    @Frequency(time = 30, limit = 10, freqLimitType = FreqLimitType.LIMIT_IP, name = "serverTimeLimitIp")
    public Object serverTimeLimitIp(HttpServletRequest request) {
        log.info("----serverTimeLimitIp--userId={}", request.getParameter(RequestParams.USER_ID));
        return System.currentTimeMillis();
    }


    @GetMapping("/serverTimeFreqDevice")
    public Object serverTimeFreqDevice(HttpServletRequest request) {
        String deviceId = RequestUtils.getDeviceId(request);
        log.info("----serverTimeFreqDevice--deviceId={}", deviceId);
        return System.currentTimeMillis();
    }

    @GetMapping("/serverTimeUri")
    public Object serverTimeUri(HttpServletRequest request) {
        log.info("----serverTimeUri--account={}", request.getParameter("account"));
        return System.currentTimeMillis();
    }

    @RequestMapping(value = "/serverTimeUriPostGet", method = {RequestMethod.GET, RequestMethod.POST})
    public Object serverTimeUriPostGet(HttpServletRequest request) {
        log.info("----serverTimeUriPostGet--account={}", request.getParameter("account"));
        return System.currentTimeMillis();
    }

    @RequestMapping(value = "/serverTimeUriPostGet2", method = {RequestMethod.GET, RequestMethod.POST})
    public Object serverTimeUriPostGet2(HttpServletRequest request) {
        log.info("----serverTimeUriPostGet2--account={}", request.getParameter("account"));
        return System.currentTimeMillis();
    }

    /**
     * json参数测试
     *
     * @param requestTest RequestTestVo
     * @return current time
     */
    @PostMapping("/serverTimeJsonFreq")
    @Frequency(time = 5, limit = 5, name = "serverTimeJsonFreq", freqLimitType = FreqLimitType.LIMIT)
    public Object serverTimeJsonFreq(@RequestBody RequestTestVo requestTest) {
        ValidateUtils.notNull(requestTest.getUserId(), "userId is null");
        log.info("----serverTimeJsonFreq--userId={}", requestTest.getUserId());
        return System.currentTimeMillis();
    }

    /**
     * 字符流测试
     *
     * @param request HttpServletRequest
     * @return current time
     * @throws Exception 异常
     */
    @PostMapping("/serverTimeStreamFreq")
    @Frequency(time = 5, limit = 5, name = "serverTimeStreamFreq")
    public Object serverTimeStreamFreq(HttpServletRequest request) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        String temp;
        while ((temp = br.readLine()) != null) {
            sb.append(temp);
        }
        IoUtil.close(br);
        String params = sb.toString();
        log.info("-----serverTimeStreamFreq--params={}", params);
        RequestTestVo requestTest = JSON.parseObject(params, RequestTestVo.class);
        ValidateUtils.notNull(requestTest.getUserId(), "userId is null");
        log.debug("----serverTimeStreamFreq--userId={}", requestTest.getUserId());
        return System.currentTimeMillis();
    }
}
