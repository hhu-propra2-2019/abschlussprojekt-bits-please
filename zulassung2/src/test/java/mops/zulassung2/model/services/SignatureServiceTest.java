package mops.zulassung2.model.services;

import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.services.SignatureService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

import static org.assertj.core.api.Assertions.assertThat;

class SignatureServiceTest {

  static SignatureService signatureService;

  @BeforeAll
  static void setupKeys() throws NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    signatureService = new SignatureService(keyPair.getPrivate(), keyPair.getPublic());
  }

  @Test
  void verifyCorrectSignatures() {
    String data = "Herr Datensatz,1,24628131";
    String data1 = "Frau Datensätzer,2,26374565";

    Receipt receipt = signatureService.sign(data);
    Receipt receipt1 = signatureService.sign(data1);

    assertThat(signatureService.verify(receipt)).isTrue();
    assertThat(signatureService.verify(receipt1)).isTrue();
  }

  @Test
  void verifyManipulatedData() {
    String data = "Klopper Klöppelmeier,false";
    String manipulatedData = "Klopper Klöppelmeier,true";

    Receipt receipt = signatureService.sign(data);
    Receipt manipulatedReceipt = new Receipt(manipulatedData, receipt.getSignature());

    assertThat(signatureService.verify(manipulatedReceipt)).isFalse();
  }

  @Test
  void verifyManipulatedSignature() {
    String data = "Jochen Jockelheim,5,22828131";

    Receipt receipt = signatureService.sign(data);
    String manipulatedSignature = receipt.getSignature().replace('a', 'c');
    Receipt manipulatedReceipt = new Receipt(receipt.getData(), manipulatedSignature);

    assertThat(signatureService.verify(manipulatedReceipt)).isFalse();
  }

  @Test
  void verifyWrongSignature() {
    String data = "Jochen Jockelheim,5,22828131";
    String data1 = "Gundel Gummershausen,3,26124565";

    Receipt receipt = signatureService.sign(data);
    Receipt receipt1 = signatureService.sign(data1);
    Receipt wrongReceipt = new Receipt(receipt1.getData(), receipt.getSignature());

    assertThat(signatureService.verify(wrongReceipt)).isFalse();
  }
}