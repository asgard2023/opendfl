package org.ccs.opendfl.mysql.dfllogs.biz;

import org.ccs.opendfl.mysql.base.IBaseService;
import org.ccs.opendfl.mysql.dfllogs.po.DflRequestScansPo;

import java.util.List;
import java.util.Map;

/**
 * IDflRequestScansBiz
 * 所有接口方法（通过扫码所有controller接口） 业务接口
 *
 * @author chenjh
 * @date 2022-5-10 22:12:23
 */
public interface IDflRequestScansBiz extends IBaseService<DflRequestScansPo> {

    public Integer getUriId(String uri);

    public Map<Integer, DflRequestScansPo> getUriPos(List<Integer> uriIdList);

    /**
     * 所有接口方法（通过扫码所有controller接口） 保存
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-10 22:12:23
     */
    Integer saveDflRequestScans(DflRequestScansPo entity);

    /**
     * 所有接口方法（通过扫码所有controller接口） 更新
     *
     * @param entity
     * @return Integer
     * @author chenjh
     * @date 2022-5-10 22:12:23
     */
    Integer updateDflRequestScans(DflRequestScansPo entity);

    /**
     * 所有接口方法（通过扫码所有controller接口） 删除
     *
     * @param id       主键ID
     * @param operUser 操作人
     * @param remark   备注
     * @return Integer
     * @author chenjh
     * @date 2022-5-10 22:12:23
     */
    Integer deleteDflRequestScans(Integer id, Integer operUser, String remark);

    public Integer updateScanController(String pkg, Integer operUser);
}