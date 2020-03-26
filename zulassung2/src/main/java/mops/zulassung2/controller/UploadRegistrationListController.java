package mops.zulassung2.controller;

import mops.zulassung2.model.dataobjects.AccountCreator;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.dataobjects.UploadCSVForm;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.FileService;
import mops.zulassung2.services.UploadRegistrationService;
import org.apache.commons.io.FilenameUtils;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@SessionScope
@RequestMapping("/zulassung2")
@Controller
public class UploadRegistrationListController {

  private final FileService fileService;
  private final EmailService emailService;
  private final UploadRegistrationService uploadRegistrationService;
  public List<Student> notAllowed = new ArrayList<>();
  public List<Student> allowed = new ArrayList<>();
  private UploadCSVForm uploadCSVForm = new UploadCSVForm();
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
  public UploadRegistrationListController(FileService fileService,
                                          EmailService emailService,
                                          UploadRegistrationService uploadRegistrationService) {
    accountCreator = new AccountCreator();
    this.uploadRegistrationService = uploadRegistrationService;
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
    model.addAttribute("form", uploadCSVForm);

    return "upload-registrationlist";
  }

  /**
   * This method is called for a POST request to /upload-registrationlist.
   *
   * @return Redirects to view orga-upload-registrationlist
   */

  @PostMapping("/upload-registrationlist")
  @Secured("ROLE_orga")
  public String submit(@ModelAttribute("form") UploadCSVForm form) {
    if (!FilenameUtils.isExtension(form.getMultipartFile().getOriginalFilename(), "csv")) {
      setDangerMessage("Die Datei muss im .csv Format sein!");
      return "redirect:/zulassung2/upload-registrationlist";
    }
    uploadCSVForm.setSubject(form.getSubject().replaceAll("[: ]", "-"));
    uploadCSVForm.setDeadline(form.getDeadline());

    List<Student> students = fileService.processCSVUpload(form.getMultipartFile());
    if (students == null) {
      setDangerMessage("Die Datei konnte nicht gelesen werden!");
      return "redirect:/zulassung2/registrationlist";
    }


    notAllowed.clear();
    allowed.clear();
    for (Student student : students) {
      if (!uploadRegistrationService.test(student, form.getSubject())) {
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
          + " Haben Sie die korrekte Anmeldeliste hochgeladen?"
          + " Möglicherweise ist MinIO nicht verfügbar.");
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
    if (uploadCSVForm.getDeadline() == null) {
      setDangerMessage("Bitte legen Sie zunächst eine Abgabefrist fest.");
      return "redirect:/zulassung2/upload-registrationlist";
    }
    boolean firstError = true;
    for (Student student : notAllowed) {
      try {
        emailService.sendWarningMail(student, uploadCSVForm.getSubject(), uploadCSVForm.getDeadline());
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
    if (uploadCSVForm.getDeadline() == null) {
      setDangerMessage("Bitte legen Sie zunächst eine Abgabefrist fest.");
      return "redirect:/zulassung2/upload-registrationlist";
    }
    Student selectedStudent = notAllowed.get(count);
    try {
      emailService.sendWarningMail(selectedStudent, uploadCSVForm.getSubject(), uploadCSVForm.getDeadline());
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
   * This method is called for a POST request to /export-allowed.
   * It creates a CSV-file which contains the approved students and
   * offers the user a dialog to download the file.
   */
  @PostMapping("/export-allowed")
  @Secured("ROLE_orga")
  public void exportCSVFile(HttpServletResponse response) {
    try {
      File csvOutput = new File("allowed.csv");
      FileWriter writer = new FileWriter(csvOutput, UTF_8);
      writer.write("matriculationnumber,email,name,forename");
      for (Student student : allowed) {
        writer.write("\n" + student.getMatriculationNumber() + ","
            + student.getEmail() + ","
            + student.getName() + ","
            + student.getForeName());
      }
      writer.close();

      response.setContentType("text/csv");
      response.setHeader("Content-Disposition",
          "attachment; filename=" + uploadCSVForm.getSubject() + "_zugelassen.csv");

      response.setContentLength((int) csvOutput.length());
      InputStream inputStream = new BufferedInputStream(new FileInputStream(csvOutput));
      FileCopyUtils.copy(inputStream, response.getOutputStream());

      Files.deleteIfExists(csvOutput.toPath());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * This method is called for a POST request to /upload-registrationlist/set-deadline.
   * It sets the deadline that will be given later in the reminder email to the student.
   *
   * @return Redirects to view upload-registrationlist
   */
  @PostMapping("/upload-registrationlist/set-deadline")
  @Secured("ROLE_orga")
  public String setDeadline(@ModelAttribute("form") UploadCSVForm form) {
    uploadCSVForm.setDeadline(form.getDeadline());
    setSuccessMessage("Abgabefrist wurde gespeichert.");
    return "redirect:/zulassung2/upload-registrationlist";
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
    this.dangerMessage = null;
    this.warningMessage = null;
    this.successMessage = null;
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
}

