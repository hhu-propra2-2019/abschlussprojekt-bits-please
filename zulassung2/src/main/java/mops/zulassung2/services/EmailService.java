package mops.zulassung2.services;

import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.model.dataobjects.Student;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

  private final JavaMailSender emailSender;
  private final SignatureService signatureService;
  @Value("${email_body_text}")
  private String emailBodyText;

  @Value("${warning_email_body_text}")
  private String warningEmailBodyText;

  public EmailService(JavaMailSender emailSender, SignatureService signatureService) {
    this.emailSender = emailSender;
    this.signatureService = signatureService;
  }

  private static boolean isValidEmailAddress(String email) {
    EmailValidator validator = EmailValidator.getInstance();
    return validator.isValid(email);
  }


  /**
   * Diese Methode wird vom OrganisatorController (Methode: sendMail) aufgerufen.
   * *
   * Diese Methode erstellt benutzerdefinierte Files und ruft sendMessage auf.
   */

  public File createFile(Student student, String currentSubject, String currentSemester) {
    ReceiptData receiptData = new CustomReceiptData(student, currentSubject, currentSemester);
    String data = receiptData.create();
    Receipt receipt = signatureService.sign(data);
    File file = new File(System.getProperty("user.dir")
        + "token_" + receiptData.getModule()
        + "_" + receiptData.getName() + ".txt");
    FileWriter writer;

    try {
      writer = new FileWriter(file, StandardCharsets.UTF_8);
      writer.write(data + "\n");
      writer.write(receipt.getSignature());
      writer.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }

  /**
   * Sends an email with attachment (File).
   *
   * @param to       receiver of the mail
   * @param subject  subject of the mail
   * @param attach   file to be attached
   * @param filename name of the attached file
   */
  public void sendMessage(String to, String subject, String text, File attach, String filename)
      throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    if (!isValidEmailAddress(to)) {
      throw new MessagingException();
    }
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(text);
    helper.addAttachment(filename, attach);
    emailSender.send(message);
  }

  /**
   * Sends an E-mail with the information.
   *
   * @param student        Gives an Student into the method.
   * @param currentSubject Gives the Subject of the Student into the method.
   */
  public void sendMail(Student student, String currentSubject, File file)
      throws MessagingException {
    String emailText = createCustomizedEmailBodyText(student, currentSubject);
    String mail = student.getEmail();
    String subject = "Ihr Zulassungsnachweis zum Fach: ";
    sendMessage(mail, subject + currentSubject, emailText, file, file.getName());
    file.deleteOnExit();
  }


  private String createCustomizedEmailBodyText(Student student, String currentSubject) {
    String customizedEmailBodyText = emailBodyText;
    customizedEmailBodyText = customizedEmailBodyText.replace(":name", student.getName());
    customizedEmailBodyText = customizedEmailBodyText.replace(":modul", currentSubject);
    customizedEmailBodyText = customizedEmailBodyText.replace(":break", "\n");
    return customizedEmailBodyText;
  }


  /**
   * Sends an email without attachment.
   *
   * @param to      receiver of the mail
   * @param subject subject of the mail
   * @param text    Is setting up the Text for the mail
   */
  public void sendSimpleMessage(String to, String subject, String text)
      throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    if (!isValidEmailAddress(to)) {
      throw new MessagingException();
    }
    helper.setTo(to);
    helper.setSubject(subject);
    helper.setText(text);
    emailSender.send(message);
  }

  /**
   * Sends an E-Mail with the Information.
   *
   * @param student        Is setting the Student´s name.
   * @param currentSubject Is setting the Student´s subject.
   */
  public void sendWarningMail(Student student, String currentSubject)
      throws MessagingException {
    String emailText = createWarningEmailBodyText(student, currentSubject);
    String mail = student.getEmail();
    sendSimpleMessage(mail, currentSubject, emailText);
  }

  private String createWarningEmailBodyText(Student student, String currentSubject) {
    String customizedWarningEmailBodyText = warningEmailBodyText;
    customizedWarningEmailBodyText = customizedWarningEmailBodyText.replace(":name", student.getName());
    customizedWarningEmailBodyText = customizedWarningEmailBodyText.replace(":modul", currentSubject);
    customizedWarningEmailBodyText = customizedWarningEmailBodyText.replace(":break", "\n");
    return customizedWarningEmailBodyText;
  }


}