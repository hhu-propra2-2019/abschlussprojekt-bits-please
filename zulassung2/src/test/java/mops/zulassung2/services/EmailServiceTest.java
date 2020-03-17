package mops.zulassung2.services;

import mops.zulassung2.model.dataobjects.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.File;

import static org.mockito.Mockito.mock;

class EmailServiceTest {

  static EmailService emailServices = mock(EmailService.class);
  static EmailService emailService;
  static SignatureService signatureService = mock(SignatureService.class);

  @BeforeAll
  static void setUp() {
    JavaMailSender javaMailSender = mock(JavaMailSender.class);
    emailService = new EmailService(javaMailSender, signatureService);
  }

  @Test
  void sendMail() {
    Student student = new Student("272490", "snami100@uni-duesseldorf.de", "Amin", "Snur");
    String subject = "Informatik";
    File file = mock(File.class);

    emailServices.sendMail(student, subject, file);

    Mockito.verify(emailServices).sendMail(student, subject, file);
  }
}