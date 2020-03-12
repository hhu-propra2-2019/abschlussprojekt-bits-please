package mops.zulassung2.controller;

import mops.zulassung2.model.AccountCreator;
import mops.zulassung2.model.Entry;
import mops.zulassung2.model.Student;
import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.services.CustomReceiptData;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.OrganisatorService;
import mops.zulassung2.services.SignatureService;
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
@RequestMapping("/zulassung2")
@Controller
public class OrganisatorController {

  private final OrganisatorService organisatorService;
  private final SignatureService signatureService;
  private final EmailService emailService;
  public List<Student> students = new ArrayList<>();
  private List<String> lines = new ArrayList<>();
  public String currentSubject;
  private AccountCreator accountCreator;
  private String errorMessage;
  private String successMessage;

  /**
   * Constructs OrganisatorController by injecting Beans of
   * OrganisatorService, SignatureService and Emailservice.
   *
   * @param organisatorService Service for parsing files
   * @param signatureService   Service for signing files
   * @param emailService       Service for sending emails
   */
  public OrganisatorController(OrganisatorService organisatorService,
                               SignatureService signatureService,
                               EmailService emailService) {
    accountCreator = new AccountCreator();
    this.organisatorService = organisatorService;
    this.signatureService = signatureService;
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
  @GetMapping("/orga")
  @Secured("ROLE_orga")
  public String orga(KeycloakAuthenticationToken token, Model model) {
    resetMessages();
    model.addAttribute("account", accountCreator.createFromPrincipal(token));
    model.addAttribute("entries", Entry.generate(10));
    model.addAttribute("students", students);
    model.addAttribute("receiptContent", lines);

    return "orga";
  }

  /**
   * Bei einem POST-Request auf /orga wird diese Funktion aufgerufen.
   * *
   *
   * @param file ist das File, welches hochgeladen wurde.
   * @return gibt die view orga zurück.
   */

  @PostMapping("/orga")
  @Secured("ROLE_orga")
  public String submit(@RequestParam("file") MultipartFile file, String subject) {
    currentSubject = subject;
    students = organisatorService.processCSVUpload(file);

    return "redirect:/zulassung2/orga";
  }

  /**
   * Uploads receipt.
   *
   * @param receipt Textfile provided by user
   * @return Returns view depending on the validity of the receipt.
   */
  @PostMapping("/orga/upload-receipt")
  @Secured("ROLE_orga")
  public String uploadReceipt(@RequestParam("receipt") MultipartFile receipt) {

    if (receipt.isEmpty()) {
      setMessages("Die übergebene Quittung ist leer.", null);
    } else if (lines == null) {
      setMessages("Die übergebene Quittung hat das falsche Format.", null);
    } else {
      lines = organisatorService.processTXTUpload(receipt);
      setMessages(null, "Quittung erfolgreich hochgeladen!");
    }
    return "redirect:/zulassung2/orga";
  }

  /**
   * Validates receipt.
   *
   * @return Returns view depending on the validity of the signature
   */
  @PostMapping("/orga/validate-receipt")
  @Secured("ROLE_orga")
  public String validateReceipt() {

    boolean valid = signatureService.verify(new Receipt(lines.get(0), lines.get(1)));

    if (valid) {
      //TODO: QUITTUNG GÜLTIG
      //setMessages(null, "Quittung ist gültig!");

    } else {
      //TODO: QUITTUNG UNGÜLTIG
      //setMessages("Die übergebene Quittung hat eine ungültige Signatur.", null);
    }
    return "redirect:/zulassung2/orga";
  }

  /**
   * Bei einem POST-Request auf /orga/sendmail wird diese Funktion aufgerufen.
   * *
   * Diese Methode ruft "createFilesAndMails" im EmailService auf
   * um Emails zu erstellen und dann zu verschicken.
   *
   * @return gibt die view orga zurück.
   */

  @PostMapping("/orga/sendmail")
  @Secured("ROLE_orga")
  public String sendMail() {
    for (Student student : students) {
      emailService.createFileAndMail(student, new CustomReceiptData(), currentSubject);
    }
    return "redirect:/zulassung2/orga";
  }

  /**
   * Set Error and Success Messages for the frontend.
   *
   * @param errorMessage   Describe error
   * @param successMessage Send a joyful message to the user
   */
  private void setMessages(String errorMessage, String successMessage) {
    this.errorMessage = errorMessage;
    this.successMessage = successMessage;
  }

  /**
   * Reset UI Messages.
   */
  private void resetMessages() {
    setMessages(null, null);
  }

  @ModelAttribute("error")
  String getError() {
    return errorMessage;
  }

  @ModelAttribute("success")
  String getSuccess() {
    return successMessage;
  }
}