package mops.zulassung2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(Zulassung2Controller.class)
@ComponentScan(
    basePackageClasses = {KeycloakSecurityComponents.class, KeycloakSpringBootConfigResolver.class})
class Zulassung2ControllerTest {

  @Autowired
  MockMvc mvc;

  @Test
  @WithMockKeycloackAuth("studentin")
  void indexAsStudentIsOk() throws Exception {
    mvc.perform(get("/zulassung2")).andExpect(status().isOk());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void indexAsOrgaIsOk() throws Exception {
    mvc.perform(get("/zulassung2")).andExpect(status().isOk());
  }

  @Test
  @WithMockKeycloackAuth("actuator")
  void indexAsActuatorIsOk() throws Exception {
    mvc.perform(get("/zulassung2")).andExpect(status().isOk());
  }

  @Test
  void indexAsUnauthorizedIsRedirected() throws Exception {
    mvc.perform(get("/zulassung2")).andExpect(status().is(302));
  }
}
