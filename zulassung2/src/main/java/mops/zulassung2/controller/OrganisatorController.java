package mops.zulassung2.controller;

import mops.zulassung2.model.AccountCreator;
import mops.zulassung2.model.Entry;
import mops.zulassung2.model.Student;
import mops.zulassung2.model.crypto.Receipt;
import mops.zulassung2.model.crypto.SignatureService;
import mops.zulassung2.model.fileparsing.CustomCSVLineParser;
import mops.zulassung2.model.fileparsing.CustomValidator;
import mops.zulassung2.model.fileparsing.FileParser;
import mops.zulassung2.model.Student;
import mops.zulassung2.services.OrganisatorService;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
  public List<Student> students = new ArrayList<>();
  private AccountCreator accountCreator;

  public OrganisatorController(OrganisatorService organisatorService, SignatureService signatureService) {
    accountCreator = new AccountCreator();
    this.organisatorService = organisatorService;
    this.signatureService = signatureService;
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
    model.addAttribute("account", accountCreator.createFromPrincipal(token));
    model.addAttribute("entries", Entry.generate(10));
    model.addAttribute("students", students);

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
  public String submit(@RequestParam("file") MultipartFile file) {
    students = organisatorService.processCSVUpload(file);

    return "redirect:/zulassung2/orga";
  }

  /**
   * Uploads receipt.
   *
   * @param receipt Textfile provided by user
   * @return Returns view depending on the validity of the receipt.
   */
  @PostMapping("/orga/validate-receipt")
  @Secured("ROLE_orga")
  public String uploadReceipt(@RequestParam("receipt") MultipartFile receipt) {

    if (receipt.isEmpty()) {
      //setMessages("Die übergebene Quittung ist leer!", null);
    }

    FileParser txtParser = new FileParser(new CustomValidator());
    List<String> lines = txtParser.processTXT(receipt);

    boolean valid = signatureService.verify(new Receipt(lines.get(0), lines.get(1)));

    if (valid) {
      //setMessages(null, "Die Quittung ist gültig!");
    } else {
      //setMessages("Die übergebene Quittung ist ungültig!", null);
    }
    return "redirect:/zulassung2/orga";
  }
}