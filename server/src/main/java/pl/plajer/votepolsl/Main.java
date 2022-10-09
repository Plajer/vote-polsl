package pl.plajer.votepolsl;

import com.cosium.spring.data.jpa.entity.graph.repository.support.EntityGraphJpaRepositoryFactoryBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@EnableJpaRepositories(repositoryFactoryBeanClass = EntityGraphJpaRepositoryFactoryBean.class)
@SpringBootApplication
public class Main {

  public static void main(String[] args) {
    boolean devMode = false;
    for(String arg : args) {
      if(arg.equals("devMode")) {
        devMode = true;
        Logger.getLogger("Dev").log(Level.INFO, "Initializing development mode.");
      }
    }
    SpringApplication app = new SpringApplication(Main.class);
    if(devMode) {
      app.setAdditionalProfiles("dev");
    } else {
      app.setAdditionalProfiles("prod");
    }
    app.run(args);
  }

}
