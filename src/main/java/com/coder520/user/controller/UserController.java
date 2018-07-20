package com.coder520.user.controller;


import com.coder520.user.entity.User;
import com.coder520.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/home")
    public String home(){

        return "home";
    }


    @RequestMapping("/userinfo")
    @ResponseBody //该注解将返回对象以json呈现给浏览器
    public User getUser(HttpSession session){
        User user = (User) session.getAttribute("userinfo");
        return user;
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session){
        session.invalidate();
        return "login";
    }
}
