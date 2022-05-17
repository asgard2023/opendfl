package org.ccs.opendfl.core.strategy.white.impl;

import org.ccs.opendfl.core.strategy.white.WhiteChain;
import org.ccs.opendfl.core.strategy.white.WhiteStrategy;
import org.ccs.opendfl.core.utils.StringUtils;
import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service(value = "whiteChain")
public class WhiteChainImpl implements WhiteChain {
    private static final Logger logger = LoggerFactory.getLogger(WhiteChainImpl.class);
    private int size = 0;
    /**
     * 注册的策略
     */
    private final List<WhiteStrategy> whiteStrategieRegists = new ArrayList<>();
    /**
     * 当前生效在用的策略，可根据limitItems起作用
     */
    private List<WhiteStrategy> whiteStrategies;
    private String freqTypeItems;

    @Resource(name = "whiteIpStrategy")
    private WhiteStrategy whiteIpStrategy;
    @Resource(name = "whiteUserStrategy")
    private WhiteStrategy whiteUserStrategy;

    /**
     * 新增策略需要在这里注册
     */
    @PostConstruct
    public void initStrategy() {
        this.addLimit(whiteIpStrategy);
        this.addLimit(whiteUserStrategy);
    }

    private void addLimit(WhiteStrategy limitStrategy) {
        whiteStrategieRegists.add(limitStrategy);
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
        String[] items = getLimitItems(limitItems);
        List<WhiteStrategy> limits = new ArrayList<>();
        for (String item : items) {
            if ("".equals(item)) {
                continue;
            }
            for (WhiteStrategy strategy : whiteStrategieRegists) {
                if (item.equals(strategy.getLimitType())) {
                    limits.add(strategy);
                    break;
                }
            }
        }
        whiteStrategies = limits;
        size = whiteStrategies.size();

    }


    @Override
    public boolean doCheckLimit(WhiteChain limitChain, final RequestStrategyParamsVo strategyParams) {
        int pos = strategyParams.getPos();
        int v = pos++;
        strategyParams.setPos(pos);
        if (v >= size) {
            return false;
        }
        return whiteStrategies.get(v).doCheckLimit(this.freqTypeItems, limitChain, strategyParams);
    }
}
