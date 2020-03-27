package mops.zulassung2.model.dataobjects;

public interface ReceiptDataInterface {
  String create();

  String getMatriculationNumber();

  String getEmail();

  String getName();

  String getForeName();

  String getModule();

  String getSemester();

  String getSignature();

  boolean isValid();

  void setValid(boolean b);
}
