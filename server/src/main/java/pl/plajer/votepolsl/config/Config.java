package pl.plajer.votepolsl.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @author Plajer
 * <p>
 * Created at 08.10.2022
 */
@Configuration
@EnableScheduling
@EnableWebSecurity
public class Config extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity security) throws Exception {
    security.csrf().disable().httpBasic().disable().cors().disable().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
  }

}
