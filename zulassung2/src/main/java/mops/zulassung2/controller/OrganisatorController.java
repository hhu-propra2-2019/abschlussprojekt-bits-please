package mops.zulassung2.controller;

import mops.zulassung2.model.AccountCreator;
import mops.zulassung2.model.Entry;
import mops.zulassung2.model.fileparsing.CustomCSVLineParser;
import mops.zulassung2.model.fileparsing.CustomValidator;
import mops.zulassung2.model.fileparsing.FileParser;
import mops.zulassung2.model.Student;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
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

  public List<Student> students = new ArrayList<>();
  private AccountCreator accountCreator;

  public OrganisatorController() {
    accountCreator = new AccountCreator();
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
    FileParser csvParser = new FileParser(new CustomValidator(), new CustomCSVLineParser());
    students.addAll(csvParser.processCSV(file));
    return "redirect:/zulassung2/orga";
  }
}