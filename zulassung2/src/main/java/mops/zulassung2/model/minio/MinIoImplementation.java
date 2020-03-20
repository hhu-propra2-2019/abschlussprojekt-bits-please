package mops.zulassung2.model.minio;

import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MinIoImplementation {
  private MinIoRepositoryInterface minIo;

  public MinIoImplementation(MinIoRepositoryInterface minIo) {
    this.minIo = minIo;
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
}
