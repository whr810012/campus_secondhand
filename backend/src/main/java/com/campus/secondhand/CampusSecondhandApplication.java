package com.campus.secondhand;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 校园二手交易平台主启动类
 *
 * @author campus-secondhand
 */
@SpringBootApplication
@MapperScan("com.campus.secondhand.mapper")
public class CampusSecondhandApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampusSecondhandApplication.class, args);
    }

}