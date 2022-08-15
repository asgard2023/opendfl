package org.ccs.opendfl.mysql.dflsystem.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dflsystem.constant.SystemConfigCodes;
import org.ccs.opendfl.mysql.dflsystem.po.DflSystemConfigPo;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * IDflSystemConfigBiz
 * 系统参数配置(树形结构) 业务接口
 *
 * @author chenjh
 * @date 2022-5-3 20:27:48
 */
public interface IDflSystemConfigBiz extends IBaseService<DflSystemConfigPo> {

    /**
     * 系统参数配置(树形结构) 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:27:48
     */
    Integer saveDflSystemConfig(DflSystemConfigPo entity);

    /**
     * 系统参数配置(树形结构) 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:27:48
     */
    Integer updateDflSystemConfig(DflSystemConfigPo entity);

    /**
     * 将系统参数常量保存到数据库
     *
     * @param systemConfigCodes 系统参数
     * @param parentId          要保存的父节点
     * @return
     */
    public DflSystemConfigPo saveSysConfigAsync(SystemConfigCodes systemConfigCodes, Integer parentId);

    /**
     * 系统参数配置(树形结构) 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-3 20:27:48
     */
    Integer deleteDflSystemConfig(Integer id, Integer operUser, String remark);


    public List<DflSystemConfigPo> findSysconfigByParentIds(Integer confType, List<Integer> parentIds);

    public List<Map<String, Object>> findSysconfigByParentIdsCount(Integer confType, List<Integer> pidList);

    public List<DflSystemConfigPo> getSysconfigByName(Integer confType, String name);

    public void getConfigValue_evict(String configCode);

    /**
     * 如果状态无效，则自动取默认值
     * 如果已删除，有调用会重新生成新数据
     *
     * @param configCode
     * @param <E>
     * @return
     */
    <E> E getConfigValue(String configCode);

    public void getSysconfigMaxUpdateTime_evict();

    /**
     * 取未删除的最大修改时间
     *
     * @return
     */
    public Long getSysconfigMaxUpdateTime();

    /**
     * 查询有新修改的数据
     *
     * @param modifyTime
     * @return
     */
    public List<DflSystemConfigPo> findSystemConfigByNewlyModify(Long modifyTime);
}