package mops.zulassung2.services;

import mops.Zulassung2Application;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.crypto.Receipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(Zulassung2Application.class);
    private final JavaMailSender emailSender;
    private final SignatureService signatureService;
    @Value("${email_body_text}")
    private String emailBodyText;

    public EmailService(JavaMailSender emailSender, SignatureService signatureService) {
        this.emailSender = emailSender;
        this.signatureService = signatureService;
    }

    /**
     * Sends an email with attachment (File).
     *
     * @param to       receiver of the mail
     * @param subject  subject of the mail
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

    /**
     * Diese Methode wird vom OrganisatorController (Methode: sendMail) aufgerufen.
     * *
     * Diese Methode erstellt benutzerdefinierte Files und ruft sendMessage auf.
     */

    public File createFile(ReceiptData receiptData, String data, Receipt receipt) {
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


    public void sendMail(Student student, String currentSubject) {
        ReceiptData receiptData = new CustomReceiptData(student, currentSubject);
        String data = receiptData.create();
        Receipt receipt = signatureService.sign(data);
        File file = createFile(receiptData, data, receipt);

        String emailText = createCustomizedEmailBodyText(student, currentSubject);
        String mail = receiptData.getEmail();
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
}