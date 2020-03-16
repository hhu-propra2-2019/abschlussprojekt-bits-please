package mops.zulassung2.model;

import mops.zulassung2.model.dataobjects.Student;

public interface NameCreator {
  String createBucketName(Student student);
}
