package org.ccs.opendfl.mysql.dflcore.mapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;
import org.ccs.opendfl.mysql.dflcore.po.DflFrequencyPo;
import org.ccs.opendfl.mysql.dflcore.vo.DflFrequencyConfigCountVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * DflFrequencyMapper
 * 频率限制配置表 Mapper
 *
 * @author chenjh
 * @date 2022-5-18 21:43:11
 */
public interface DflFrequencyMapper extends Mapper<DflFrequencyPo> {
    @Select("select max(modify_time) from dfl_frequency where if_del=0 and status=1 and uri=#{uri}")
    public Date getFrequencyByUriMaxUpdateTime(@Param("uri") String uri);

    @Select("select max(modify_time) from dfl_frequency where if_del=0")
    public Date getFrequencyMaxUpdateTime();

    @Select("SELECT uri,uri_id uriId,code,count(*) cout,max(create_time) maxCreateTime,max(modify_time) maxModifyTime" +
            " FROM dfl_frequency WHERE if_del = 0" +
            " and (#{uri} is null or uri=#{uri})"+
            " group by uri,uri_id,code,time")
    public List<DflFrequencyConfigCountVo> uriConfigCounts(@Param("uri") String uri);
}