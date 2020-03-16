package mops.zulassung2.controller;

import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.model.dataobjects.AccountCreator;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.OrganisatorService;
import mops.zulassung2.services.ReceiptData;
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
@RequestMapping("/zulassung2/orga")
@Controller
public class OrgaUploadReceiptController {

  private final OrganisatorService organisatorService;
  private final SignatureService signatureService;
  private final EmailService emailService;
  public List<Student> students = new ArrayList<>();
  public String currentSubject;
  public String currentSemester;
  private List<ReceiptData> verifiedReceipts = new ArrayList<>();
  private AccountCreator accountCreator;
  private String dangerMessage;
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
  public OrgaUploadReceiptController(OrganisatorService organisatorService,
                                     SignatureService signatureService,
                                     EmailService emailService) {
    accountCreator = new AccountCreator();
    this.organisatorService = organisatorService;
    this.signatureService = signatureService;
    this.emailService = emailService;
  }

  /**
   * Bei einem GET-Request auf /orga/upload-receipt wird diese Funktion aufgerufen.
   * *
   *
   * @param token mit den Rollen des Accounts
   * @param model Objekt von Spring, das als Container genutzt wird, um die Variablen mitzuliefern
   * @return gibt eine view zurück, die gerendert werden kann
   */
  @GetMapping("/upload-receipt")
  @Secured("ROLE_orga")
  public String uploadReceipts(KeycloakAuthenticationToken token, Model model) {
    resetMessages();
    model.addAttribute("account", accountCreator.createFromPrincipal(token));
    model.addAttribute("receipts", verifiedReceipts);

    return "orga-upload-receipt";
  }

  /**
   * Upload receipts.
   *
   * @param receipt Textfile provided by user
   * @return Returns view depending on the validity of the receipt.
   */
  @PostMapping("/upload-receipt")
  @Secured("ROLE_orga")
  public String uploadReceipt(@RequestParam("receipt") MultipartFile... receipt) {

    List<ReceiptData> receipts = new ArrayList<>();

    boolean firstError = true;
    for (MultipartFile rec : receipt) {

      List<String> receiptLines = organisatorService.processTXTUpload(rec);

      if (rec.isEmpty() || receiptLines == null) {

        if (firstError) {
          setDangerMessage("Folgende übergebene Quittungen haben ein falsches Format "
              + "und konnten daher nicht geprüft werden: "
              + rec.getOriginalFilename());
          firstError = false;
        } else {
          setDangerMessage(dangerMessage.concat(", " + rec.getOriginalFilename()));
        }

      } else {
        receipts.add(organisatorService.readReceiptContent(
            receiptLines.get(0),
            receiptLines.get(1)));
      }
    }

    boolean checkRun = false;
    boolean allReceiptsValid = true;
    for (ReceiptData data : receipts) {

      boolean valid = signatureService.verify(new Receipt(data.create(), data.getSignature()));
      data.setValid(valid);
      verifiedReceipts.add(data);
      if (!valid) {
        allReceiptsValid = false;
      }
      checkRun = true;
    }

    // Generating error messages
    if (dangerMessage == null) {

      if (allReceiptsValid) { // all files and signatures are valid
        setSuccessMessage("Alle neu hochgeladenen Quittungen wurden geprüft und sind gültig.");
      } else { // all files but not all signatures are valid
        setErrorMessage("Alle hochgeladenen Quittungen wurden geprüft. "
            + "Bitte überprüfen Sie die Gültigkeit anhand der Tabelle.");
      }

    } else if (checkRun) {

      if (allReceiptsValid) { // not all files but all signatures are valid
        setSuccessMessage(" Neu hochgeladene Quittungen im korrekten Format wurden geprüft. "
            + "Die korrekt formatierten Quittungen sind gültig.");
      } else { // not all files and not all signatures are valid
        setErrorMessage(" Quittungen im korrekten Format wurden geprüft. "
            + "Bitte überprüfen Sie die Gültigkeit anhand der Tabelle.");
      }

    } else {
      setErrorMessage("Es wurden keine neuen Quittungen geprüft,"
          + " da keine dem geforderten Format entsprach.");
    }
    return "redirect:/zulassung2/orga/upload-receipt";
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
}