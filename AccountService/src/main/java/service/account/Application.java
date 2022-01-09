package service.account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
@PropertySource("classpath:mongodb-atlas.properties")
public class Application {

  @Autowired
  UserRepository userRepository;
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

}