package jieyi.lu.huanju;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
@SpringBootApplication 相当于下面三个注解的合并：

@SpringBootConfiguration
    类似@Configuration
@EnableAutoConfiguration
    配置 DispatcherServlet
    配置 Tomcat 内嵌服务器
    配置 Jackson JSON 转换器
    配置 ViewResolver
    配置 20+ 个 Web 相关组件！
@ComponentScan
    扫描当前包及其子包下的所有组件
*/
@SpringBootApplication
public class HuanJuApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuanJuApplication.class, args);
    }

}
