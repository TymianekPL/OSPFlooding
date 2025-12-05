package org.tymi.ospflooding.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

     public static void main(String[] args) {
          Dotenv dotenv = Dotenv.load();

          System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
          System.setProperty("DB_USER", dotenv.get("DB_USER"));
          System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
          System.setProperty("DB_NAME", dotenv.get("DB_NAME"));

          SpringApplication.run(BackendApplication.class, args);
     }

}
