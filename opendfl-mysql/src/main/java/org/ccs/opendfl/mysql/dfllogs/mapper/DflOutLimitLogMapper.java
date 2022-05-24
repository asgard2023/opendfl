package org.ccs.opendfl.mysql.dfllogs.mapper;

import org.apache.ibatis.annotations.Param;
import org.ccs.opendfl.mysql.dfllogs.po.DflOutLimitLogPo;
import org.ccs.opendfl.mysql.dfllogs.vo.DflOutLimitLogCountVo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @Version V1.0
 * @Title: DflOutLimitLogMapper
 * @Description: 频率限制超限日志 Mapper
 * @Author: Created by chenjh
 * @Date: 2022-5-6 23:21:44
 */
public interface DflOutLimitLogMapper extends Mapper<DflOutLimitLogPo> {
    /**
     * 频率超限日志统计
     *
     * @param limitType
     * @param limitCount
     * @param timeSecond
     * @param uid
     * @param startTime
     * @param endTime
     * @return
     */
    public List<DflOutLimitLogCountVo> countFreqLogs(@Param("uriId") Integer uriId, @Param("findType") String findType, @Param("limitType") String limitType, @Param("limitCount") Integer limitCount
            , @Param("timeSecond") Integer timeSecond, @Param("uid") Long uid
            , @Param("startTime") String startTime, @Param("endTime") String endTime);
}