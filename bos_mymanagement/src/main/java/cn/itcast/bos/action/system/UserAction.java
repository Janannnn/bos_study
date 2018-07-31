package cn.itcast.bos.action.system;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.UserService;
import com.opensymphony.xwork2.ActionContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
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
public class UserAction extends BaseAction<User> {
    @Autowired
    private UserService userService;

    private String[] roleIds;

    public void setRoleIds(String[] roleIds) {
        this.roleIds = roleIds;
    }

    @Action(value = "user_login", results = {
            @Result(name = "login", type = "redirect", location = "login.html"),
            @Result(name = "success", type = "redirect", location = "index.html")})
    public String login() {
        //用户名和密码都保存在model中
        //基于shiro实现登录
        Subject subject = SecurityUtils.getSubject();

        //用户名和密码信息
        AuthenticationToken token = new UsernamePasswordToken(
                model.getUsername(), model.getPassword());
        try {
            subject.login(token);
            //认证成功
            //将用户信息保存到Session
            return SUCCESS;
        } catch (AuthenticationException e) {
            return LOGIN;
        }

    }

    //用户退出
    @Action(value = "user_logout", results = {@Result(name = "success", type = "redirect", location = "login.html")})
    public String logOut() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return SUCCESS;
    }

    //用户列表查询
    @Action(value = "user_list", results = {@Result(name = "success", type = "json")})
    public String userList() {
        List<User> userList = userService.findAll();
        ActionContext.getContext().getValueStack().push(userList);
        return SUCCESS;
    }

    //用户的添加
    @Action(value = "user_save", results = {@Result(name = "success", type = "redirect", location = "./pages/system/userlist.html")})
    public String userSave() {
        userService.save(model, roleIds);
        return SUCCESS;

    }
}
