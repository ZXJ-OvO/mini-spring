package com.zxj.ovo.controller.work;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Random;


@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private ApplicationContext applicationContext;

    @GetMapping("/register")
    public String register(String phoneNumber) {
        // 生成验证码
        String verificationCode = String.valueOf(new Random().nextInt(999999));

        applicationContext.publishEvent(new UserRegisterEvent(phoneNumber, verificationCode));

        return verificationCode;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserRegisterEvent {
    private String phoneNumber;
    private String verificationCode;
}

@Component
class UserRegisterEventListener {

    @EventListener
    public void onApplicationEvent(UserRegisterEvent event) {
        System.out.println("发送验证码到手机 " + event.getPhoneNumber() + " ，验证码为: " + event.getVerificationCode());
    }
}

@Aspect
@Component
class UserRegisterAspect {

    @Before("execution(* com.zxj.ovo.controller.work.UserController.register(..))")
    public void beforeRegister(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        System.out.println("用户注册手机号为: " + args[0]);
    }

    @AfterReturning("execution(* com.zxj.ovo.controller.work.UserController.register(..))")
    public void afterRegister(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        System.out.println("手机号: " + args[0] + " 的用户成功注册！");
    }
}

