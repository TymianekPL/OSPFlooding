package org.tymi.ospflooding.backend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class BackendApplication {

     public static void main(String[] args) {
          Path envPath = Paths.get(".env");
          if (!Files.exists(envPath)) {
               envPath = Paths.get("backend/.env");
          }

          Dotenv dotenv = Dotenv.configure()
                  .filename(envPath.toString())
                  .load();

          System.setProperty("spring.datasource.url",
                  "jdbc:mariadb://localhost:" + dotenv.get("DB_PORT") + "/" + dotenv.get("DB_NAME"));
          System.setProperty("spring.datasource.username", dotenv.get("DB_USER"));
          System.setProperty("spring.datasource.password", dotenv.get("DB_PASSWORD"));
          System.setProperty("spring.datasource.driver-class-name", "org.mariadb.jdbc.Driver");
          System.setProperty("WCS_UUID", dotenv.get("WCS_UUID"));

          SpringApplication.run(BackendApplication.class, args);
     }

}
