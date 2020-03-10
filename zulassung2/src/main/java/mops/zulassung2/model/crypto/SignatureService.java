package mops.zulassung2.model.crypto;

import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import java.security.PrivateKey;
import java.security.PublicKey;

@Service
public class SignatureService {

  PrivateKey privateKey;
  PublicKey publicKey;

  public SignatureService(PrivateKey privateKey, PublicKey publicKey) {
    this.privateKey = privateKey;
    this.publicKey = publicKey;
  }

  /**
   * Creates a signature for a given String.
   *
   * @param toSign data to be signed
   * @return Receipt which contains data and signature
   */
  public Receipt sign(String toSign) {

    HasherInterface hasherInterface = new Hasher();
    String hashed = hasherInterface.getHash(toSign);

    EncryptionInterface encryptionInterface = new Encryption();
    byte[] signed = encryptionInterface.encrypt(hashed, privateKey);

    return new Receipt(toSign, Base64.encodeBase64String(signed));
  }

  /**
   * Verifies the signature and validity for a given Receipt.
   *
   * @param toVerify Receipt to be verified
   * @return true, if valid; false, if invalid
   */
  public boolean verify(Receipt toVerify) {

    HasherInterface hasherInterface = new Hasher();
    EncryptionInterface encryptionInterface = new Encryption();

    String data = toVerify.getData();
    String signature = toVerify.getSignature();

    return encryptionInterface.decrypt(hasherInterface.getHash(data), signature, publicKey);
  }

}
