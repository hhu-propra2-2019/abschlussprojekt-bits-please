package mops.zulassung2.services;

import mops.zulassung2.model.dataobjects.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  @InjectMocks
  EmailService emailService;
  @Mock
  JavaMailSender javaMailSender;
  @Mock
  MimeMessage mimeMessage;
  String subject;

  @BeforeEach
  void setup() {
    when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
    subject = "Informatik";
  }

  @Test
  void sendMailWithAttachmentAndValidAdress() {
    //Arrange
    Student student = new Student("2444111", "hapet100@hhu.de", "Peter", "Hans");
    File file = mock(File.class);
    when(file.getName()).thenReturn("Testfile");

    //Act
    try {
      emailService.sendMail(student, subject, file);
    } catch (MessagingException e) {
      e.printStackTrace();
    }

    //Assert
    verify(javaMailSender).send(mimeMessage);
  }

  @Test
  void sendMailWithAttachmentAndInvalidAdresses() {
    //Arrange
    Student studentWithoutDot =
        new Student("2444111", "hapet100@hhude", "Peter", "Hans");
    Student studentWithoutAt =
        new Student("123456", "meibe104uni-duesseldorf.de", "Beck", "Meinhard");
    File file = mock(File.class);
    when(file.getName()).thenReturn("Testfile");

    //Act & Assert
    assertThatThrownBy(() -> {
      emailService.sendMail(studentWithoutDot, subject, file);
    }).isInstanceOf(MessagingException.class);
    assertThatThrownBy(() -> {
      emailService.sendMail(studentWithoutAt, subject, file);
    }).isInstanceOf(MessagingException.class);
  }

  @Test
  void sendSimpleMailWithValidAdress() {
    //Arrange
    Student student = new Student("2444111", "hapet100@hhu.de", "Peter", "Hans");

    //Act
    try {
      emailService.sendWarningMail(student, subject, "2030-01-01T00:00");
    } catch (MessagingException e) {
      e.printStackTrace();
    }

    //Assert
    verify(javaMailSender).send(mimeMessage);
  }

  @Test
  void sendSimpleMailWithInvalidAdresses() {
    //Arrange
    Student studentWithoutDot =
        new Student("2444111", "hapet100@hhude", "Peter", "Hans");
    Student studentWithoutAt =
        new Student("123456", "meibe104uni-duesseldorf.de", "Beck", "Meinhard");

    //Act & Assert
    assertThatThrownBy(() -> {
      emailService.sendWarningMail(studentWithoutDot, subject, "03.02.2030 um 20:15 Uhr");
    }).isInstanceOf(MessagingException.class);
    assertThatThrownBy(() -> {
      emailService.sendWarningMail(studentWithoutAt, subject, "03.04.2030 um 20:15 Uhr");
    }).isInstanceOf(MessagingException.class);
  }
}