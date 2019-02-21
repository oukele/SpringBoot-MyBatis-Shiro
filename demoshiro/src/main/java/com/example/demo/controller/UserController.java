package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.serviceImpl.UserServiceIpml;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    /*
    *  测试方法
    * */
    @GetMapping(path = "/hello")
    public String hello(){
        System.out.println("UserController.hello()");
        return "ok";
    }

    /**
     *  测试 新增方法
     */
    @GetMapping(path = "/add")
    public String add(){
        return "新增成功";
    }

    /**
     *  测试 更新方法
     */
    @GetMapping(path = "/update")
    public String update(){
        return "更新成功";
    }

    /**
     *  测试 登录方法
     */
    @PostMapping(path = "/login")
    public String login(@RequestParam("name") String name ,@RequestParam("password") String password){

        /**
         * 使用 shiro编写认证操作
         */
        //1.获取Subject
        Subject subject = SecurityUtils.getSubject();

        //封张用户数据
        UsernamePasswordToken token = new UsernamePasswordToken(name,password);

        //执行登录
        try{
            subject.login(token);
            //登录成功
            return "{msg:登陆成功}";
        }catch (UnknownAccountException e){
            return "{msg:账号不存在}";
        }catch (IncorrectCredentialsException e){
            return "{msg:密码错误}";
        }
    }

    /**
     *  管理员方法
     */
    @GetMapping(path = "/admin/add")
    public String adminAdd(){
        return "某位管理员点击了新增方法";
    }
    @GetMapping(path = "/admin/update")
    public String adminUpdate(){
        return "某位管理员点击了更新方法";
    }

    /**
     *  根据用户名 查询数据
     * @param name
     * @return User
     */
    @Autowired
    private UserServiceIpml userServiceIpml;

    @GetMapping(path = "/get/{name}")
    public User findByName(@PathVariable("name") String name){
        return userServiceIpml.findByName(name);
    }

    /**
     *  未授权提示url
     */
    @GetMapping(path = "/noAuth")
    public String unAuth(){
        return "{msg:您未经授权}";
    }
}
