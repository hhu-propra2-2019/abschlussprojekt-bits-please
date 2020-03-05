package mops.zulassung2.controller;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import mops.zulassung2.model.Account;
import mops.zulassung2.model.Entry;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

@Controller
public class Zulassung2Controller {

  private final Counter authenticatedAccess;
  private final Counter publicAccess;

  @Value("${dev_private_key}")
  private String devPrivateKey;

  @Value("${dev_public_key}")
  private String devPublicKey;


  public Zulassung2Controller(MeterRegistry registry) {
    authenticatedAccess = registry.counter("access.authenticated");
    publicAccess = registry.counter("access.public");
  }


  /**
   * Nimmt das Authentifizierungstoken von Keycloak und erzeugt ein AccountDTO für die Views.
   * *
   *
   * @param token mit den Rollen des Accounts
   * @return neuen Account, der im Template verwendet wird
   */


  private Account createAccountFromPrincipal(KeycloakAuthenticationToken token) {
    KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
    return new Account(
        principal.getName(),
        principal.getKeycloakSecurityContext().getIdToken().getEmail(),
        null,
        token.getAccount().getRoles());
  }

  /**
   * Bei einem GET-Request auf root wird diese Funktion aufgerufen.
   * *
   *
   * @param token mit den Rollen des Accounts
   * @param model Objekt von Spring, das als Container genutzt wird, um die Variablen mitzuliefern
   * @return gibt eine view zurück, die gerendert werden kann
   */
  @GetMapping("/")
  public String index(KeycloakAuthenticationToken token, Model model) {
    if (token != null) {
      model.addAttribute("account", createAccountFromPrincipal(token));
    }
    publicAccess.increment();
    return "index";
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
    model.addAttribute("account", createAccountFromPrincipal(token));
    model.addAttribute("entries", Entry.generate(10));
    authenticatedAccess.increment();
    return "orga";
  }

  /**
   * Bei einem GET-Request auf /studi wird diese Funktion aufgerufen.
   * *
   *
   * @param token mit den Rollen des Accounts
   * @param model Objekt von Spring, das als Container genutzt wird, um die Variablen mitzuliefern
   * @return gibt eine view zurück, die gerendert werden kann
   */
  @GetMapping("/studi")
  @Secured("ROLE_studentin")
  public String studentin(KeycloakAuthenticationToken token, Model model) {
    model.addAttribute("account", createAccountFromPrincipal(token));
    model.addAttribute("entries", Entry.generate(10));
    authenticatedAccess.increment();
    return "studentin";
  }

  /**
   * Bei einem GET-Request auf /personal wird diese Funktion aufgerufen.
   * *
   *
   * @param token mit den Rollen des Accounts
   * @param model Objekt von Spring, das als Container genutzt wird, um die Variablen mitzuliefern
   * @return gibt eine view zurück, die gerendert werden kann
   */
  @GetMapping("/personal")
  @RolesAllowed({"ROLE_orga", "ROLE_studentin"})
  public String personal(KeycloakAuthenticationToken token, Model model) {
    model.addAttribute("account", createAccountFromPrincipal(token));
    authenticatedAccess.increment();
    return "personal";
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
    return "redirect:/";
  }
}
