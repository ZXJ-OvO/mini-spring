package com.zxj.ovo.controller.proxy;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Cglib 动态代理：通过生成一个被代理对象的子类实现的
 */
public class CglibProxyTest {
    public static void main(String[] args) {
        // 设置增强器
        Enhancer enhancer = new Enhancer();

        // 设置父类
        enhancer.setSuperclass(UserService.class);

        // 执行增强
        enhancer.setCallback(new MethodInterceptor() {
            /**
             * @param proxy 代理对象
             * @param method 被调用的方法
             * @param objects 方法参数
             * @param methodProxy 代理方法
             */
            @Override
            public Object intercept(Object proxy, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                // 增强
                System.out.println("Cglib代理增强--Before");

                // 目标方法调用

                // 方式一：通过反射调用目标方法，效率低，安全性差不推荐
                // Object result = method.invoke(userService, objects);

                // 方式二：这种方式会产生一个递归调用。详情：https://blog.csdn.net/z69183787/article/details/106878203
                // Object result = methodProxy.invoke(userService, objects);

                // 方式三：调用的是被代理类的方法，且不会被拦截。这种方式效率最高，因为CGLib通过生成字节码的方式创建了一个被代理对象的子类，直接进行方法调用，效率比普通的反射调用要高。
                Object result = methodProxy.invokeSuper(proxy, objects);

                // 增强
                System.out.println("Cglib代理增强--After");

                return result;
            }
        });

        // 创建代理对象
        UserService proxy$1 = (UserService) enhancer.create();

        // 执行代理对象的方法
        proxy$1.save();

    }

}


class UserService {

    public void save() {
        System.out.println("保存用户");
    }
}
