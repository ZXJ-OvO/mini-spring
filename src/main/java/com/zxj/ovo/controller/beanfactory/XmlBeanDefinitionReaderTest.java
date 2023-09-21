package com.zxj.ovo.controller.factory;


import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.stereotype.Component;

/**
 * @author zxj
 */
public class XmlBeanDefinitionReaderTest {

    public static void main(String[] args) {
        // 1、创建IOC容器
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

        // 2、创建XML解析的XmlBeanDefinitionReader对象，创建对象还是IOC容器
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

        // 3、指定XML的位置
        xmlBeanDefinitionReader.loadBeanDefinitions("classpath:spring.xml");

        // 4、使用Bean
        System.out.println(beanFactory.getBean(MyBean3.class));

    }


    @Component
    static class MyBean3 {

        //构造函数
        public MyBean3() {
            System.out.println("MyBean2构造函数执行了！");
        }
    }
}
