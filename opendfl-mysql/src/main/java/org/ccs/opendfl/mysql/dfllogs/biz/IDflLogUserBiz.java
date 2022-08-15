package org.ccs.opendfl.mysql.dfllogs.biz;

import org.ccs.opendfl.core.vo.RequestStrategyParamsVo;
import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dfllogs.po.DflLogUserPo;

import java.util.List;
import java.util.Map;

/**
 * IDflLogUserBiz
 * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 业务接口
 *
 * @author chenjh
 * @date 2022-5-10 22:16:27
 */
public interface IDflLogUserBiz extends IBaseService<DflLogUserPo> {
    public Long getUid(String userId);

    public Long getUid(String userId, String sysType, String ip);

    public Map<Long, DflLogUserPo> getUserPos(List<Long> uriIdList);

    /**
     * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-10 22:16:27
     */
    Integer saveDflLogUser(DflLogUserPo entity);

    /**
     * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-10 22:16:27
     */
    Integer updateDflLogUser(DflLogUserPo entity);

    /**
     * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-10 22:16:27
     */
    Integer deleteDflLogUser(Long id, Integer operUser, String remark);
}