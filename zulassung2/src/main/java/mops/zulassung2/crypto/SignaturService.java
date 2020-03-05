package mops.zulassung2.crypto;

public class SignaturService {

  public String sign(String toSign) {

    HasherInterface hasherInterface = new Hasher();
    String hashed = hasherInterface.getHash(toSign);
    EncryptionInterface encryptionInterface = new Encryption();
    byte[] signed = encryptionInterface.encrypt(hashed);

    String readytodeliver = new String(signed);
    return toSign + readytodeliver;
  }
}
