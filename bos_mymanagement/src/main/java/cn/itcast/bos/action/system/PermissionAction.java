package cn.itcast.bos.action.system;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.service.system.PermissionService;
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
public class PermissionAction extends BaseAction<Permission> {
    @Autowired
    private PermissionService permissionService;

    @Action(value = "permission_list",results = {@Result(name = "success",type = "json")})
    public String permissionList(){
        List<Permission> permissionList= permissionService.findAll();
        ActionContext.getContext().getValueStack().push(permissionList);
        return SUCCESS;
    }
    @Action(value = "permission_save",results = {@Result(name = "success",type = "redirect",location = "./pages/system/permission.html")})
    public String save(){
        permissionService.save(model);
        return SUCCESS;
    }
}
