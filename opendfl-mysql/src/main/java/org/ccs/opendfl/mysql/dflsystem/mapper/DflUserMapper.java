package org.ccs.opendfl.mysql.dflsystem.mapper;

import org.apache.ibatis.annotations.Param;
import org.ccs.opendfl.mysql.base.PageVO;
import org.ccs.opendfl.mysql.dflsystem.po.DflUserPo;
import org.ccs.opendfl.mysql.vo.UserVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * DflUserMapper
 * Mapper
 *
 * @author chenjh
 * @date 2022-5-3 20:24:48
 */
public interface DflUserMapper extends Mapper<DflUserPo> {
    List<UserVo> findUserListByPage(@Param("param") DflUserPo param, @Param("page") PageVO<DflUserPo> page);

    int findUserListByPageCount(@Param("param") DflUserPo record);

}