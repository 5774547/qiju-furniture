package com.qiju.furniture;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Qiju Furniture Application - Main Entry
 *
 * @author Qiju Team
 */
@SpringBootApplication
@MapperScan("com.qiju.furniture.**.mapper")
public class QijuFurnitureApplication {

    public static void main(String[] args) {
        SpringApplication.run(QijuFurnitureApplication.class, args);
    }
}
