package mops.zulassung2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.FileService;
import mops.zulassung2.services.MinIoService;
import mops.zulassung2.services.SignatureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UploadRegistrationListController.class)
@ComponentScan(basePackageClasses = {KeycloakSecurityComponents.class,
    KeycloakSpringBootConfigResolver.class})

public class UploadRegistrationListControllerTests {
  MockMultipartFile mockCsvFile;
  @Autowired
  MockMvc mvc;
  @MockBean
  FileService fileService;
  @MockBean
  SignatureService signatureService;
  @MockBean
  MinIoService minIoService;
  @MockBean
  EmailService emailService;

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaListAsOrgaIsOk() throws Exception {
    mvc.perform(get("/zulassung2/upload-registrationlist"))
        .andExpect(status().isOk());
  }
  @Test
  void orgaListAsUnauthorizedIsRedirected() throws Exception {
    mvc.perform(get("/zulassung2/upload-registrationlist"))
        .andExpect(status().is(302));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSendWarningMailsToAllStudents() throws Exception {
    mvc.perform(post("/zulassung2/sendmailreglist")
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/upload-registrationlist"));
  }
  @Test
  @WithMockKeycloackAuth("orga")
  void testSendWarningMailsWithInvalidCsrfToken() throws Exception {
    mvc.perform(post("/zulassung2/sendmailreglist")
        .with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden());
  }
}