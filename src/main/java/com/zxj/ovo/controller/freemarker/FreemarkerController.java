package com.zxj.ovo.controller.freemarker;


import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zxj
 */
@RestController
@RequestMapping(value = "/fk")
public class FreemarkerController {

    @Autowired
    private Configuration configuration;

    /**
     * 使用1.ftl模板生成一个新文件
     */
    @GetMapping(value = "/test")
    public String test() throws IOException, TemplateException {
        //1.加载模板
        Template template = configuration.getTemplate("1.ftl");

        //2.准备模板数据  Map
        Map<String,Object> dataMap = new HashMap<>();
        dataMap.put("uname","张三");

        //3.将模板和数据合成输出一个新文件
        FileWriter fileWriter = new FileWriter("D:/1.txt");
        template.process(dataMap,fileWriter);
        return "OK";
    }
}


@Data
class Student {
    private String name;//姓名
    private int age;//年龄
    private Date birthday;//生日
    private Float money;//钱包
}
