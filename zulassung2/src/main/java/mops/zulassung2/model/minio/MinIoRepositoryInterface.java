package mops.zulassung2.model.minio;

import io.minio.Result;
import io.minio.messages.Bucket;
import io.minio.messages.Item;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MinIoRepositoryInterface {
  boolean bucketExists(String bucketName);

  void makeBucket(String bucketName);

  void removeBucket(String bucketName);

  void putObject(String bucketName, String objectName, String fileName, Long size,
                 Map<String, String> headerMap, String contentType);

  void removeObject(String bucketName, String objectName);

  List<Bucket> listBuckets();

  Iterable<Result<Item>> listObjects(String bucketName);

  Date getCreateTime(String bucketName, String objectName);

  String getObjectName(Result<Item> object);
}
