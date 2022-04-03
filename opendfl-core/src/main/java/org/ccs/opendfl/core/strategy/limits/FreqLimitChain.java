package org.ccs.opendfl.core.strategy.limits;


import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class FreqLimitChain {
    private static Logger logger = LoggerFactory.getLogger(FreqLimitChain.class);
    private int pos = 0;
    private int size = 0;
    private final List<FreqLimitStrategy> limitStrategieList=new ArrayList<>();
    private List<FreqLimitStrategy> limitStrategies;
    private RequestStrategyParamsVo strategyParams;
    private String freqTypeItems;

    @Resource(name = "freqLimitIpStrategy")
    private FreqLimitStrategy freqLimitIpStrategy;
    @Resource(name = "freqLimitIpUserStrategy")
    private FreqLimitStrategy freqLimitIpUserStrategy;
    @Resource(name = "freqLimitUserCountStrategy")
    private FreqLimitStrategy freqLimitUserCountStrategy;
    @Resource(name = "freqLimitUserIpStrategy")
    private FreqLimitStrategy freqLimitUserIpStrategy;

    @PostConstruct
    public void initStrategy() {
        this.addLimit(this.freqLimitIpUserStrategy);
        this.addLimit(this.freqLimitUserIpStrategy);
        this.addLimit(this.freqLimitUserCountStrategy);
        this.addLimit(this.freqLimitIpStrategy);
    }


    public void setStrategyParams(RequestStrategyParamsVo strategyParams) {
        this.strategyParams = strategyParams;
    }

    private void addLimit(FreqLimitStrategy limitStrategy) {
        limitStrategieList.add(limitStrategy);
    }

    public RequestStrategyParamsVo getStrategyParams() {
        return strategyParams;
    }

    public void clearLimit() {
        pos = 0;
    }

    private void setFreqTypeItems(String freqTypeItems) {
        this.freqTypeItems = freqTypeItems;
    }

    private static String freqLimitItemStr = null;
    private static String[] freqLimitItems = null;

    private String[] getLimitItems(String limitItems) {
        if (freqLimitItems == null || !StringUtils.equals(limitItems, freqLimitItemStr)) {
            logger.info("----getLimitItems--limitItems={}", limitItems);
            freqLimitItemStr = limitItems;
            freqLimitItems = limitItems.split(",");
        }
        return freqLimitItems;
    }

    private String limitItemsLast;

    /**
     * 按limitItems过滤及重新排序
     *
     * @param limitItems
     */
    public void sortStrategies(String limitItems) {
        //配置值相同不用继续处理
        if (StringUtils.equals(limitItems, limitItemsLast)) {
            return;
        }
        this.limitItemsLast = limitItems;
        this.setFreqTypeItems(limitItems);

        logger.info("-----sortStrategies--limitItems={}", limitItems);
        String[] items = getLimitItems(limitItems);
        List<FreqLimitStrategy> limits = new ArrayList<>();
        for (String item : items) {
            if ("".equals(item)) {
                continue;
            }
            for (FreqLimitStrategy strategy : limitStrategieList) {
                if (item.equals(strategy.getLimitType())) {
                    limits.add(strategy);
                    break;
                }
            }
        }
        limitStrategies = limits;
        size = limitStrategies.size();

    }


    public void doCheckLimit(FreqLimitChain limitChain) {
        int v = pos++;
        if (v >= size) {
            return;
        }
        limitStrategies.get(v).doCheckLimit(this.freqTypeItems, limitChain);
    }
}
