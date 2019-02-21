package com.example.demo.shiro;

import com.example.demo.entity.User;
import com.example.demo.service.serviceImpl.UserServiceIpml;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 自定义 Realm
 * @author OYL
 */
public class UserRealm extends AuthorizingRealm {

    @Autowired
    private UserServiceIpml serviceIpml;

    /**
     * 执行授权逻辑
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        System.out.println("执行授权逻辑");

        // 给 资源进行授权
        SimpleAuthorizationInfo info =new SimpleAuthorizationInfo();
        //添加资源的授权字符串
        //info.addStringPermission("user:add");

        //获取数据库查询当前登录用户的授权字符串
        //获取当前登录用户
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();

        User byName = serviceIpml.findByName(user.getName());
        info.addStringPermission(byName.getPermit());

        return info;
    }

    /**
     *
     * 执行认证逻辑
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("执行认证逻辑");

        // 编写 shiro判断逻辑 判断用户名和密码
        //1.判断用户名
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        User user = serviceIpml.findByName(token.getUsername());
        if( user == null ){
            //用户名不存在
            return null;//shiro底层会抛出UnknowAccountException
        }

        /**
         *  返回数据信息，系统自动比较输入密码
         *  第一个参数 返回login的信息 token.login();
         *  第二个参数 数据库获取的密码
         *  第三个参数 realm的名称
         */
        return new SimpleAuthenticationInfo(user,user.getPassword(),"");
    }

}
