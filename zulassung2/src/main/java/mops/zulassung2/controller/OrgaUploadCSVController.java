package mops.zulassung2.controller;

import mops.Zulassung2Application;
import mops.zulassung2.model.CustomNameCreator;
import mops.zulassung2.model.MinIoHelper;
import mops.zulassung2.model.NameCreator;
import mops.zulassung2.model.dataobjects.AccountCreator;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.OrganisatorService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SessionScope
@RequestMapping("/zulassung2/orga")
@Controller
public class OrgaUploadCSVController {

  private static final Logger logger = LoggerFactory.getLogger(Zulassung2Application.class);
  private final OrganisatorService organisatorService;
  private final EmailService emailService;
  public List<Student> students = new ArrayList<>();
  public String currentSubject;
  public String currentSemester;
  private NameCreator nameCreator;
  private MinIoHelper minIoHelper;
  private AccountCreator accountCreator;
  private String dangerMessage;
  private String warningMessage;
  private String successMessage;

  /**
   * Constructs OrganisatorController by injecting Beans of
   * OrganisatorService, SignatureService and Emailservice.
   *
   * @param organisatorService Service for parsing files
   * @param emailService       Service for sending emails
   */
  public OrgaUploadCSVController(OrganisatorService organisatorService,
                                 EmailService emailService) {
    accountCreator = new AccountCreator();
    nameCreator = new CustomNameCreator();
    minIoHelper = new MinIoHelper();
    this.organisatorService = organisatorService;
    this.emailService = emailService;
  }

  /**
   * This method is called for a GET request to /orga.
   *
   * @return Redirects to view orga-upload-csv
   */
  @GetMapping("")
  @Secured("ROLE_orga")
  public String redirectOrga() {

    return "redirect:/zulassung2/orga/upload-csv";
  }

  /**
   * This method is called for a GET request to /orga/upload-csv.
   *
   * @param token contains account data
   * @param model Spring object that is used as a container to supply the variables
   * @return Returns view orga-upload-csv
   */
  @GetMapping("/upload-csv")
  @Secured("ROLE_orga")
  public String orga(KeycloakAuthenticationToken token, Model model) {
    resetMessages();
    model.addAttribute("account", accountCreator.createFromPrincipal(token));
    model.addAttribute("students", students);

    return "orga-upload-csv";
  }

  /**
   * This method is called for a POST request to /orga/upload-csv.
   *
   * @param file File that was uploaded
   * @return Redirects to view orga-upload-csv
   */

  @PostMapping("/upload-csv")
  @Secured("ROLE_orga")
  public String submit(@RequestParam("file") MultipartFile file, String subject, String semester) {
    if (!file.getContentType().contains("csv")) {
      setDangerMessage("Die Datei muss im .csv Format sein!");
      return "redirect:/zulassung2/orga/upload-csv";
    }
    currentSubject = subject.replaceAll("[: ]", "-");
    currentSemester = semester.replaceAll("[: ]", "-");
    students = organisatorService.processCSVUpload(file);
    if (students == null) {
      setDangerMessage("Die Datei konnte nicht gelesen werden!");
      return "redirect:/zulassung2/orga/upload-csv";
    }

    return "redirect:/zulassung2/orga/upload-csv";
  }

  /**
   * This method is called for a POST request to /orga/sendmail.
   * It calls "createFilesAndMails" in the EmailService to create emails and then send them.
   *
   * @return Redirects to view orga-upload-csv
   */
  @PostMapping("/sendmail")
  @Secured("ROLE_orga")
  public String sendMail() {
    boolean firstError = true;
    for (Student student : students) {
      File file = emailService.createFile(student, currentSubject);
      try {
        emailService.sendMail(student, currentSubject, file);
      } catch (MessagingException e) {
        if (firstError) {
          setDangerMessage("An folgende Studenten konnte keine Email versendet werden: "
              + student.getForeName() + " " + student.getName());
          firstError = false;
        } else {
          setDangerMessage(dangerMessage.concat(", "
              + student.getForeName() + " " + student.getName()));
        }
      }
      String bucketName = nameCreator.createBucketName(student);
      if (!minIoHelper.bucketExists(bucketName)) {
        minIoHelper.makeBucket(bucketName);
      }

      minIoHelper.putObject(bucketName, file.getName(), file.getPath(), file.length(),
          new HashMap<String, String>(), ".txt");
    }
    if (firstError) {
      setSuccessMessage("Alle Emails wurden erfolgreich versendet.");
    } else {
      setWarningMessage("Es wurden nicht alle Emails korrekt versendet.");
    }
    return "redirect:/zulassung2/orga/upload-csv";
  }

  /**
   * This method is called for a POST request to /orga/sendmail/individual.
   * It calls "createFilesAndMails" in the EmailService to create emails and then send them.
   * In doing so, it uses the provided counter to get to the student from the list of students.
   *
   * @return Redirects to view orga-upload-csv
   */
  @PostMapping("/sendmail/individual")
  @Secured("ROLE_orga")
  public String sendMail(@RequestParam("count") int count) {
    Student selectedStudent = students.get(count);
    File file = emailService.createFile(selectedStudent, currentSubject);
    try {
      emailService.sendMail(selectedStudent, currentSubject, file);
      setSuccessMessage("Email an " + selectedStudent.getForeName() + " "
          + selectedStudent.getName()
          + " wurde erfolgreich versendet.");
    } catch (MessagingException e) {
      setDangerMessage("Email an " + selectedStudent.getForeName()
          + " " + selectedStudent.getName()
          + " konnte nicht versendet werden!");
    }
    return "redirect:/zulassung2/orga/upload-csv";
  }

  /**
   * Set Warning and Success Messages for the frontend.
   *
   * @param warningMessage Describe warning
   * @param successMessage Send a joyful message to the user
   */
  private void setMessages(String dangerMessage, String warningMessage, String successMessage) {
    this.dangerMessage = dangerMessage;
    this.warningMessage = warningMessage;
    this.successMessage = successMessage;
  }

  /**
   * Set Danger Message for the frontend.
   *
   * @param dangerMessage Describe danger
   */
  private void setDangerMessage(String dangerMessage) {
    this.dangerMessage = dangerMessage;
  }

  /**
   * Set Warning Message for the frontend.
   *
   * @param warningMessage Describe warning
   */
  private void setWarningMessage(String warningMessage) {
    this.warningMessage = warningMessage;
  }

  /**
   * Set Success Message for the frontend.
   *
   * @param successMessage Send a joyful message to the user
   */
  private void setSuccessMessage(String successMessage) {
    this.successMessage = successMessage;
  }

  /**
   * Reset UI Messages.
   */
  private void resetMessages() {
    setMessages(null, null, null);
  }

  @ModelAttribute("danger")
  String getDanger() {
    return dangerMessage;
  }

  @ModelAttribute("warning")
  String getWarning() {
    return warningMessage;
  }

  @ModelAttribute("success")
  String getSuccess() {
    return successMessage;
  }

  @ModelAttribute("subject")
  String getCurrentSubject() {
    return currentSubject;
  }

  @ModelAttribute("semester")
  String getCurrentSemester() {
    return currentSemester;
  }
}