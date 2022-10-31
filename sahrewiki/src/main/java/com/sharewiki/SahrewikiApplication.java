package com.sharewiki;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 * @author IKY
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("com.sharewiki.mapper")
public class SahrewikiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SahrewikiApplication.class, args);
    }

}
