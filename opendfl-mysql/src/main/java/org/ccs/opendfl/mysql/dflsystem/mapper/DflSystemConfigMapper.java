package org.ccs.opendfl.mysql.dflsystem.mapper;

import org.apache.ibatis.annotations.Param;
import org.ccs.opendfl.mysql.dflsystem.po.DflSystemConfigPo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;
import java.util.Map;

/**
 * @Version V1.0
 * @Title: DflSystemConfigMapper
 * @Description: 系统参数配置(树形结构) Mapper
 * @Author: Created by chenjh
 * @Date: 2022-5-3 20:27:48
 */
public interface DflSystemConfigMapper extends Mapper<DflSystemConfigPo> {
    public List<DflSystemConfigPo> findSysconfigAll(@Param("confType") Integer confType
            , @Param("parentCode") String parentCode);

    public List<DflSystemConfigPo> findSysconfigByParentIds(@Param("confType") Integer confType
            , @Param("pidList") List<Integer> pidList);

    public List<Map<String, Object>> findSysconfigByParentIdsCount(@Param("confType") Integer confType,
                                                                   @Param("pidList") List<Integer> pidList);
}