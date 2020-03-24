package mops.zulassung2.model.minio;

import mops.zulassung2.model.dataobjects.Student;

public class NameCreator implements NameCreatorInterface {

  private BucketNameValidatorInterface bucketNameValidator;

  public NameCreator(BucketNameValidatorInterface bucketNameValidator) {
    this.bucketNameValidator = bucketNameValidator;
  }

  @Override
  public String createBucketName(Student student) {
    String bucketName = student.getMatriculationNumber() + "-" + student.getName();
    if (bucketNameValidator.isValidBucketName(bucketName)) {
      return bucketName;
    } else {
      return student.getMatriculationNumber();
    }
  }
}
