package mops.zulassung2.model.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface EncryptionInterface {

  byte[] encrypt(String toEncrypt, PrivateKey privateKey);

  boolean decrypt(String toDecrypt, String toVerify, PublicKey publicKey);
}
