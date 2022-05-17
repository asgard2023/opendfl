package org.ccs.opendfl.core.strategy.black.impl;

import org.ccs.opendfl.core.strategy.black.BlackChain;
import org.ccs.opendfl.core.strategy.black.BlackStrategy;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service(value = "blackChain")
public class BlackChainImpl implements BlackChain {
    private static final Logger logger = LoggerFactory.getLogger(BlackChainImpl.class);
    private int size = 0;
    /**
     * 注册的策略
     */
    private final List<BlackStrategy> blackStrategieRegists = new ArrayList<>();
    /**
     * 当前生效在用的策略，可根据limitItems起作用
     */
    private List<BlackStrategy> blackStrategies;
    private RequestStrategyParamsVo strategyParams;
    private String freqTypeItems;

    @Resource(name = "blackIpStrategy")
    private BlackStrategy blackIpStrategy;
    @Resource(name = "blackUserStrategy")
    private BlackStrategy blackUserStrategy;
    @Resource(name = "blackDeviceIdtrategy")
    private BlackStrategy blackDeviceIdtrategy;

    /**
     * 新增策略需要在这里注册
     */
    @PostConstruct
    public void initStrategy() {
        this.addLimit(blackIpStrategy);
        this.addLimit(blackUserStrategy);
        this.addLimit(blackDeviceIdtrategy);
    }


    public void addLimit(BlackStrategy limitStrategy) {
        blackStrategieRegists.add(limitStrategy);
    }

    public void setFreqTypeItems(String freqTypeItems) {
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

        String[] items = getLimitItems(limitItems);
        List<BlackStrategy> limits = new ArrayList<>();
        for (String item : items) {
            if ("".equals(item)) {
                continue;
            }
            for (BlackStrategy strategy : blackStrategieRegists) {
                if (item.equals(strategy.getLimitType())) {
                    limits.add(strategy);
                    break;
                }
            }
        }
        blackStrategies = limits;
        size = blackStrategies.size();

    }

    @Override
    public boolean doCheckLimit(BlackChain limitChain, final RequestStrategyParamsVo strategyParams) {
        int pos = strategyParams.getPos();
        int v = pos++;
        strategyParams.setPos(pos);
        if (v >= size) {
            return false;
        }
        return blackStrategies.get(v).doCheckLimit(this.freqTypeItems, limitChain, strategyParams);
    }
}
