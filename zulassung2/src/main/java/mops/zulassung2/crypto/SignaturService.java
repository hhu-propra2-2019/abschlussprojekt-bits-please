package mops.zulassung2.crypto;

public class SignaturService {

  public Token sign(String toSign) {

    HasherInterface hasherInterface = new Hasher();
    String hashed = hasherInterface.getHash(toSign);

    EncryptionInterface encryptionInterface = new Encryption();
    byte[] signed = encryptionInterface.encrypt(hashed);

    return new Token(toSign, new String(signed));
  }

  public boolean verify(Token toVerify){

    HasherInterface hasherInterface = new Hasher();
    EncryptionInterface encryptionInterface = new Encryption();

    return encryptionInterface.decrypt(hasherInterface.getHash(toVerify.getData()), toVerify.getSignature());
  }

}
