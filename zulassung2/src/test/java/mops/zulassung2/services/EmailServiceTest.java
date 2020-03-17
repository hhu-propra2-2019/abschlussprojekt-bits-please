package mops.zulassung2.services;

import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.model.dataobjects.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class EmailServiceTest {

  static EmailService emailServices = mock(EmailService.class);
  static EmailService emailService;
  static SignatureService signatureService = mock(SignatureService.class);
  private Student douglas = new Student("2729350", "dobla101@hhu.de", "blank", "douglas");

  @BeforeAll
  static void setUp() {
    JavaMailSender javaMailSender = mock(JavaMailSender.class);
    emailService = new EmailService(javaMailSender, signatureService);
  }

  @Test
  void sendMessage() {
    //Arrange
    String to = "snami100@uni-duesseldorf.de";
    String subject = "Informatik";
    String text = "Test, sendMessage";
    File attach = mock(File.class);
    String filename = "Dateiname";

    //Act
    emailServices.sendMessage(to, subject, text, attach, filename);

    //Assert
    verify(emailServices).sendMessage(to, subject, text, attach, filename);
  }

  @Test
  void sendMail() {
    //Arrange
    Student student = new Student("272490", "snami100@uni-duesseldorf.de", "Amin", "Snur");
    String subject = "Informatik";
    File file = mock(File.class);

    //Act
    emailServices.sendMail(student, subject, file);

    //Assert
    verify(emailServices).sendMail(student, subject, file);
  }

  @Test
  void createFile() throws IOException {
    // Arrange
    ReceiptData receiptData = mock(ReceiptData.class);
    String data = "matriculationnumber:" + douglas.getMatriculationNumber()
        + " email:" + douglas.getEmail()
        + " name:" + douglas.getName()
        + " forename:" + douglas.getForeName()
        + " module:" + "informatik"
        + " semester:" + "null"
        + "\n"
        + "signatur";
    Receipt receipt = mock(Receipt.class);
    when(receiptData.getModule()).thenReturn("informatik");
    when(receiptData.getName()).thenReturn("blank");
    when(receipt.getSignature()).thenReturn("signatur");
    Receipt receipt1 = new Receipt(data, "signatur");
    when(signatureService.sign(anyString())).thenReturn(receipt1);

    // Act
    File file = emailService.createFile(douglas, "informatik");
    String content = Files.readString(file.toPath());

    // Assert
    assertEquals(data, content);
  }
}