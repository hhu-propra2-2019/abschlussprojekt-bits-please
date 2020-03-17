package mops.zulassung2.model.tasks;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

public class DeleteStorageTask {

  @Value("${receipt_storage_duration}")
  private double storageDuration;

  @Scheduled(fixedRateString = "${check_storage_rate}")
  public void delete() {

  }

}
