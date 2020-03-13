package mops.zulassung2.services;

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
