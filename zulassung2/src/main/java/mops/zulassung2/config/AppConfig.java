package mops.zulassung2.config;

import mops.zulassung2.model.tasks.DeleteStorageTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {

  @Bean
  public DeleteStorageTask task() {
    return new DeleteStorageTask();
  }
}