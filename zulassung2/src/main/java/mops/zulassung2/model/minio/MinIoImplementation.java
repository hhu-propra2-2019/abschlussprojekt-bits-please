package mops.zulassung2.model.minio;

import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import mops.zulassung2.model.dataobjects.Student;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MinIoImplementation {
  private MinIoRepositoryInterface minIo;
  private NameCreator nameCreator;

  public MinIoImplementation(MinIoRepositoryInterface minIo, NameCreator nameCreator) {
    this.minIo = minIo;
    this.nameCreator = nameCreator;
  }

  /**
   * Creates a list of all bucket/object combinations.
   *
   * @return list of bucket objects
   */
  public List<BucketObject> getAllObjects() {
    List<BucketObject> buckets = new ArrayList<>();
    List<Bucket> bucketList = minIo.listBuckets();
    for (Bucket bucket : bucketList) {
      addBucketObjects(buckets, bucket);
    }

    return buckets;
  }

  private void addBucketObjects(List<BucketObject> buckets, Bucket bucket) {

    Iterable<Result<Item>> bucketObjects;
    bucketObjects = minIo.listObjects(bucket.name());

    for (Result<Item> object : bucketObjects) {
      BucketObject bucketObject = new BucketObject(bucket.name(), minIo.getObjectName(object));
      buckets.add(bucketObject);
    }
  }

  public void removeBucket(String bucketName) {
    minIo.removeBucket(bucketName);
  }

  public void makeBucket(String bucketName) {
    minIo.makeBucket(bucketName);
  }

  public boolean bucketExists(String bucketName) {
    return minIo.bucketExists(bucketName);
  }

  public void putObject(String bucketName, String objectName, String fileName, Long size,
                        Map<String, String> headerMap, String contentType) {
    minIo.putObject(bucketName, objectName, fileName, size, headerMap, contentType);
  }

  public void removeObject(String bucketName, String objectName) {
    minIo.removeObject(bucketName, objectName);
  }

  public Date getCreateTime(String bucketName, String objectName) {
    return minIo.getCreateTime(bucketName, objectName);
  }

  /**
   * Checks whether a bucket is empty or not.
   *
   * @param bucketName name of the bucket
   * @return boolean
   */
  public boolean isBucketEmpty(String bucketName) {
    List<Bucket> bucketList;
    bucketList = minIo.listBuckets();
    for (Bucket bucket : bucketList) {
      if (bucket.name().equals(bucketName)) {
        if (isObjectListEmpty(bucket)) {
          return false;
        }
      }
    }

    return true;
  }

  private boolean isObjectListEmpty(Bucket bucket) {
    Iterable<Result<Item>> results = minIo.listObjects(bucket.name());
    for (Result<Item> item : results) {
      return true;
    }

    return false;
  }

  /**
   * Checks whether a student is authorized to participate.
   *
   * @param student student that needs to be checked
   * @param subject subject that student needs to be authorized for
   * @return
   */
  public boolean isAuthorized(Student student, String subject) {
    String bucketName = nameCreator.createBucketName(student);
    if (minIo.bucketExists(bucketName)) {
      Iterable<Result<Item>> objects = minIo.listObjects(bucketName);
      for (Result<Item> object : objects) {
        String objectName = minIo.getObjectName(object);
        if (objectName.contains(subject)) {
          return true;
        }
      }
    }

    return false;
  }
}
