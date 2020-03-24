package mops.zulassung2.controller;

import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.model.dataobjects.AccountCreator;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.services.FileService;
import mops.zulassung2.services.ReceiptDataInterface;
import mops.zulassung2.services.SignatureService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SessionScope
@RequestMapping("/zulassung2")
@Controller
public class UploadReceiptsController {

  private final FileService fileService;
  private final SignatureService signatureService;
  private List<ReceiptDataInterface> verifiedReceipts = new ArrayList<>();
  private AccountCreator accountCreator;
  private String dangerMessage;
  private String warningMessage;
  private String successMessage;

  /**
   * Constructs Controller by injecting Beans of
   * OrganisatorService, SignatureService and Emailservice.
   *
   * @param fileService      Service for parsing files
   * @param signatureService Service for signing files
   */
  public UploadReceiptsController(FileService fileService,
                                  SignatureService signatureService) {
    accountCreator = new AccountCreator();
    this.fileService = fileService;
    this.signatureService = signatureService;
  }

  /**
   * This method is called for a GET request to /orga/upload-receipt.
   *
   * @param token contains account data
   * @param model Spring object that is used as a container to supply the variables
   * @return Returns view orga-upload-receipt
   */
  @GetMapping("/upload-receipt")
  @Secured({"ROLE_orga", "ROLE_studentin"})
  public String redirectOrga(KeycloakAuthenticationToken token, Model model) {
    resetMessages();
    model.addAttribute("account", accountCreator.createFromPrincipal(token));
    model.addAttribute("receipts", verifiedReceipts);

    return "upload-receipt";
  }

  /**
   * Stores the receipt from a single student in MinIO.
   *
   * @param count the selected student
   * @return redirect to /zulassung2/upload-receipt
   */
  @PostMapping("/submit-receipt/individual")
  @Secured({"ROLE_orga", "ROLE_studentin"})
  public String submitIndividualReceipt(@RequestParam("count") int count) {
    ReceiptDataInterface receiptData = verifiedReceipts.get(count);
    if (!receiptData.isValid()) {
      setDangerMessage("Zulassung von "
          + receiptData.getForeName()
          + " " + receiptData.getName()
          + " zur Veranstaltung "
          + receiptData.getModule()
          + " ist ungültig und konnte daher nicht eingereicht werden.");
      return "redirect:/zulassung2/upload-receipt";
    }
    File file = fileService.createFileFromSubmittedReceipt(receiptData);
    Student student = new Student(
        receiptData.getMatriculationNumber(),
        receiptData.getEmail(),
        receiptData.getName(),
        receiptData.getForeName());

    fileService.storeReceipt(student, file);
    //TODO: Fehlermeldungen von MinIO anzeigen
    setSuccessMessage("Zulassung von "
        + receiptData.getForeName()
        + " " + receiptData.getName()
        + " zur Veranstaltung "
        + receiptData.getModule()
        + " wurde erfolgreich eingereicht.");
    return "redirect:/zulassung2/upload-receipt";
  }

  /**
   * This method is called for a POST request to /orga/upload-receipt.
   *
   * @param receiptMultipartFile Textfile provided by user
   * @return Returns view depending on the validity of the receipt.
   */
  @PostMapping("/upload-receipt")
  @Secured({"ROLE_orga", "ROLE_studentin"})
  public String uploadReceipt(@RequestParam("receipt") MultipartFile... receiptMultipartFile) {
    List<ReceiptDataInterface> receiptDataInterfaceList = new ArrayList<>();

    boolean firstError = true;
    for (MultipartFile rec : receiptMultipartFile) {

      List<String> receiptLines = fileService.processTXTUpload(rec);

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
        receiptDataInterfaceList.add(fileService.readReceiptContent(
            receiptLines.get(0),
            receiptLines.get(1)));
      }
    }

    boolean checkRun = false;
    boolean allReceiptsValid = true;
    for (ReceiptDataInterface data : receiptDataInterfaceList) {

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
        setWarningMessage("Alle hochgeladenen Quittungen wurden geprüft. "
            + "Bitte überprüfen Sie die Gültigkeit anhand der Tabelle.");
      }

    } else if (checkRun) {

      if (allReceiptsValid) { // not all files but all signatures are valid
        setSuccessMessage(" Neu hochgeladene Quittungen im korrekten Format wurden geprüft. "
            + "Die korrekt formatierten Quittungen sind gültig.");
      } else { // not all files and not all signatures are valid
        setWarningMessage(" Quittungen im korrekten Format wurden geprüft. "
            + "Bitte überprüfen Sie die Gültigkeit anhand der Tabelle.");
      }

    } else {
      setWarningMessage("Es wurden keine neuen Quittungen geprüft,"
          + " da keine dem geforderten Format entsprach.");
    }
    return "redirect:/zulassung2/upload-receipt";
  }

  /**
   * Set Error and Success Messages for the frontend.
   *
   * @param dangerMessage  Describe danger
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
   * Set Error Message for the frontend.
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
}
