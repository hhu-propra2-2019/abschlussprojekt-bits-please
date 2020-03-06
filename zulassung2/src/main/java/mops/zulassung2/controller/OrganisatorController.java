package mops.zulassung2.controller;

import mops.zulassung2.model.AccountCreator;
import mops.zulassung2.model.Entry;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/zulassung2")
@Controller
public class OrganisatorController {

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
    //authenticatedAccess.increment();
    return "orga";
  }
}
