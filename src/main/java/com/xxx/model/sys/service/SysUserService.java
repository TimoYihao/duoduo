package com.xxx.model.sys.service;

import com.xxx.common.framework.base.BaseService;
import com.xxx.common.util.DictionaryUtils;
import com.xxx.common.util.MD5Utils;
import com.xxx.common.util.StringUtil;
import com.xxx.model.base.dao.IdentityDAO;
import com.xxx.model.base.entity.Identity;
import com.xxx.model.base.service.InformationService;
import com.xxx.model.sys.dao.SysUserDAO;
import com.xxx.model.sys.entity.SysUser;
import com.xxx.model.sys.entity.SysUserRole;
import com.xxx.model.sys.vo.resp.SysUserResp;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 系统用户业务逻辑类
 */
@Service
public class SysUserService extends BaseService<SysUser> {

	@Resource
	private SysUserDAO sysUserDAO;

	@Resource
	private SysUserRoleService sysUserRoleService;

	@Resource
	private IdentityDAO identityDAO;

	@Resource
	private InformationService informationService;

	/** 用户登录成功 */
	@Transactional
	public void userLogin(Long userId){
		sysUserDAO.userLogin(userId);
	}

	/** 添加、修改用户信息 */
	@Transactional
	public void save(SysUser sysUser,Long[] roleIds,Long loginUserId){
		sysUser.setUpdateBy(loginUserId);
		//判断是新增或者是修改
		Date date = new Date();
		if(sysUser.getId()==null){
			sysUser.setCreateBy(loginUserId);
			sysUser.setStatus(1);
			sysUser.setUpdateTime(date);
			sysUser.setCreateTime(date);
			this.insert(sysUser);
		}else{
			sysUserRoleService.deleteByUserId(sysUser.getId());
			sysUser.setUpdateTime(date);
			this.updateNotNull(sysUser);
		}
		for(Long roleId : roleIds) {
			SysUserRole sysUserRole = new SysUserRole();
			sysUserRole.setSysRoleId(roleId);
			sysUserRole.setSysUserId(sysUser.getId());
			sysUserRoleService.insert(sysUserRole);
		}
	}

	/** 获取用户详情 */
	public SysUserResp info(Long userId){
		SysUser sysUser = this.selectById(userId);
		SysUserRole sysUserRole = new SysUserRole();
		sysUserRole.setSysUserId(userId);
		List<Long> roleIds = sysUserRoleService.selectAll(sysUserRole).stream().map(userRole -> userRole.getSysRoleId()).collect(Collectors.toList());
		SysUserResp sysUserResp = new SysUserResp();
		BeanUtils.copyProperties(sysUser,sysUserResp);
		sysUserResp.setRoleIds(roleIds);
		return sysUserResp;
	}

	public List<Map<String,Object>> findListByDepartment(int department){
		return sysUserDAO.findListByDepartment(department);
	}

	public List<Map<String,Object>> findListByDepartmentAll(int department){
		return sysUserDAO.findListByDepartmentAll(department);
	}

	public List<Map<String,Object>> findListByState(int state){
		return sysUserDAO.findListByState(state);
	}

	public SysUser selectOne(String username, String password) {
		return sysUserDAO.selectOne(username,password);
	}

	public Map<String,Object> exist(String username, String password){
		return sysUserDAO.exist(username, password);
	}

	public Map<String,Object> findUserById(int id,String url){
		Map<String,Object> map;
		SysUser sysUser = sysUserDAO.selectById(id);
		if(sysUser.getLevel()==3){
			map = identityDAO.findIdentityById(id);
			map.put("id",id);
			map.put("username",sysUser.getUsername());
			map.put("imgA", url + map.get("imgA"));
			map.put("imgB", url + map.get("imgB"));
		}else{
			map = sysUserDAO.findUserById(id);
		}
		return map;
	}

	public int findNum(){
		return sysUserDAO.findNum();
	}

	public int findDepartmentById(int id){
		return sysUserDAO.findDepartmentById(id);
	}

	public void add(SysUser sysUser,String level,String imgA,String imgB){
		Date time = new Date();
		sysUser.setUsername(sysUser.getName());
		sysUser.setPassword(MD5Utils.getMD5String("123456"));
		sysUser.setImage("");
		sysUser.setLoginTime(time);
		sysUser.setErrorDate(time);
		sysUser.setErrorTime(0);
		sysUser.setCreateTime(time);
		sysUser.setCreateBy(1L);
		sysUser.setUpdateTime(time);
		sysUser.setUpdateBy(1L);
		sysUserDAO.insert(sysUser);
		if("2".equals(level)){
			Identity identity = new Identity();
			identity.setUser(sysUser.getId().intValue());
			identity.setName(sysUser.getName());
			identity.setPhone(sysUser.getPhone());
			identity.setImgA(imgA);
			identity.setImgB(imgB);
			identity.setCreatetime(time);
			identityDAO.insert(identity);
		}
	}

	public void update(SysUser sysUser,String imgA,String imgB){
		sysUser.setStatus(0);
		sysUserDAO.updateById(sysUser);
		Identity identity = new Identity();
		identity.setId(identityDAO.findIdByUserId(sysUser.getId().intValue()));
		identity.setName(sysUser.getName());
		identity.setPhone(sysUser.getPhone());
		if(StringUtil.notEmpty(imgA)){
			identity.setImgA(imgA);
		}
		if(StringUtil.notEmpty(imgB)){
			identity.setImgB(imgB);
		}
		identityDAO.updateById(identity);
	}

	public boolean judge_password(Long id,String password){
		boolean b = false;
		SysUser user = sysUserDAO.selectById(id);
		if(MD5Utils.getMD5String(password).equals(user.getPassword())){
			b = true;
		}
		return b;
	}

	public void update_password(Long id,String password){
		SysUser user = new SysUser();
		user.setId(id);
		user.setPassword(MD5Utils.MD5(password));
		sysUserDAO.updateById(user);
	}

	public void update_image(Long id,String iamge){
		SysUser user = new SysUser();
		user.setId(id);
		user.setImage(iamge);
		sysUserDAO.updateById(user);
	}

    public void accredit(Long id,Integer status) {
		sysUserDAO.accredit(id,status);
    }

	public void examine(int id,String state,String remarks){
		SysUser user = sysUserDAO.selectById(id);
		user.setStatus(Integer.valueOf(state));
		sysUserDAO.updateById(user);
		if("2".equals(state)){
			informationService.add(sysUserDAO.findIdByDepartment(user.getDepartmentId().intValue()),remarks,"3");
		}
	}
}