package com.zxj.ovo.controller.factory;


import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.stereotype.Component;

/**
 * @author zxj
 */
public class ComponentTest {

    public static void main(String[] args) {
        // 1、创建IOC容器
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2、扫描指定包 创建对象实例由IOC容器完成
        ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(beanFactory);

        // 3、扫描指定包
        classPathBeanDefinitionScanner.scan("com.zxj.ovo.factory");

        // 4、从容器获取Bean
        System.out.println(beanFactory.getBean(MyBean2.class));
    }


    @Component
    static class MyBean2 {

        //构造函数
        public MyBean2() {
            System.out.println("MyBean2构造函数执行了！");
        }
    }
}
