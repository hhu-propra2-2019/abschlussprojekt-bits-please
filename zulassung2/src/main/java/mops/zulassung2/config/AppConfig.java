package mops.zulassung2.config;

import mops.zulassung2.model.minio.CustomDate;
import mops.zulassung2.model.minio.MinIoEndpoint;
import mops.zulassung2.tasks.DeleteStorageTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class AppConfig {

  @Value("${receipt_storage_duration}")
  private int storageDuration;
  @Value("${endpoint}")
  private String endpoint;
  @Value("${access_key}")
  private String accessKey;
  @Value("${secret_key}")
  private String secretKey;

  @Bean
  public DeleteStorageTask task() {
    MinIoEndpoint minIoEndpoint = new MinIoEndpoint(endpoint, accessKey, secretKey);
    return new DeleteStorageTask(minIoEndpoint, storageDuration, new CustomDate());
  }
}