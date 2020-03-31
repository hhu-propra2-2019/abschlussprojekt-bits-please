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
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MinIoRepository implements MinIoRepositoryInterface {
  private MinioClient minioClient;

  public MinIoRepository(String endpoint, String accessKey, String secretKey) {
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
      return minioClient.bucketExists(bucketName.toLowerCase());
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
      minioClient.putObject(bucketName.toLowerCase(), objectName, fileName, size, headerMap,
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
      minioClient.removeObject(bucketName.toLowerCase(), objectName);
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | IOException
            | InvalidKeyException | NoResponseException | XmlPullParserException
            | ErrorResponseException | InternalException | InvalidArgumentException
            | InsufficientDataException | InvalidResponseException e) {
      e.printStackTrace();
    }
  }

  /**
   * Lists all buckets.
   *
   * @return list of buckets
   */
  public List<Bucket> listBuckets() {
    try {
      return minioClient.listBuckets();
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
            | IOException | InvalidKeyException | NoResponseException
            | XmlPullParserException | ErrorResponseException | InternalException
            | InvalidResponseException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Lists all objects.
   *
   * @param bucketName bucket name whose objects need to be queried
   * @return list of objects
   */
  public Iterable<Result<Item>> listObjects(String bucketName) {
    try {
      return minioClient.listObjects(bucketName.toLowerCase());
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    }

    return null;
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
      ObjectStat objectStat = minioClient.statObject(bucketName.toLowerCase(), objectName);
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
   * Get the name of an object.
   *
   * @param object object whose name needs to be fetched
   * @return name of object
   */
  public String getObjectName(Result<Item> object) {
    try {
      return object.get().objectName();
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException
            | IOException | InvalidKeyException | NoResponseException
            | XmlPullParserException | ErrorResponseException | InternalException e) {
      e.printStackTrace();
    }

    return null;
  }
}