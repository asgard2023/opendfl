package org.ccs.opendfl.mysql.dfllogs.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.base.MyPageInfo;
import org.ccs.opendfl.mysql.dfllogs.po.DflOutLimitLogPo;
import org.ccs.opendfl.mysql.dfllogs.vo.DflOutLimitLogCountVo;

import java.util.List;
import java.util.Map;

/**
 * IDflOutLimitLogBiz
 * 频率限制超限日志 业务接口
 *
 * @author chenjh
 * @date 2022-5-6 23:21:44
 */
public interface IDflOutLimitLogBiz extends IBaseService<DflOutLimitLogPo> {

    /**
     * 频率限制超限日志 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-6 23:21:44
     */
    Integer saveDflOutLimitLog(DflOutLimitLogPo entity);

    /**
     * 频率限制超限日志 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-6 23:21:44
     */
    Integer updateDflOutLimitLog(DflOutLimitLogPo entity);

    /**
     * 频率限制超限日志 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-6 23:21:44
     */
    Integer deleteDflOutLimitLog(Long id, Integer operUser, String remark);

    /**
     * 频率超限日志统计
     *
     * @param entity 对象
     * @param pageInfo 翻页对象
     * @param paramsMap
     * @return
     */
    public List<DflOutLimitLogCountVo> countFreqLogs(DflOutLimitLogPo entity, Map<String, Object> paramsMap, MyPageInfo pageInfo);
}