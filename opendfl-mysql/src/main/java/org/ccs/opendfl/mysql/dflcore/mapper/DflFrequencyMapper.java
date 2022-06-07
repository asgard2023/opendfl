package org.ccs.opendfl.mysql.dflcore.mapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

/**
 * @Version V1.0
 * @Title: DflFrequencyMapper
 * @Description: 频率限制配置表 Mapper
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:43:11
 */
public interface DflFrequencyMapper extends Mapper<DflFrequencyPo> {
    @Select("select max(modify_time) from dfl_frequency where if_del=0 and status=1 and uri=#{uri}")
    public Date getFrequencyByUriMaxUpdateTime(@Param("uri") String uri);

    @Select("select max(modify_time) from dfl_frequency where if_del=0")
    public Date getFrequencyMaxUpdateTime();
}