package mops.zulassung2.services;

public interface ReceiptData {
  String create();

  String getEmail();

  String getName();

  String getModule();

  String getSignature();

  void setValid(boolean b);
}
