package mops.zulassung2.model.minio;

import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.Result;
import io.minio.errors.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MinIoHelper {
  private MinioClient minioClient;

  public MinIoHelper(String endpoint, String accessKey, String secretKey) {
    initializeClient(endpoint, accessKey, secretKey);
  }

  private void initializeClient(String endpoint, String accessKey, String secretKey) {
    try {
      minioClient = new MinioClient(endpoint, accessKey, secretKey);
    } catch (InvalidEndpointException | InvalidPortException e) {
      e.printStackTrace();
    }
  }

  /**
   * Checks whether given bucketName exists.
   *
   * @param bucketName name of the bucket
   * @return boolean
   */
  public boolean bucketExists(String bucketName) {
    try {
      return minioClient.bucketExists(bucketName);
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
            | IOException | InvalidKeyException | NoResponseException
            | XmlPullParserException | ErrorResponseException | InternalException
            | InvalidResponseException e) {
      e.printStackTrace();
    }

    return false;
  }

  /**
   * Creates a bucket with the given name.
   *
   * @param bucketName name of the bucket
   */
  public void makeBucket(String bucketName) {
    try {
      minioClient.makeBucket(bucketName.toLowerCase());
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
            | IOException | InvalidKeyException | NoResponseException
            | XmlPullParserException | ErrorResponseException | InternalException
            | InvalidResponseException | RegionConflictException e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes a bucket with the given name.
   *
   * @param bucketName name of the bucket
   */
  public void removeBucket(String bucketName) {
    try {
      minioClient.removeBucket(bucketName.toLowerCase());
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
            | IOException | InvalidKeyException | NoResponseException
            | XmlPullParserException | ErrorResponseException | InternalException
            | InvalidResponseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Puts an object into a bucket.
   *
   * @param bucketName  name of the bucket
   * @param objectName  name of the file
   * @param fileName    path + name of the file
   * @param size        size of the file
   * @param headerMap   additional metadata
   * @param contentType file content type
   */
  public void putObject(String bucketName, String objectName, String fileName, Long size,
                        Map<String, String> headerMap, String contentType) {
    try {
      minioClient.putObject(bucketName, objectName, fileName, size, headerMap,
              null, contentType); //no encryption necessary
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | IOException
            | InvalidKeyException | NoResponseException | XmlPullParserException
            | ErrorResponseException | InternalException | InvalidArgumentException
            | InsufficientDataException | InvalidResponseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Removes an object from a given bucket.
   *
   * @param bucketName name of the bucket
   * @param objectName name of the file
   */
  public void removeObject(String bucketName, String objectName) {
    try {
      minioClient.removeObject(bucketName, objectName);
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | IOException
            | InvalidKeyException | NoResponseException | XmlPullParserException
            | ErrorResponseException | InternalException | InvalidArgumentException
            | InsufficientDataException | InvalidResponseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Creates a list of all bucket/object combinations.
   *
   * @return list of bucket objects
   */
  public List<BucketObject> getAllObjects() {
    List<BucketObject> buckets = new ArrayList<>();
    try {
      List<Bucket> bucketList = minioClient.listBuckets();
      for (Bucket bucket : bucketList) {
        addBucketObjects(buckets, bucket);
      }
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
            | IOException | InvalidKeyException | NoResponseException
            | XmlPullParserException | ErrorResponseException | InternalException
            | InvalidResponseException e) {
      e.printStackTrace();
    }

    return buckets;
  }

  private void addBucketObjects(List<BucketObject> buckets, Bucket bucket) {

    Iterable<Result<Item>> bucketObjects;
    try {
      bucketObjects = minioClient.listObjects(bucket.name());

      for (Result<Item> object : bucketObjects) {
        BucketObject bucketObject = new BucketObject(bucket.name(), object.get().objectName());
        buckets.add(bucketObject);
      }
    } catch (XmlPullParserException | InvalidBucketNameException | NoSuchAlgorithmException
            | InsufficientDataException | IOException | InvalidKeyException
            | NoResponseException | ErrorResponseException | InternalException e) {
      e.printStackTrace();
    }
  }

  /**
   * Get time of creation of object.
   *
   * @param bucketName name of the bucket
   * @param objectName name of the object
   * @return date
   */
  public Date getCreateTime(String bucketName, String objectName) {
    Date date = null;
    try {
      ObjectStat objectStat = minioClient.statObject(bucketName, objectName);
      date = objectStat.createdTime();
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
            | IOException | InvalidKeyException | NoResponseException
            | XmlPullParserException | ErrorResponseException | InternalException
            | InvalidResponseException | InvalidArgumentException e) {
      e.printStackTrace();
    }

    return date;
  }

  /**
   * Checks whether a bucket is empty or not.
   *
   * @param bucketName name of the bucket
   * @return boolean
   */
  public boolean isBucketEmpty(String bucketName) {
    List<Bucket> bucketList;
    try {
      bucketList = minioClient.listBuckets();
      for (Bucket bucket : bucketList) {
        if (bucket.name().equals(bucketName)) {
          if (isObjectListEmpty(bucket)) {
            return false;
          }
        }
      }
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
            | IOException | InvalidKeyException | NoResponseException
            | XmlPullParserException | ErrorResponseException | InternalException
            | InvalidResponseException e) {
      e.printStackTrace();
    }

    return true;
  }

  private boolean isObjectListEmpty(Bucket bucket) throws XmlPullParserException {
    Iterable<Result<Item>> results = minioClient.listObjects(bucket.name());
    for (Result<Item> item : results) {
      return true;
    }
    return false;
  }
}
