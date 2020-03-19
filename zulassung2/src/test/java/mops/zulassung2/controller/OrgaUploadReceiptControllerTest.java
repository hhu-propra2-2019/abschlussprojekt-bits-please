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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrgaUploadReceiptController.class)
@ComponentScan(basePackageClasses = {KeycloakSecurityComponents.class,
    KeycloakSpringBootConfigResolver.class})
class OrgaUploadReceiptControllerTest {

  static MockMultipartFile mockMultipartFile;
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
    mockMultipartFile = new MockMultipartFile(
        "MockReceipt",
        "Receipt.txt",
        "text/plain",
        "Test Data".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  @WithMockKeycloackAuth("studentin")
  void studentIsRefused() throws Exception {
    mvc.perform(get("/zulassung2/orga/upload-receipt"))
        .andExpect(status().isForbidden());
    mvc.perform(multipart("/zulassung2/orga/upload-receipt")
        .file(mockMultipartFile)
        .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaGetIsOk() throws Exception {
    mvc.perform(get("/zulassung2/orga/upload-receipt"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaPostWithValidCsrfIsOk() throws Exception {
    mvc.perform(multipart("/zulassung2/orga/upload-receipt")
        .file(mockMultipartFile)
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/orga/upload-receipt"));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaPostWithInvalidCsrfIsRefused() throws Exception {
    mvc.perform(multipart("/zulassung2/orga/upload-receipt")
        .file(mockMultipartFile)
        .with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void unauthorizedIsRedirectedToLogin() throws Exception {
    mvc.perform(get("/zulassung2/orga/upload-receipt"))
        .andExpect(redirectedUrl("/sso/login"));
    mvc.perform(multipart("/zulassung2/orga/upload-receipt")
        .file(mockMultipartFile)
        .with(csrf()))
        .andExpect(redirectedUrl("/sso/login"));
  }
}