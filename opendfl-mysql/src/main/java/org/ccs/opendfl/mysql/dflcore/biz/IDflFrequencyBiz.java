package org.ccs.opendfl.mysql.dflcore.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.ccs.opendfl.mysql.dflcore.vo.DflFrequencyConfigCountVo;

import java.util.List;
import java.util.Map;

/**
 * IDflFrequencyBiz
 * 频率限制配置表 业务接口
 *
 * @author chenjh
 * @date 2022-5-18 21:43:11
 */
public interface IDflFrequencyBiz extends IBaseService<DflFrequencyPo> {
    public Map<Integer, DflFrequencyPo> getFrequencyByIds(List<Integer> freqencyIdList);

    /**
     * 频率限制配置表 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:43:11
     */
    Integer saveDflFrequency(DflFrequencyPo entity);

    /**
     * 频率限制配置表 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:43:11
     */
    Integer updateDflFrequency(DflFrequencyPo entity);

    /**
     * 频率限制配置表 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-18 21:43:11
     */
    Integer deleteDflFrequency(Integer id, Integer operUser, String remark);

    public void getFrequencyByCode_evict(String code, Integer freqLimitType, Integer time);

    DflFrequencyPo getFrequencyByCode(String code, Integer freqLimitType, Integer time);

    public Integer getFrequencyIdByCode(String code, Integer freqLimitType, Integer time);

    public void getFrequencyByUri_evict(String uri);

    public List<DflFrequencyPo> getFrequencyByUri(String uri);

    public void getFrequencyByUriMaxUpdateTime_evict(String uri);

    public Long getFrequencyByUriMaxUpdateTime(String uri);

    public void getFrequencyMaxUpdateTime_evict();

    public Long getFrequencyMaxUpdateTime();

    public List<DflFrequencyPo> findFrequencyByNewlyModify(Long modifyTime);

    public List<DflFrequencyPo> getFrequencyByUris(List<String> uris, String fields);

    public MyPageInfo<DflFrequencyConfigCountVo> uriConfigCounts(DflFrequencyConfigCountVo entity, MyPageInfo<DflFrequencyConfigCountVo> pageInfo);
}