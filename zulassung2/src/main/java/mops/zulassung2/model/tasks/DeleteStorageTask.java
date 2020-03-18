package mops.zulassung2.model.tasks;

import com.fasterxml.jackson.databind.ser.std.CalendarSerializer;
import mops.zulassung2.model.BucketObject;
import mops.zulassung2.model.MinIoHelper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.text.DateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeleteStorageTask {

  @Value("${receipt_storage_duration}")
  private int storageDuration;

  private MinIoHelper minIoHelper;
  @Value("${endpoint}")
  private String endpoint;
  @Value("${access_key}")
  private String accessKey;
  @Value("${secret_key}")
  private String secretKey;

  public DeleteStorageTask() {
  }

  @Scheduled(fixedRateString = "${check_storage_rate}")
  public void delete() {
    if (minIoHelper == null) {
      minIoHelper = new MinIoHelper(endpoint, accessKey, secretKey);
    }
    List<BucketObject> allObjects = minIoHelper.getAllObjects();
    for (BucketObject bucketObject : allObjects) {
      Date creationDate = minIoHelper.getCreateTime(bucketObject.getBucketName(), bucketObject.getObjectName());
      Date currentDate = DateTime.now().toDate();

      Calendar c = Calendar.getInstance();
      c.setTime(creationDate);
      c.add(Calendar.YEAR, storageDuration);
      Date newDate = c.getTime();

      if (newDate.before(currentDate)) {
        minIoHelper.removeObject(bucketObject.getBucketName(), bucketObject.getObjectName());

        if (minIoHelper.isBucketEmpty(bucketObject.getBucketName())) {
          minIoHelper.removeBucket(bucketObject.getBucketName());
        }
      }
    }
  }

}
