package cn.itcast.bos.action.system;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.service.system.RoleService;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class RoleAction extends BaseAction<Role> {

    private String[] permissionIds;
    private String menuIds;

    public void setPermissionIds(String[] permissionIds) {
        this.permissionIds = permissionIds;
    }

    public void setMenuIds(String menuIds) {
        this.menuIds = menuIds;
    }

    @Autowired
    private RoleService roleService;

    @Action(value = "role_list",results = {@Result(name = "success",type = "json")})
    public String roleList(){
        List<Role> roleList = roleService.findAll();
        ActionContext.getContext().getValueStack().push(roleList);
        return SUCCESS;
    }

    @Action(value = "role_save",results = {@Result(name = "success",type = "redirect",location = "./pages/system/role.html")})
    public String roleSave(){
        System.out.println("菜单id："+menuIds);
        System.out.println(permissionIds);
        roleService.save(model,menuIds,permissionIds);
        return SUCCESS;
    }
}
