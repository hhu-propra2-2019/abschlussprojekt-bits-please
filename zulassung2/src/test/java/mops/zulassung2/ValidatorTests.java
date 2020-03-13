package mops.zulassung2;

import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.services.SignatureService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;


import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTests {
  static SignatureService signatureService;
  @BeforeAll
  static void setupKeys() throws NoSuchAlgorithmException {
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    KeyPair keyPair = keyPairGenerator.generateKeyPair();
    signatureService = new SignatureService(keyPair.getPrivate(), keyPair.getPublic());
  }

  @Test
  public void testValidator()  {
    File file = new File("test.txt");
    FileWriter writer = null;
    String data ="Jochen Jockelheim,5,22828131";
    Receipt receipt = signatureService.sign(data);
    try {
      writer = new FileWriter(file,UTF_8);
      writer.write(receipt.getSignature());
    } catch (IOException e) {
      e.printStackTrace();
    }finally{
      //close resources
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    assertThat(signatureService.verify(receipt)).isTrue();
  }
  @Test
  public void testManipulatedData()  {
    File file = new File("test.txt");
    FileWriter writer = null;
    String data ="Jochen Jockelheim,5,22828131";
    String manipulatedData = "Klopper Klöppelmeier,true";
    Receipt receipt = signatureService.sign(data);
    Receipt manipulatedReceipt = new Receipt(manipulatedData, receipt.getSignature());
    try {
      writer = new FileWriter(file,UTF_8);
      writer.write(receipt.getSignature());
    } catch (IOException e) {
      e.printStackTrace();
    }finally{
      //close resources
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    assertThat(signatureService.verify(manipulatedReceipt)).isFalse();
  }




}


