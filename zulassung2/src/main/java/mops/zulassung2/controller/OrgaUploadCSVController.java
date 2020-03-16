package mops.zulassung2.controller;

import mops.zulassung2.model.dataobjects.AccountCreator;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.services.CustomReceiptData;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.OrganisatorService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@SessionScope
@RequestMapping("/zulassung2/orga")
@Controller
public class OrgaUploadCSVController {

  private final OrganisatorService organisatorService;
  private final EmailService emailService;
  public List<Student> students = new ArrayList<>();
  public String currentSubject;
  public String currentSemester;
  private AccountCreator accountCreator;
  private String dangerMessage;
  private String errorMessage;
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
    this.organisatorService = organisatorService;
    this.emailService = emailService;
  }

  /**
   * Bei einem GET-Request auf /orga wird diese Funktion aufgerufen.
   * *
   *
   * @param token mit den Rollen des Accounts
   * @param model Objekt von Spring, das als Container genutzt wird, um die Variablen mitzuliefern
   * @return gibt eine view zurück, die gerendert werden kann
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
   * Bei einem POST-Request auf /orga/upload-csv wird diese Funktion aufgerufen.
   * *
   *
   * @param file ist das File, welches hochgeladen wurde.
   * @return gibt die view orga zurück.
   */

  @PostMapping("/upload-csv")
  @Secured("ROLE_orga")
  public String submit(@RequestParam("file") MultipartFile file, String subject, String semester) {
    currentSubject = subject.replaceAll("[: ]", "-");
    currentSemester = semester.replaceAll("[: ]", "-");
    students = organisatorService.processCSVUpload(file);

    return "redirect:/zulassung2/orga/upload-csv";
  }

  /**
   * Bei einem POST-Request auf /orga/sendmail wird diese Funktion aufgerufen.
   * *
   * Diese Methode ruft "createFilesAndMails" im EmailService auf
   * um Emails zu erstellen und dann zu verschicken.
   *
   * @return gibt die view orga zurück.
   */
  @PostMapping("/sendmail")
  @Secured("ROLE_orga")
  public String sendMail() {
    for (Student student : students) {
      emailService.createFileAndMail(
          new CustomReceiptData(student, currentSubject, currentSemester));
    }
    return "redirect:/zulassung2/orga/upload-csv";
  }

  /**
   * Bei einem POST-Request auf /orga/sendmail/individual wird diese Funktion aufgerufen.
   * *
   * Diese Methode ruft "createFilesAndMails" im EmailService auf
   * um Emails zu erstellen und dann zu verschicken. Hierbei nutzt sie den mitgegebenen
   * Counter, um nur die gewünschte E-Mail individuell an den Studenten zu verschicken.
   *
   * @return Leitet auf die View orga-upload-csv weiter.
   */
  @PostMapping("/sendmail/individual")
  @Secured("ROLE_orga")
  public String sendMail(@RequestParam("count") int count) {
    emailService.createFileAndMail(
        new CustomReceiptData(students.get(count), currentSubject, currentSemester));
    return "redirect:/zulassung2/orga/upload-csv";
  }

  /**
   * Set Error and Success Messages for the frontend.
   *
   * @param errorMessage   Describe error
   * @param successMessage Send a joyful message to the user
   */
  private void setMessages(String dangerMessage, String errorMessage, String successMessage) {
    this.dangerMessage = dangerMessage;
    this.errorMessage = errorMessage;
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
   * Set Error Message for the frontend.
   *
   * @param errorMessage Describe error
   */
  private void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
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

  @ModelAttribute("error")
  String getError() {
    return errorMessage;
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