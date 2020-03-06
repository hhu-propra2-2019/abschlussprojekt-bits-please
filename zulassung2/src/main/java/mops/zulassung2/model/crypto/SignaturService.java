package mops.zulassung2.model.crypto;

import java.nio.charset.StandardCharsets;

public class SignaturService {

  /**
   *  Creates a signature for a given String.
   *
   * @param toSign data to be signed
   * @return Receipt which contains data and signature
   */
  public Receipt sign(String toSign) {

    HasherInterface hasherInterface = new Hasher();
    String hashed = hasherInterface.getHash(toSign);

    EncryptionInterface encryptionInterface = new Encryption();
    byte[] signed = encryptionInterface.encrypt(hashed);

    return new Receipt(toSign, new String(signed, StandardCharsets.UTF_8));
  }

  /**
   *  Verifies the signature and validity for a given Receipt.
   *
   * @param toVerify Receipt to be verified
   * @return true, if valid; false, if invalid
   */
  public boolean verify(Receipt toVerify) {

    HasherInterface hasherInterface = new Hasher();
    EncryptionInterface encryptionInterface = new Encryption();

    String data = toVerify.getData();
    String signature = toVerify.getSignature();

    return encryptionInterface.decrypt(hasherInterface.getHash(data), signature);
  }

}
