package com.freeshelf.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class FreeShelfApplication {

  public static void main(String[] args) {
    SpringApplication.run(FreeShelfApplication.class, args);
  }

}
