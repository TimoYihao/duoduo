package com.xxx.model.sys.dao;


import com.xxx.common.framework.base.BaseDAO;
import com.xxx.model.sys.entity.SysMenu;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统菜单持久层接口
 */
@Component
public interface SysMenuDAO {

    /** 根据角色IDS 查询菜单集合 */
    List<SysMenu> selectByRoleIds(@Param("roleIds") List<Long> roleIds);

    @Update("update sys_menu set sys_menu.status = #{status} where id = #{id}")
    void accredit(@Param("id")Long id,@Param("status")Integer status);

    List<String> selectPermissions(@Param("roleIds") List<Long> roleIds);
}
@Component
interface AutoSysMenuDAO extends BaseDAO<SysMenu> {

}

