package mops.zulassung2.services;

import mops.zulassung2.model.Student;

public class CustomReceiptData implements ReceiptData {

  @Override
  public String create(Student student) {
    //TODO: Add module.
    String data = "matriculationnumber:" + student.getMatriculationNumber()
        + " email:" + student.getEmail()
        + " name:" + student.getName()
        + " forname:" + student.getForeName()
        + " module:"; // + module;
    return data;
  }
}
