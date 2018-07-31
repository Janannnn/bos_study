package cn.itcast.bos.realm;

import cn.itcast.bos.domain.system.Permission;
import cn.itcast.bos.domain.system.Role;
import cn.itcast.bos.domain.system.User;
import cn.itcast.bos.service.system.PermissionService;
import cn.itcast.bos.service.system.RoleService;
import cn.itcast.bos.service.system.UserService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("bosRealm")
public class BosRealm extends AuthorizingRealm {
    @Autowired
    private UserService userService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection pc) {
        System.out.println("授权管理");
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        //得到此用户
        Subject subject = SecurityUtils.getSubject();
        User user  = (User) subject.getPrincipal();
        //查询此用户所有角色
        List<Role> roleList = roleService.findByUser(user);
        for (Role role : roleList) {
            simpleAuthorizationInfo.addRole(role.getKeyword());
        }
        //查询此用户的所有权限
        List<Permission> permissionList = permissionService.findByUser(user);
        for (Permission permission : permissionList) {

            simpleAuthorizationInfo.addStringPermission(permission.getKeyword());
        }
        return simpleAuthorizationInfo;

    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //转换
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        //根据用户名查询用户信息
        User user = userService.findByUsername(usernamePasswordToken.getUsername());
        if (user==null){
            // 用户名不存在
            // 参数一： 期望登录后，保存在Subject中信息
            // 参数二： 如果返回为null 说明用户不存在，报用户名
            // 参数三 ：realm名称
            return null;
        }else {
            // 用户名存在
            // 当返回用户密码时，securityManager安全管理器，自动比较返回密码和用户输入密码是否一致
            // 如果密码一致 登录成功， 如果密码不一致 报密码错误异常
            return new SimpleAuthenticationInfo(user, user.getPassword(),getName());
        }
    }
}
