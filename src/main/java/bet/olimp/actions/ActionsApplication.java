package bet.olimp.actions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = {"bet.olimp.actions.repo"})
@EnableCaching
public class ActionsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActionsApplication.class, args);
    }
}
