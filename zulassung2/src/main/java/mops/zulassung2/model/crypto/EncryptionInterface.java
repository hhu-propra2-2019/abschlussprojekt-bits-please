package mops.zulassung2.model.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface EncryptionInterface {

  public byte[] encrypt(String toEncrypt, PrivateKey privateKey);

  public boolean decrypt(String toDecrypt, String toVerify, PublicKey publicKey);
}
