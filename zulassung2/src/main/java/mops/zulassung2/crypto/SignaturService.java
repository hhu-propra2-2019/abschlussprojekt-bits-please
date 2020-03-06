package mops.zulassung2.crypto;

public class SignaturService {

  public Receipt sign(String toSign) {

    HasherInterface hasherInterface = new Hasher();
    String hashed = hasherInterface.getHash(toSign);

    EncryptionInterface encryptionInterface = new Encryption();
    byte[] signed = encryptionInterface.encrypt(hashed);

    return new Receipt(toSign, new String(signed));
  }

  public boolean verify(Receipt toVerify){

    HasherInterface hasherInterface = new Hasher();
    EncryptionInterface encryptionInterface = new Encryption();

    return encryptionInterface.decrypt(hasherInterface.getHash(toVerify.getData()), toVerify.getSignature());
  }

}
