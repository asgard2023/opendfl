package org.ccs.opendfl.core.strategy.limits.impl;

import org.ccs.opendfl.core.strategy.limits.FreqLimitChain;
import org.ccs.opendfl.core.strategy.limits.FreqLimitStrategy;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service(value = "freqLimitChain")
public class FreqLimitChainImpl implements FreqLimitChain {
    private static final Logger logger = LoggerFactory.getLogger(FreqLimitChainImpl.class);
    private int size = 0;
    /**
     * 注册的策略
     */
    private final List<FreqLimitStrategy> limitStrategieRegists = new ArrayList<>();
    /**
     * 当前生效在用的策略，可根据limitItems起作用
     */
    private List<FreqLimitStrategy> limitStrategies;
    private String freqTypeItems;

    @Resource(name = "freqLimitIpStrategy")
    private FreqLimitStrategy freqLimitIpStrategy;
    @Resource(name = "freqLimitIpUserStrategy")
    private FreqLimitStrategy freqLimitIpUserStrategy;
    @Resource(name = "freqLimitUserCountStrategy")
    private FreqLimitStrategy freqLimitUserCountStrategy;
    @Resource(name = "freqLimitUserIpStrategy")
    private FreqLimitStrategy freqLimitUserIpStrategy;

    /**
     * 新增策略需要在这里注册
     */
    @PostConstruct
    public void initStrategy() {
        this.addLimit(this.freqLimitIpUserStrategy);
        this.addLimit(this.freqLimitUserIpStrategy);
        this.addLimit(this.freqLimitUserCountStrategy);
        this.addLimit(this.freqLimitIpStrategy);
    }


    private void addLimit(FreqLimitStrategy limitStrategy) {
        limitStrategieRegists.add(limitStrategy);
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
    @Override
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
            for (FreqLimitStrategy strategy : limitStrategieRegists) {
                if (item.equals(strategy.getLimitType())) {
                    limits.add(strategy);
                    break;
                }
            }
        }
        limitStrategies = limits;
        size = limitStrategies.size();

    }

    @Override
    public void doCheckLimit(final FreqLimitChain limitChain, final RequestStrategyParamsVo strategyParams) {
        int pos=strategyParams.getPos();
        int v = pos++;
        strategyParams.setPos(pos);
        if (v >= size) {
            return;
        }
        limitStrategies.get(v).doCheckLimit(this.freqTypeItems, limitChain, strategyParams);
    }
}
