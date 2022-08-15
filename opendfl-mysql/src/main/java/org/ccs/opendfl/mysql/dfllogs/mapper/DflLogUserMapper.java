package org.ccs.opendfl.mysql.dfllogs.mapper;

import org.ccs.opendfl.mysql.dfllogs.po.DflLogUserPo;

import tk.mybatis.mapper.common.Mapper;

/**
 * DflLogUserMapper
 * 用于非数字的userId转Long型uid，以减少日志存储量，并提高性能 Mapper
 *
 * @author chenjh
 * @date 2022-5-10 22:16:27
 */
public interface DflLogUserMapper extends Mapper<DflLogUserPo> {

}