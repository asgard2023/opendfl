package org.ccs.opendfl.mysql.dflsystem.biz.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.ccs.opendfl.mysql.dflsystem.biz.IUserBiz;
import org.ccs.opendfl.mysql.dflsystem.mapper.UserMapper;
import org.ccs.opendfl.mysql.dflsystem.po.UserPo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 *
 * @Version V1.0
*/
@Service(value = "userInfoBiz")
@Slf4j
public class UserBiz  implements IUserBiz {
	@Autowired
	private UserMapper mapper;

	@Override
	public PageInfo<UserPo> findPageBy(UserPo entity, PageInfo<UserPo> pageInfo, Map<String, Object> otherParams) {
		if(entity == null) {
			entity = new UserPo();
		}
		if(entity.getIfDel()==null){
			entity.setIfDel(0);
		}
		PageHelper.startPage(pageInfo.getPageNum(), pageInfo.getPageSize());
		List<UserPo> list = this.mapper.select(entity);
		return new PageInfo<>(list);
	}

	@Override
	public UserPo findById(Integer id) {
		return mapper.selectByPrimaryKey(id);
	}

	@Override
	public Integer saveUser(UserPo entity){
		if (entity.getCreateTime() == null) {
			entity.setCreateTime(new Date());
		}
		entity.setUpdateTime(new Date());
		if (entity.getIfDel() == null) {
			entity.setIfDel(0);
		}
		int v = this.mapper.insert(entity);
		return v;
	}

	@Override
	public Integer updateUser(UserPo entity){
		entity.setUpdateTime(new Date());
		int v = this.mapper.updateByPrimaryKeySelective(entity);
		return v;
	}

	@Override
	public Integer deleteUser(Integer id, String operUser, String remark){
		UserPo po = new UserPo();
		po.setId(id);
		po.setIfDel(1); // 0未删除,1已删除
		po.setUpdateUser(operUser);
		po.setRemark(remark);
		po.setUpdateTime(new Date());
		int v = this.mapper.updateByPrimaryKeySelective(po);
		return v;
	}
}