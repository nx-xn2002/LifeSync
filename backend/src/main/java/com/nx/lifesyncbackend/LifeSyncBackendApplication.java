package com.nx.lifesyncbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * life sync backend application
 *
 * @author nx-xn2002
 * @date 2025-01-04
 */
@SpringBootApplication
@MapperScan("com.nx.lifesyncbackend.mapper")
public class LifeSyncBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifeSyncBackendApplication.class, args);
    }

}
