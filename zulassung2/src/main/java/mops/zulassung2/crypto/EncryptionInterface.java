package mops.zulassung2.crypto;

public interface EncryptionInterface {

  public byte[] encrypt(String toEncrypt);
  public boolean decrypt(String toDecrypt, String toVerify);
}
