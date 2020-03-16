package mops.zulassung2.model;

import io.minio.MinioClient;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class MinIOHelper {
  private MinioClient minioClient;

  public MinIOHelper() {
    initializeClient();
  }

  private void initializeClient() {
    try {
      minioClient = new MinioClient("https://play.min.io", "Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG");
    } catch (InvalidEndpointException | InvalidPortException e) {
      e.printStackTrace();
    }
  }

  public boolean bucketExists(String bucketName) {
    try {
      return minioClient.bucketExists(bucketName);
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException e) {
      e.printStackTrace();
    } catch (InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException e) {
      e.printStackTrace();
    } catch (InternalException | InvalidResponseException e) {
      e.printStackTrace();
    }

    return false;
  }

  public void makeBucket(String bucketName) {
    try {
      minioClient.makeBucket(bucketName);
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException e) {
      e.printStackTrace();
    } catch (InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException e) {
      e.printStackTrace();
    } catch (InternalException | InvalidResponseException | RegionConflictException e) {
      e.printStackTrace();
    }
  }

  public void removeBucket(String bucketName) {
    try {
      minioClient.removeBucket(bucketName);
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | InsufficientDataException | IOException e) {
      e.printStackTrace();
    } catch (InvalidKeyException | NoResponseException | XmlPullParserException | ErrorResponseException e) {
      e.printStackTrace();
    } catch (InternalException | InvalidResponseException e) {
      e.printStackTrace();
    }
  }

  public void putObject(String bucketName, String objectName, String path) {
    try {
      minioClient.putObject(bucketName, objectName, path);
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoResponseException | XmlPullParserException | ErrorResponseException | InternalException e) {
      e.printStackTrace();
    } catch (InvalidArgumentException | InsufficientDataException | InvalidResponseException e) {
      e.printStackTrace();
    }
  }

  public void removeObject(String bucketName, String objectName) {
    try {
      minioClient.removeObject(bucketName, objectName);
    } catch (InvalidBucketNameException | NoSuchAlgorithmException | IOException | InvalidKeyException e) {
      e.printStackTrace();
    } catch (NoResponseException | XmlPullParserException | ErrorResponseException | InternalException e) {
      e.printStackTrace();
    } catch (InvalidArgumentException | InsufficientDataException | InvalidResponseException e) {
      e.printStackTrace();
    }
  }
}
