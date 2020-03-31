package mops.zulassung2.tasks;

import mops.zulassung2.model.minio.*;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DeleteStorageTask {

  private int storageDuration;
  private MinIoImplementationInterface minIoImplementation;
  private CustomDateInterface currentDate;

  /**
   * Initialize minIoHelper from given instance.
   *
   * @param impl          minIoImplementation instance
   * @param storeDuration duration in years
   * @param currentDate   current date
   */

  public DeleteStorageTask(MinIoImplementationInterface impl, int storeDuration, CustomDateInterface currentDate) {
    this.minIoImplementation = impl;
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

  public DeleteStorageTask(MinIoEndpoint config, int storeDuration, CustomDateInterface currentDate) {
    MinIoRepositoryInterface repo = new MinIoRepository(config.endpoint, config.accessKey, config.secretKey);
    NameCreatorInterface nameCreatorInterface = new NameCreator(new BucketNameValidator());
    this.minIoImplementation = new MinIoImplementation(repo, nameCreatorInterface);
    this.storageDuration = storeDuration;
    this.currentDate = currentDate;
  }

  /**
   * Task that periodically deletes too old files.
   */
  @Scheduled(fixedRateString = "${check_storage_rate}")
  public void checkStorageDuration() {

    List<BucketObject> allObjects = minIoImplementation.getAllObjects();
    for (BucketObject bucketObject : allObjects) {
      String bucketName = bucketObject.getBucketName();
      String objectName = bucketObject.getObjectName();
      Date creationDate = minIoImplementation.getCreateTime(bucketName, objectName);

      Date deletionDate = getDeletionDate(creationDate);

      deleteObject(bucketName, objectName, deletionDate, currentDate.getCurrentDate());
    }
  }

  private void deleteObject(String bucketName, String objectName, Date deletionDate, Date curDate) {
    if (deletionDate.before(curDate)) {
      minIoImplementation.removeObject(bucketName, objectName);

      deleteBucket(bucketName);
    }
  }

  private void deleteBucket(String bucketName) {
    if (minIoImplementation.isBucketEmpty(bucketName)) {
      minIoImplementation.removeBucket(bucketName);
    }
  }

  private Date getDeletionDate(Date creationDate) {
    Calendar c = Calendar.getInstance();
    c.setTime(creationDate);
    c.add(Calendar.YEAR, storageDuration);
    return c.getTime();
  }

}
