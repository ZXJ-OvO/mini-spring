package com.zxj.ovo.controller.factory;


import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

/**
 * @author zxj
 */
public class BeanDefinitionTest {

    public static void main(String[] args) {

        // 1、创建IOC容器，即BeanFactory  （创建工厂）
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2、创建Bean定义信息     （创建图纸）
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder
                // 指定需要构造的Bean的类型
                .genericBeanDefinition(MyBean1.class)
                // 指定Bean的作用域（单例、多例）
                .setScope(ConfigurableBeanFactory.SCOPE_SINGLETON)
                // 可以在初始化Bean对象的时候指定初始化方法，指定方法名String（可选项）
                .setInitMethodName("initMethod")
                // 获得Bean的定义信息对象  （获得图纸）
                .getBeanDefinition();

        // 3、将Bean定义信息注册到容器中，此时IOC只是保存了这个Bean的定义信息，并没有真正创建Bean  （将图纸交给工厂）
        beanFactory.registerBeanDefinition("myBean1", beanDefinition);

        // 4、 使用Bean的时候，工厂马上创建这个Bean
        System.out.println(beanFactory.getBean(MyBean1.class));

    }

    static class MyBean1 {
        public MyBean1() {
            System.out.println("MyBean1的构造器执行了");
        }

        public static void initMethod() {
            System.out.println("bean的方法执行了");
        }
    }
}
