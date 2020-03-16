package mops.zulassung2.controller;

import mops.zulassung2.model.CustomNameCreator;
import mops.zulassung2.model.MinIoHelper;
import mops.zulassung2.model.NameCreator;
import mops.zulassung2.model.dataobjects.AccountCreator;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.OrganisatorService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
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
  private NameCreator nameCreator;
  private MinIoHelper minIoHelper;
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
    currentSubject = subject.replaceAll("[: ]", "-");
    currentSemester = semester.replaceAll("[: ]", "-");
    students = organisatorService.processCSVUpload(file);

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
    for (Student student : students) {
      File file = emailService.createFile(student, currentSubject);
      emailService.sendMail(student, currentSubject, file);

      String bucketName = nameCreator.createBucketName(student);
      if (!minIoHelper.bucketExists(bucketName)) {
        minIoHelper.makeBucket(bucketName);
      }

      minIoHelper.putObject(bucketName, file.getName(), file.getPath(), file.length(),
              new HashMap<String, String>(), ".txt");
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
    emailService.sendMail(selectedStudent, currentSubject, file);
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