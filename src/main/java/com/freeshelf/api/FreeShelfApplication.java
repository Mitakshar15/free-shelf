package com.freeshelf.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EntityScan("com.freeshelf.api.data.domain")
@EnableCaching
public class FreeShelfApplication {

  public static void main(String[] args) {
    SpringApplication.run(FreeShelfApplication.class, args);
  }

}
