package mops.zulassung2.controller;

import mops.zulassung2.model.AccountCreator;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@Controller
public class Zulassung2Controller {

  private AccountCreator accountCreator;

  /**
   * erstellt ein Obejekt vom Zulassungs2Controller.
   * Parameter: @param registry
   */

  public Zulassung2Controller() { //MeterRegistry registry) {
    accountCreator = new AccountCreator();
  }


  /**
   * Bei einem GET-Request auf root wird diese Funktion aufgerufen.
   * *
   *
   * @param token mit den Rollen des Accounts
   * @param model Objekt von Spring, das als Container genutzt wird, um die Variablen mitzuliefern
   * @return gibt eine view zurück, die gerendert werden kann
   */
  @GetMapping("/zulassung2")
  @RolesAllowed({"ROLE_orga", "ROLE_studentin", "ROLE_actuator"})
  public String index(KeycloakAuthenticationToken token, Model model) {
    if (token != null) {
      model.addAttribute("account", accountCreator.createFromPrincipal(token));
    }
    return "index";
  }

  /**
   * Bei einem GET-Request auf /logout wird diese Funktion aufgerufen.
   * *
   *
   * @return routet zurück auf root
   */
  @GetMapping("/logout")
  public String logout(HttpServletRequest request) throws Exception {
    request.logout();
    return "redirect:/zulassung2/";
  }
}
