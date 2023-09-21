package com.zxj.ovo.controller;


import com.zxj.ovo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Locale;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Resource
    private UserService userService;


    /***
     * 案例
     */
    @GetMapping(value = "/info")
    public String info(Locale locale) {
        return userService.userInfo(locale);
    }
}
