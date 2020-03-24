package mops.zulassung2.model.minio;

import com.amazonaws.services.s3.model.IllegalBucketNameException;

import static com.amazonaws.services.s3.internal.BucketNameUtils.validateBucketName;

public class BucketNameValidator implements BucketNameValidatorInterface {

  @Override
  public boolean isValidBucketName(String bucketName) {
    try {
      validateBucketName(bucketName);
    } catch (IllegalBucketNameException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

}
