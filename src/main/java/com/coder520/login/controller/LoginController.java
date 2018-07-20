package com.coder520.login.controller;

import com.coder520.common.utils.MD5Utils;
import com.coder520.user.entity.User;
import com.coder520.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;


@Controller
@RequestMapping("login")
public class LoginController {

    @Autowired
    private UserService userService;

    @RequestMapping()
    public String login() {
        return "login";
    }


    @RequestMapping("/check")
    @ResponseBody
    public String checkLogin(HttpServletRequest request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        String username = request.getParameter("username");
        String pwd = request.getParameter("password");

         //查数据库 如果查到数据  调用MD5加密对比密码
        User user = userService.findUserByUserName(username);
        if(user!=null){
            if(MD5Utils.checkPassword(pwd,user.getPassword())){
                //校验成功  设置session
                request.getSession().setAttribute("userinfo",user);
                return "login_succ";
            }else{
                // 校验失败  返回校验失败signal
                return "login_fail";
            }
        }else {
            // 校验失败  返回校验失败signal
            return "login_fail";
        }

}
    /**
     *@Author HJY
     *@Date
     *@Description: 本方法仅通过postman 发送json注册用户
    */
    @RequestMapping("/register")
    @ResponseBody
    public String register(@RequestBody User user) throws UnsupportedEncodingException, NoSuchAlgorithmException {

        userService.createUser(user);

        return "succ";
    }



}
