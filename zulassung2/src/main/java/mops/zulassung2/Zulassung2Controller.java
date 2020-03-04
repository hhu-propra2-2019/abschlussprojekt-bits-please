package mops.zulassung2;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
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

  public Zulassung2Controller(MeterRegistry registry) {
    authenticatedAccess = registry.counter("access.authenticated");
    publicAccess = registry.counter("access.public");
  }


  /**
   * Nimmt das Authentifizierungstoken von Keycloak und erzeugt ein AccountDTO für die Views.
   * *
   *
   * @param token
   * @return neuen Account der im Template verwendet wird
   */


  private Account createAccountFromPrincipal(KeycloakAuthenticationToken token) {
    KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
    return new Account(
        principal.getName(),
        principal.getKeycloakSecurityContext().getIdToken().getEmail(),
        null,
        token.getAccount().getRoles());
  }

  @GetMapping("/")
  public String index(KeycloakAuthenticationToken token, Model model) {
    if(token != null) {
      model.addAttribute("account", createAccountFromPrincipal(token));
    }
    publicAccess.increment();
    return "index";
  }

  @GetMapping("/orga")
  @Secured("ROLE_orga")
  public String orga(KeycloakAuthenticationToken token, Model model) {
    model.addAttribute("account", createAccountFromPrincipal(token));
    model.addAttribute("entries", Entry.generate(10));
    authenticatedAccess.increment();
    return "orga";
  }

  @GetMapping("/studi")
  @Secured("ROLE_studentin")
  public String studentin(KeycloakAuthenticationToken token, Model model) {
    model.addAttribute("account", createAccountFromPrincipal(token));
    model.addAttribute("entries", Entry.generate(10));
    authenticatedAccess.increment();
    return "studentin";
  }


  @GetMapping("/personal")
  @RolesAllowed({"ROLE_orga", "ROLE_studentin"})
  public String personal(KeycloakAuthenticationToken token, Model model) {
    model.addAttribute("account", createAccountFromPrincipal(token));
    authenticatedAccess.increment();
    return "personal";
  }

  @GetMapping("/logout")
  public String logout(HttpServletRequest request) throws Exception {
    request.logout();
    return "redirect:/";
  }
}
