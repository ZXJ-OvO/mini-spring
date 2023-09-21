package com.zxj.ovo.controller.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 模拟JDK 动态代理
 */
public class JdkProxyTest {
    public static void main(String[] args) {

        // 被代理的对象
        Target target = new Target();

        // Proxy.newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)
        // 创建代理对象
        MyService proxy$1 = (MyService) Proxy.newProxyInstance(
                // ClassLoader loader       加载代理对象字节码文件
                Target.class.getClassLoader(),
                // Class<?>[] interfaces    被代理的对象实现的所有接口 -> 生成的代理对象也会实现这些接口 为了保持一样的行为
                new Class[]{MyService.class},
                // InvocationHandler h      代理对象的调用处理程序实现增强
                new InvocationHandler() {
                    /**
                     * @param proxy  代理对象
                     * @param method 被调用的方法
                     * @param args   方法的参数
                     */
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

                        // 执行增强
                        System.out.println("增强代码执行了！");

                        // 反射调用
                        Object result = method.invoke(target, args);

                        // 执行增强
                        System.out.println("资源关闭了！");

                        return result;
                    }
                });

        // 调用代理对象的方法 代理成功就会执行增强代码
        proxy$1.save();

    }
}

interface MyService {
    void save();
}

class Target implements MyService {
    @Override
    public void save() {
        System.out.println("save方法执行了！");
    }
}
