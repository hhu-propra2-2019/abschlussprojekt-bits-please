package mops.zulassung2.services;

import mops.zulassung2.model.dataobjects.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

  static EmailService emailServiceMock = mock(EmailService.class);
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
    try {
      emailServiceMock.sendMessage(to, subject, text, attach, filename);
    } catch (MessagingException e) {
      e.printStackTrace();
    }

    //Assert
    try {
      verify(emailServiceMock).sendMessage(to, subject, text, attach, filename);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }

  @Test
  void sendMail() {
    //Arrange
    Student student = new Student("272490", "snami100@uni-duesseldorf.de", "Amin", "Snur");
    String subject = "Informatik";
    File file = mock(File.class);

    //Act
    try {
      emailServiceMock.sendMail(student, subject, file);
    } catch (MessagingException e) {
      e.printStackTrace();
    }

    //Assert
    try {
      verify(emailServiceMock).sendMail(student, subject, file);
    } catch (MessagingException e) {
      e.printStackTrace();
    }
  }


}