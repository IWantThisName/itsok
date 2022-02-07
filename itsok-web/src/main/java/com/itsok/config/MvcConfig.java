package com.itsok.config;

import com.itsok.utils.AsmContext;
import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.annotation.PostConstruct;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

@Configuration
@EnableWebMvc
public class MvcConfig {

    private Logger logger = LoggerFactory.getLogger(MvcConfig.class);

    @PostConstruct
    public void init() {
        try {
            AsmContext.getInstance();
            //attach当前jvm,并且增强指定类
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            String pid = runtime.getName().split("@")[0];
            System.out.println(pid);
            VirtualMachine virtualMachine = null;
            virtualMachine = VirtualMachine.attach(pid);
            virtualMachine.loadAgent("E:\\sourcecode\\itsok\\itsok-agent\\target\\sandbox-agent-1.0-SNAPSHOT-jar-with-dependencies.jar");
            logger.error("agent代理加载成功");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Bean
    public ItsokWebmvcConfigurer webmvcConfigurer() {
        return new ItsokWebmvcConfigurer();
    }

}
