package cn.itcast.bos.action.system;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.system.Menu;
import cn.itcast.bos.service.system.MenuService;
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
public class MenuAction extends BaseAction<Menu> {

    @Autowired
    private MenuService menuService;

    //查询菜单列表
    @Action(value = "menu_list",results = {@Result(name = "success",type = "json")})
    public String menuList(){
        List<Menu>  menuList = menuService.findAll();
        //json:[{},{},{}]正好和zTree的zNodes属性一致
        ActionContext.getContext().getValueStack().push(menuList);
        return SUCCESS;
    }
    //保存菜单数据
    @Action(value = "menu_save",results = {@Result(name = "success",type = "redirect",location = "./pages/system/menu.html")})
    public String menuSave(){
        //没有指定父菜单项
        if(model.getParentMenu()!=null&&model.getParentMenu().getId()==0){
            model.setParentMenu(null);
        }
        menuService.save(model);
        return SUCCESS;
    }
    //根据不同用户进行动态菜单显示
    @Action(value = "menu_showmenu",results = {@Result(name = "success",type = "json")})
    public String showMenu(){
        List<Menu> menuList = menuService.findByUser();
        ActionContext.getContext().getValueStack().push(menuList);
        return SUCCESS;
    }
}
