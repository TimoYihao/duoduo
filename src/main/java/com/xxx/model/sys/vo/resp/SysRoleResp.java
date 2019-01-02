package com.xxx.model.sys.vo.resp;

import java.io.Serializable;
import java.util.List;

/**
 * 系统角色出参类
 */
public class SysRoleResp implements Serializable {

	private static final long serialVersionUID = 3117491496630542942L;

	/** ID编号 */
	private Long id;
	/** 名称 */
	private String name;
	/** 是否禁用 1是 2否 */
	private Integer status;
	/** 创建时间 */
	private java.util.Date createTime;
	/** 修改时间 */
	private java.util.Date updateTime;
	/** 创建人 */
	private Long createBy;
	/** 修改人 */
	private Long updateBy;
	/** 关联菜单IDS */
	private List<Long> menuIds;

	// -------------------- GET AND SET --------------------

	public Long getId(){
		return id;
	}
	
	public void setId(Long id){
		this.id = id;
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}

	public Integer getStatus(){
		return status;
	}
	
	public void setStatus(Integer status){
		this.status = status;
	}

	public java.util.Date getCreateTime(){
		return createTime;
	}
	
	public void setCreateTime(java.util.Date createTime){
		this.createTime = createTime;
	}

	public java.util.Date getUpdateTime(){
		return updateTime;
	}
	
	public void setUpdateTime(java.util.Date updateTime){
		this.updateTime = updateTime;
	}

	public Long getCreateBy(){
		return createBy;
	}
	
	public void setCreateBy(Long createBy){
		this.createBy = createBy;
	}

	public Long getUpdateBy(){
		return updateBy;
	}
	
	public void setUpdateBy(Long updateBy){
		this.updateBy = updateBy;
	}

	public List<Long> getMenuIds() {
		return menuIds;
	}

	public void setMenuIds(List<Long> menuIds) {
		this.menuIds = menuIds;
	}
}
