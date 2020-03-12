package mops.zulassung2.services;

import mops.zulassung2.model.Student;

public interface ReceiptData {
  String create(Student student, String currentSubject);
}
