package com.zxj.ovo.controller.springmvc;


import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11Nio2Protocol;

import javax.servlet.ServletConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/*
     Tomcat 的组件：
     Tomcat 代表整个服务器
     Context 代表 Servlet 运行容器，可以向其中添加 Servlet，jsp，静态页面等
     这些内容可以来自文件，也可以通过代码动态添加
     Connector 连接器，用来监听客户端连接，并提供线程
     Servlet 用来处理请求、返回响应
 */

/**
 * 1、DispatcherServlet -> 拦截所有请求
 */
public class DispatcherServlet extends HttpServlet {

    /**
     * 重写service方法，它可以处理所有类型的请求，比doGet、doPost更强大
     */
    @Override
    @SneakyThrows
    public void service(ServletRequest req, ServletResponse resp) {

        // 转换 请求封装对象、响应封装对象  转换后才能获取更多的信息
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        // 获取请求的方式
        String method = request.getMethod();
        System.out.println("拦截的请求方式：" + method);

        // 获取请求的路径
        String uri = request.getRequestURI();
        System.out.println("拦截的请求路径：" + uri);

        // 排除图标的请求
        if (uri.equals("/favicon.ico")) {
            return;
        }

        // 获取请求处理器
        Method handler = HandlerMapping.getHandler(uri);

        // 调用目标方法
        Object result = HandlerAdapter.invoke(handler, request);

        // 返回响应结果
        ViewResolver.view(response, result);

    }


    /**
     * 重写初始化方法，实现加载的任务  初始化工作只会在所有请求之前执行一次
     */
    @SneakyThrows
    @Override
    public void init(ServletConfig servletConfig) {
        // 加载所有的类，解析类上的注解，将请求路径和方法对应起来
        HandlerMapping.parse(UserController.class);
        HandlerMapping.parse(ItemController.class);
    }


    /**
     * Tomcat服务器启动的入口
     */
    @SneakyThrows
    public static void main(String[] args) {
        // 1、创建Tomcat服务器对象
        Tomcat tomcat = new Tomcat();

        // 2、设置tomcat的工作目录，tomcat通过内嵌的方式进入项目，会在项目下创建一个tomcat文件夹
        tomcat.setBaseDir("tomcat");

        // 3、设置Tomcat运行期间产生的临时文件存放的未知
        File file = Files.createTempDirectory("tomcat_temp").toFile();

        // 4、当程序退出时，删除本次创建的临时目录
        file.deleteOnExit();

        // 5、创建Servlet运行容器，可以向其中添加Servlet，jsp，静态页面等
        //    第1个参数如果为空，则表示默认的空间
        //    第2个填写前面创建的临时路径
        Context context = tomcat.addContext("", file.getAbsolutePath());

        // 6、将DispatcherServlet注册到Tomcat的web服务中
        /*  context.addServletContainerInitializer(new ServletContainerInitializer() {

            @Override
            public void onStartup(Set<Class<?>> c, ServletContext ctx) {
                //tomcat启动后，会调用该方法
                //添加Servlet
                ctx.addServlet("baseController", new DispatcherServlet())
                        //添加一个访问路径
                        .addMapping("/");
            }
        }, null);*/

        context.addServletContainerInitializer((c, ctx) -> {
            ctx.addServlet("dispatcherServlet", new DispatcherServlet())
                    .addMapping("/");
        }, null);

        // 7、启动tomcat服务器，start方法中有一个死循环，会不断监听端口并等待请求
        tomcat.start();

        // 8、配置连接器，配置端口、设置协议等
        Connector connector = new Connector(new Http11Nio2Protocol()); // 设置Http协议
        connector.setPort(8080); // 指定端口

        // 9、设置连接器
        tomcat.setConnector(connector);
    }
}


/**
 * 2、处理器映射器 -> 会根据用户请求路径，获取处理当前请求的Handler（处理当前请求的方法）
 */
class HandlerMapping {

    // 存储所有请求路径和被调用的方法的映射关系
    private static Map<String, Method> mappingMethods = new HashMap<>();

    // 初始化解析所有路径
    public static void parse(Class<?> clazz) {
        // 获取clazz的方法 循环所有方法，找到被RequestMapping注解的方法
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                // 获取注解
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                // 获取注解的值，此时值就是uri
                String uri = annotation.value();
                // 将uri和method存入map中
                mappingMethods.put(uri, method);
            }
        }
    }


    /**
     * 根据请求uri去Map中获取请求处理器
     */
    static Method getHandler(String uri) {
        return mappingMethods.get(uri);
    }
}


/**
 * 3、处理器适配器 -> 找到处理器后，通过HandlerAdapter调用目标方法，并拿到返回结果
 * 如果返回结果是JSON，就返回JSON
 * 如果不是JSON，就封装成ModelAndView，并通过视图解析器ViewResolver执行视图渲染
 */
class HandlerAdapter {
    // 执行目标方法调用
    @SneakyThrows
    public static Object invoke(Method method, HttpServletRequest httpServletRequest) {
        // 创建方法所在类的对象
        Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
        // 参数解析器解析参数
        return method.invoke(instance, ArgumentResolver.parametersHandler(method, httpServletRequest));
    }
}


/**
 * 4、视图解析器 -> 对当前响应信息进行解析，将View和Model单独拆分后，进行视图渲染，返回数据（这里可以是转发，也可以是重定向）
 */
class ViewResolver {
    // 响应用户请求
    @SneakyThrows
    public static void view(HttpServletResponse response, Object result) {
        // 判断请求处理结果是视图还是数据 如果是视图，就跳转，如果是数据，就返回json
        String uri = result.toString();
        if (uri.startsWith("redirect:")) {
            // replace方法是将redirect:替换成空字符串，得到的就是跳转的路径
            String url = uri.replace("redirect:", "");
            // 执行跳转
            response.sendRedirect(url);
        } else {
            // 设置响应类型
            response.setHeader("content-type", "application/json");
            // 响应数据
            response.getWriter().print(JSON.toJSONString(result));
            // 关闭资源
            response.getWriter().close();
        }
    }
}


/**
 * 参数解析器
 */
class ArgumentResolver {
    //参数处理
    @SneakyThrows
    public static Object parametersHandler(Method method, HttpServletRequest request) {

        // 获取入参集合
        Class<?>[] parameterTypes = method.getParameterTypes();

        // 遍历入参集合
        for (Class<?> type : parameterTypes) {
            // 判断方法入参是什么类型，如果不是java.lang包下的，就是自定义类型
            if (!type.getName().startsWith("java.lang")) {
                // 如参实例
                Object instance = type.getDeclaredConstructor().newInstance();

                // 构建一个实体bean，并获取前端传入的值，并赋值
                for (Field field : type.getDeclaredFields()) {
                    field.setAccessible(true);
                    String key = field.getName();
                    String parameterValue = request.getParameter(key);
                    field.set(instance, parameterValue);
                }
                return instance;
            }
        }
        return null;
    }
}


/**
 * 自定义注解用于标记方法获取请求路径
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface RequestMapping {
    String value();
}


/**
 * 测试用的UserController
 */
class UserController {
    @RequestMapping(value = "/user/one")
    public String one() {
        return "hello user one info";
    }

    @RequestMapping(value = "/user/add")
    public String add(User user) {
        System.out.println(user); // FIXME: 2023/9/19 拿到的参数是null
        return "hello user info!";
    }

    @RequestMapping(value = "/user/itheima")
    public String itheima() {
        return "redirect:http://www.itheima.com";
    }
}


/**
 * 测试用的ItemController
 */
class ItemController {
    @RequestMapping(value = "/item/one")
    public String one() {
        return "hello item one info";
    }
}


/**
 * 测试用的User
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
class User {
    private String name;
    private Integer age;
}

