package mops.zulassung2.services;

import mops.zulassung2.model.dataObjects.Student;

public class CustomReceiptData implements ReceiptData {

  @Override
  public String create(Student student, String currentSubject) {
    String data = "matriculationnumber:" + student.getMatriculationNumber()
        + " email:" + student.getEmail()
        + " name:" + student.getName()
        + " forname:" + student.getForeName()
        + " module:" + currentSubject;
    return data;
  }
}
