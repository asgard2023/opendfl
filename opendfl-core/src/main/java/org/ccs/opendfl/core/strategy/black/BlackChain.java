package org.ccs.opendfl.core.strategy.black;


import org.ccs.opendfl.core.config.vo.WhiteBlackConfigVo;
import org.ccs.opendfl.core.utils.CommUtils;
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
public class BlackChain  {
    private static Logger logger = LoggerFactory.getLogger(BlackChain.class);
    private int pos = 0;
    private int size = 0;
    private final List<BlackStrategy> blackStrategieList=new ArrayList<>();
    private List<BlackStrategy> blackStrategies;
    private WhiteBlackConfigVo blackConfig;
    private RequestStrategyParamsVo strategyParams;
    private String freqTypeItems;

    @Resource(name = "blackIpStrategy")
    private BlackStrategy blackIpStrategy;
    @Resource(name = "blackUserStrategy")
    private BlackStrategy blackUserStrategy;

    @PostConstruct
    public void initStrategy() {
        this.addLimit(blackIpStrategy);
        this.addLimit(blackUserStrategy);
    }


    public void setStrategyParams(RequestStrategyParamsVo strategyParams){
        this.strategyParams = strategyParams;
    }

    public RequestStrategyParamsVo getStrategyParams() {
        return strategyParams;
    }

    public void setBlackConfig(WhiteBlackConfigVo blackConfig){
        blackConfig.setIps(CommUtils.appendComma(blackConfig.getIps()));
        this.blackConfig = blackConfig;
    }

    public WhiteBlackConfigVo getBlackConfig(){
        return blackConfig;
    }

    private void addLimit(BlackStrategy limitStrategy) {
        blackStrategieList.add(limitStrategy);
    }

    private BlackStrategy blackStrategy;
    public void setBlackStrategy(BlackStrategy black){
        this.blackStrategy = black;
    }
    public BlackStrategy getBlackStrategy(){
        return blackStrategy;
    }


    public void clearLimit() {
        pos = 0;
    }

    public void setFreqTypeItems(String freqTypeItems){
        this.freqTypeItems = freqTypeItems;
    }
    private static String freqLimitItemStr = null;
    private static String[] freqLimitItems = null;

    private String[] getLimitItems(String limitItems) {
        if (freqLimitItems == null || !StringUtils.equals(limitItems, freqLimitItemStr)) {
            logger.info("----getLimitItems--limitItems={}", limitItems);
            freqLimitItemStr=limitItems;
            freqLimitItems = limitItems.split(",");
        }
        return freqLimitItems;
    }

    private String limitItemsLast;
    /**
     * 按limitItems过滤及重新排序
     * @param limitItems
     */
    public void sortStrategies(String limitItems) {
        //配置值相同不用继续处理
        if(StringUtils.equals(limitItems, limitItemsLast)){
            return;
        }
        this.limitItemsLast=limitItems;
        this.setFreqTypeItems(limitItems);

        String[] items = getLimitItems(limitItems);
        List<BlackStrategy> limits = new ArrayList<>();
        for (String item : items) {
            if ("".equals(item)) {
                continue;
            }
            for (BlackStrategy strategy : blackStrategieList) {
                if (item.equals(strategy.getLimitType())) {
                    limits.add(strategy);
                    break;
                }
            }
        }
        blackStrategies = limits;
        size = blackStrategies.size();

    }


    public boolean doCheckLimit(BlackChain limitChain) {
        int v = pos++;
        if (v >= size) {
            return false;
        }
        return blackStrategies.get(v).doCheckLimit(this.freqTypeItems, limitChain);
    }

}
