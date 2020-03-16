package mops.zulassung2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.OrganisatorService;
import mops.zulassung2.services.SignatureService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrgaUploadCSVController.class)
@ComponentScan(basePackageClasses = {KeycloakSecurityComponents.class,
    KeycloakSpringBootConfigResolver.class})
class OrgaUploadCSVControllerTest {

  @Autowired
  MockMvc mvc;
  @MockBean
  EmailService emailService;


  @MockBean
  OrganisatorService organisatorService;
  @MockBean
  SignatureService signatureService;

  @Test
  @WithMockKeycloackAuth("studentin")
  void orgaCSVAsStudentIsRefused() throws Exception {
    mvc.perform(get("/zulassung2/orga/upload-csv")).andExpect(status().isForbidden());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaCSVAsOrgaIsOk() throws Exception {
    mvc.perform(get("/zulassung2/orga/upload-csv")).andExpect(status().isOk());
  }

  @Test
  void orgaCSVAsUnauthorizedIsRedirected() throws Exception {
    mvc.perform(get("/zulassung2/orga/upload-csv")).andExpect(status().is(302));
  }
}
