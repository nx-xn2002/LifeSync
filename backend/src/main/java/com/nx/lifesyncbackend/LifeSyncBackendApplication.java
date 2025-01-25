package com.nx.lifesyncbackend;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * life sync backend application
 *
 * @author nx-xn2002
 * @date 2025-01-04
 */
@SpringBootApplication
@MapperScan("com.nx.lifesyncbackend.mapper")
@Slf4j
public class LifeSyncBackendApplication {
    public static void main(String[] args) throws UnknownHostException {
        SpringApplication app = new SpringApplication(LifeSyncBackendApplication.class);
        ConfigurableApplicationContext application = app.run(args);
        Environment env = application.getEnvironment();
        log.info("""
                        
                        ----------------------------------------------------------
                        \tApplication '{}' is running! Access URLs:
                        \tDoc: \thttp://{}:{}{}/doc.html
                        \tDoc: \thttp://localhost:{}{}/doc.html
                        ----------------------------------------------------------""",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path"),
                env.getProperty("server.port"),
                env.getProperty("server.servlet.context-path"));
    }

}
