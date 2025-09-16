package com.campus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 青春校园二手平台主启动类
 * 
 * @author Campus Team
 * @version 1.0.0
 */
@SpringBootApplication
public class SecondhandPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecondhandPlatformApplication.class, args);
        System.out.println("\n===========================================\n" +
                "  青春校园二手平台启动成功！\n" +
                "  访问地址: http://localhost:8080\n" +
                "===========================================\n");
    }
}