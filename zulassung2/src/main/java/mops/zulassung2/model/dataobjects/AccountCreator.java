package mops.zulassung2.model.dataobjects;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;

public class AccountCreator {

  /**
   * Nimmt das Authentifizierungstoken von Keycloak und erzeugt ein AccountDTO für die Views.
   * *
   *
   * @param token mit den Rollen des Accounts
   * @return neuen Account, der im Template verwendet wird
   */


  public Account createFromPrincipal(KeycloakAuthenticationToken token) {
    KeycloakPrincipal principal = (KeycloakPrincipal) token.getPrincipal();
    return new Account(
            principal.getName(),
            principal.getKeycloakSecurityContext().getIdToken().getEmail(),
            null,
            token.getAccount().getRoles());
  }
}
