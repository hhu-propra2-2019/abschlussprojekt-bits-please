package mops.zulassung2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.OrganisatorService;
import mops.zulassung2.services.SignatureService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrgaUploadCSVController.class)
@ComponentScan(basePackageClasses = {KeycloakSecurityComponents.class,
    KeycloakSpringBootConfigResolver.class})
class OrgaUploadCSVControllerTest {

  static MockMultipartFile mockCsvFile;
  @Autowired
  MockMvc mvc;
  @MockBean
  EmailService emailService;
  @MockBean
  OrganisatorService organisatorService;
  @MockBean
  SignatureService signatureService;

  @BeforeAll
  static void createFile() {
/*
    String header = "matriculationnumber,email,name,forename\n";
    String student1Data = "2727912,tigeu100@hhu.de,geuer,tim\n";
    String student2Data = "2757144,masie@hhu.de,siewert,markus\n";
    String data = header + student1Data + student2Data;
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    String name = "testFile.csv";
    String originalFileName = "test_students.csv";
    MockMultipartFile mockCsvFile = new MockMultipartFile(name, originalFileName, "text/csv", bytes);
*/

    mockCsvFile = new MockMultipartFile(
        "MockCsvFile",
        "Receipt.csv",
        "text/csv",
        "Test Data".getBytes(StandardCharsets.UTF_8));
  }

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

  @Test
  @WithMockKeycloackAuth("orga")
  void testSubmitCsvFile() throws Exception {
    mvc.perform(multipart("/zulassung2/orga/upload-csv")
        .file(mockCsvFile)
        .param("subject", "Propra")
        .param("semester", "WS1920")
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/orga/upload-csv"));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSendMails() throws Exception {
    // Arrange

    // Act & Assert
    mvc.perform(post("/zulassung2/orga/sendmail")).andExpect(status().is(302));
  }

  // testen dass falsche Rolle Forbidden ist
}
