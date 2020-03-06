package mops.zulassung2.model.crypto;

public interface EncryptionInterface {

  public byte[] encrypt(String toEncrypt);
  public boolean decrypt(String toDecrypt, String toVerify);
}
