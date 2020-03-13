package mops.zulassung2.services;
import mops.zulassung2.model.dataObjects.Student;

public interface ReceiptData {
  String create();

  String getMatriculationNumber();

  String getEmail();

  String getName();

  String getForeName();

  String getModule();

  String getSignature();

  void setValid(boolean b);
}
