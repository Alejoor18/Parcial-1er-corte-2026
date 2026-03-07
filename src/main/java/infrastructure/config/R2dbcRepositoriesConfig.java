package infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "infrastructure.adapters.out.persistence")
public class R2dbcRepositoriesConfig {
}
