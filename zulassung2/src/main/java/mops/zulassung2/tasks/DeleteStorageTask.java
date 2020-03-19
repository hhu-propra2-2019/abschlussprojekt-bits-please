package mops.zulassung2.tasks;

import mops.zulassung2.model.minio.BucketObject;
import mops.zulassung2.model.minio.CustomDateInterface;
import mops.zulassung2.model.minio.MinIoConfig;
import mops.zulassung2.model.minio.MinIoHelper;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeleteStorageTask {

  private int storageDuration;
  private MinIoHelper minIoHelper;
  private CustomDateInterface currentDate;

  /**
   * Initialize minIoHelper from given instance.
   *
   * @param helper        minIoHelper instance
   * @param storeDuration duration in years
   * @param currentDate   current date
   */

  public DeleteStorageTask(MinIoHelper helper, int storeDuration, CustomDateInterface currentDate) {
    this.minIoHelper = helper;
    this.storageDuration = storeDuration;
    this.currentDate = currentDate;
  }

  /**
   * Initialize minIoHelper from given Strings.
   *
   * @param config        object with endpoint, accessKey, secretKey
   * @param storeDuration duration in years
   * @param currentDate   current date
   */

  public DeleteStorageTask(MinIoConfig config, int storeDuration, CustomDateInterface currentDate) {
    minIoHelper = new MinIoHelper(config.endpoint, config.accessKey, config.secretKey);
    this.storageDuration = storeDuration;
    this.currentDate = currentDate;
  }

  /**
   * Task that periodically deletes too old files.
   */
  @Scheduled(fixedRateString = "${check_storage_rate}")
  public void checkStorageDuration() {

    List<BucketObject> allObjects = minIoHelper.getAllObjects();
    for (BucketObject bucketObject : allObjects) {
      String bucketName = bucketObject.getBucketName();
      String objectName = bucketObject.getObjectName();
      Date creationDate = minIoHelper.getCreateTime(bucketName, objectName);

      Date deletionDate = getDeletionDate(creationDate);

      deleteObject(bucketName, objectName, deletionDate, currentDate.getCurrentDate());
    }
  }

  private void deleteObject(String bucketName, String objectName, Date deletionDate, Date curDate) {
    if (deletionDate.before(curDate)) {
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
