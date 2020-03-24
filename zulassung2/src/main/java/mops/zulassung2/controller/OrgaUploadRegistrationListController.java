package mops.zulassung2.controller;

import mops.zulassung2.model.dataobjects.AccountCreator;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.FileService;
import mops.zulassung2.services.OrgaUploadRegistrationService;
import org.apache.commons.io.FilenameUtils;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;

@SessionScope
@RequestMapping("/zulassung2")
@Controller
public class OrgaUploadRegistrationListController {

  private final FileService fileService;
  private final EmailService emailService;
  private final OrgaUploadRegistrationService orgaUploadRegistrationService;
  public List<Student> notAllowed = new ArrayList<>();
  public List<Student> allowed = new ArrayList<>();
  public String currentSubject = "";
  public String currentSemester = "";
  private AccountCreator accountCreator;
  private String dangerMessage;
  private String warningMessage;
  private String successMessage;


  /**
   * Constructs OrganisatorController by injecting Beans of
   * FileService, SignatureService and Emailservice.
   *
   * @param fileService  Service for parsing files
   * @param emailService Service for sending emails
   */
  public OrgaUploadRegistrationListController(FileService fileService,
                                              EmailService emailService,
                                              OrgaUploadRegistrationService orgaUploadRegistrationService) {
    accountCreator = new AccountCreator();
    this.orgaUploadRegistrationService = orgaUploadRegistrationService;
    this.fileService = fileService;
    this.emailService = emailService;
  }


  /**
   * This method is called for a GET request to /upload-registrationlist.
   *
   * @param token contains account data
   * @param model Spring object that is used as a container to supply the variables
   * @return Returns view upload-registrationlist
   */
  @GetMapping("/upload-registrationlist")
  @Secured("ROLE_orga")
  public String orga(KeycloakAuthenticationToken token, Model model) {
    resetMessages();
    model.addAttribute("account", accountCreator.createFromPrincipal(token));
    model.addAttribute("notallowed", notAllowed);
    model.addAttribute("allowed", allowed);
    model.addAttribute("orgauploadregistrationservice", new OrgaUploadRegistrationService());

    return "orga-upload-registrationlist";
  }

  /**
   * This method is called for a POST request to /upload-registrationlist.
   *
   * @param file File that was uploaded
   * @return Redirects to view orga-upload-registrationlist
   */

  @PostMapping("/upload-registrationlist")
  @Secured("ROLE_orga")
  public String submit(@RequestParam("file") MultipartFile file, String subject, String semester) {
    if (!FilenameUtils.isExtension(file.getOriginalFilename(), "csv")) {
      setDangerMessage("Die Datei muss im .csv Format sein!");
      return "redirect:/zulassung2/upload-registrationlist";
    }
    currentSubject = subject.replaceAll("[: ]", "-");
    currentSemester = semester.replaceAll("[: ]", "-");
    List<Student> students = fileService.processCSVUpload(file);
    if (students == null) {
      setDangerMessage("Die Datei konnte nicht gelesen werden!");
      return "redirect:/zulassung2/registrationlist";
    }
    notAllowed.clear();
    allowed.clear();
    for (Student student : students) {
      if (!orgaUploadRegistrationService.test(student, subject)) {
        notAllowed.add(student);
      } else {
        allowed.add(student);
      }
    }
    if (notAllowed.isEmpty()) {
      setSuccessMessage("Alle Angemeldeten verfügen über eine gültige Zulassung!");
    } else if (!allowed.isEmpty()) {
      setWarningMessage("Es konnte nicht für alle Angemeldeten eine gültige Zulassung gefunden werden."
          + " Bitte lassen Sie den Betroffenen über untenstehendes Formular eine Nachricht zukommen.");
    } else {
      setDangerMessage("Es konnte für keinen Angemeldeten eine gültige Zulassung gefunden werden."
          + " Haben Sie die korrekte Anmeldeliste hochgeladen?");
    }
    return "redirect:/zulassung2/upload-registrationlist";
  }


  /**
   * This method is called for a POST request to /sendmailreglist.
   * It calls "createFilesAndMails" in the EmailService to create emails and then send them.
   *
   * @return Redirects to view upload-registrationlist
   */
  @PostMapping("/sendmailreglist")
  @Secured("ROLE_orga")
  public String sendWarningMail() {
    boolean firstError = true;
    for (Student student : notAllowed) {
      try {
        emailService.sendWarningMail(student, currentSubject);
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

    }
    if (firstError) {
      setSuccessMessage("Alle Emails wurden erfolgreich versendet.");
    } else {
      setWarningMessage("Es wurden nicht alle Emails korrekt versendet.");
    }
    return "redirect:/zulassung2/upload-registrationlist";
  }


  /**
   * This method is called for a POST request to /sendmailreglist/individual.
   * It calls "createFilesAndMails" in the EmailService to create emails and then send them.
   * In doing so, it uses the provided counter to get to the student from the list of students.
   *
   * @return Redirects to view upload-registrationlist
   */
  @PostMapping("/sendmailreglist/individual")
  @Secured("ROLE_orga")
  public String sendWarningMail(@RequestParam("count") int count) {
    Student selectedStudent = notAllowed.get(count);
    try {
      emailService.sendWarningMail(selectedStudent, currentSubject);
      setSuccessMessage("Email an " + selectedStudent.getForeName() + " "
          + selectedStudent.getName()
          + " wurde erfolgreich versendet.");
    } catch (MessagingException e) {
      setDangerMessage("Email an " + selectedStudent.getForeName()
          + " " + selectedStudent.getName()
          + " konnte nicht versendet werden!");
    }
    return "redirect:/zulassung2/upload-registrationlist";
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