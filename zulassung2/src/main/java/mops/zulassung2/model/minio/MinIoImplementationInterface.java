package mops.zulassung2.model.minio;

import mops.zulassung2.model.dataobjects.Student;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MinIoImplementationInterface {
  List<BucketObject> getAllObjects();

  void removeBucket(String bucketName);

  void makeBucket(String bucketName);

  boolean bucketExists(String bucketName);

  void putObject(String bucketName, String objectName, String fileName, Long size,
                 Map<String, String> headerMap, String contentType);

  void removeObject(String bucketName, String objectName);

  Date getCreateTime(String bucketName, String objectName);

  boolean isBucketEmpty(String bucketName);

  boolean isAuthorized(Student student, String subject);
}
