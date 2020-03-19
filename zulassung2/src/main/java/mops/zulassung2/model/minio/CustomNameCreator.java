package mops.zulassung2.model.minio;

import mops.zulassung2.model.dataobjects.Student;

public class CustomNameCreator implements NameCreator {
  @Override
  public String createBucketName(Student student) {
    return student.getMatriculationNumber() + "-" + student.getName();
  }
}
