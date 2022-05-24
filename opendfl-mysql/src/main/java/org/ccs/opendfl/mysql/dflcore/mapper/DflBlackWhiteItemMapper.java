package org.ccs.opendfl.mysql.dflcore.mapper;

import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Select;
import org.ccs.opendfl.mysql.dflcore.po.DflBlackWhiteItemPo;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;

/**
 * @Version V1.0
 * @Title: DflBlackWhiteItemMapper
 * @Description: 黑名单 Mapper
 * @Author: Created by chenjh
 * @Date: 2022-5-18 21:45:02
 */
public interface DflBlackWhiteItemMapper extends Mapper<DflBlackWhiteItemPo> {
    @Select("select if(typeMaxTime>itemMaxTime, typeMaxTime, itemMaxTime) from ( " +
            " select max(bl.modify_time) typeMaxTime, max(item.modify_time) itemMaxTime " +
            " from dfl_black_white bl,dfl_black_white_item item" +
            " where bl.id=item.blackwhite_id and  bl.type=#{type} and (#{limitType} is null || bl.limit_type=#{limitType})" +
            ") t ")
    public Date findBlackWhiteMaxUpdateTime(@Param("type")String type, @Param("limitType") Integer limitType);
}