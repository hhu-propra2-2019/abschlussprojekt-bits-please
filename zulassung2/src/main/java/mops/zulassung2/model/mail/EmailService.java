package mops.zulassung2.model.mail;

import mops.Zulassung2Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService implements EmailServiceInterface {

  private static final Logger logger = LoggerFactory.getLogger(Zulassung2Application.class);
  @Autowired
  public JavaMailSender emailSender;

  /**
   * Sends an email without any attachment.
   *
   * @param to      receiver of the mail
   * @param subject subject of the mail
   * @param text    content of the mail
   */
  public void sendSimpleMessage(String to, String subject, String text) {

    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject(subject);
    message.setText(text);
    emailSender.send(message);
  }

  /**
   * Sends an email with attachment (File).
   *
   * @param to       receiver of the mail
   * @param subject  subject of the mail
   * @param text     content of the mail
   * @param attach   file to be attached
   * @param filename name of the attached file
   */
  public void sendMessage(String to, String subject, String text, File attach, String filename) {

    try {
      MimeMessage message = emailSender.createMimeMessage();

      MimeMessageHelper helper = new MimeMessageHelper(message, true);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(text);
      helper.addAttachment(filename, attach);

      emailSender.send(message);

    } catch (MessagingException e) {
      logger.error(e.getMessage());
    }
  }
}