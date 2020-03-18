package mops.zulassung2.tasks;

import mops.zulassung2.model.minio.BucketObject;
import mops.zulassung2.model.minio.MinIoHelper;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

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

  /**
   * Task that periodically deletes too old files.
   */
  @Scheduled(fixedRateString = "${check_storage_rate}")
  public void checkStorageDuration() {
    if (minIoHelper == null) {
      minIoHelper = new MinIoHelper(endpoint, accessKey, secretKey);
    }
    List<BucketObject> allObjects = minIoHelper.getAllObjects();
    for (BucketObject bucketObject : allObjects) {
      String bucketName = bucketObject.getBucketName();
      String objectName = bucketObject.getObjectName();
      Date creationDate = minIoHelper.getCreateTime(bucketName, objectName);

      Date deletionDate = getDeletionDate(creationDate);

      deleteObject(bucketName, objectName, deletionDate);
    }
  }

  private void deleteObject(String bucketName, String objectName, Date deletionDate) {
    Date currentDate = DateTime.now().toDate();
    if (deletionDate.before(currentDate)) {
      minIoHelper.removeObject(bucketName, objectName);

      deleteBucket(bucketName);
    }
  }

  private void deleteBucket(String bucketName) {
    if (minIoHelper.isBucketEmpty(bucketName)) {
      minIoHelper.removeBucket(bucketName);
    }
  }

  private Date getDeletionDate(Date creationDate) {
    Calendar c = Calendar.getInstance();
    c.setTime(creationDate);
    c.add(Calendar.YEAR, storageDuration);
    return c.getTime();
  }

}
