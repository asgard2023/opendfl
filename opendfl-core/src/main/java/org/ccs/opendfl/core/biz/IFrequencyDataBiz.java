package org.ccs.opendfl.core.biz;

import org.ccs.opendfl.core.vo.FrequencyVo;

import java.util.List;

/**
 * 频率已限制数据查询与管理
 * @author chenjh
 */
public interface IFrequencyDataBiz {
    /**
     * 查询用户的限制数据
     *
     * @param account attrName(默认userId)的值
     * @return 删除的Key及数量
     */
    List<FrequencyVo> limitUsers(String account);

    /**
     * 查询用户的限制数据
     *
     * @param ip 限制的IP
     * @return 删除的Key及数量
     */
    List<FrequencyVo> limitIps(String ip);


    /**
     * 删除code对应功能的全部限制
     *
     * @param code 编码
     * @param timeList 间格时间5,3600
     * @param account  attrName(默认userId)的值
     * @return 删除成功对应的key列表
     */
    List<String> freqEvictList(String code, List<Integer> timeList, String account);

    /**
     * 重置用户频率限制次数
     *
     * @param frequency 对应的频限制
     * @param account   attrName(默认userId)的值
     * @return 删除成功对应的key
     */
    String freqEvict(FrequencyVo frequency, String account);

    /**
     * 重置同一用户多个IP登入限制
     *
     * @param frequency 对应的频限制
     * @param account   attrName(默认userId)的值
     * @return 删除的Key及数量
     */
    String freqIpUserEvict(FrequencyVo frequency, String account);

    /**
     * 重置同一用户多个IP登入限制
     *
     * @param frequency 对应的频限制
     * @param account   attrName(默认userId)的值
     * @return 删除的Key及数量
     */
    String freqUserIpEvict(FrequencyVo frequency, String account);

}
