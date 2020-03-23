package mops.zulassung2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.OrganisatorService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(OrgaUploadCSVController.class)
@ComponentScan(basePackageClasses = {KeycloakSecurityComponents.class,
    KeycloakSpringBootConfigResolver.class})
class OrgaUploadCSVControllerTest {

  MockMultipartFile mockCsvFile;
  @Autowired
  MockMvc mvc;
  @MockBean
  EmailService emailService;
  @MockBean
  OrganisatorService organisatorService;
  @MockBean
  SignatureService signatureService;

  @BeforeEach
  void createFile() {
    mockCsvFile = new MockMultipartFile(
        "file",
        "file.csv",
        "text/csv",
        "Test Data".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  @WithMockKeycloackAuth("studentin")
  void orgaCSVAsStudentIsRefused() throws Exception {
    // Arrange
    MockHttpSession mockHttpSession = new MockHttpSession();
    List<Student> students = new ArrayList<>();
    students.add(mock(Student.class));
    when(organisatorService.processCSVUpload(mockCsvFile)).thenReturn(students);

    // Act and Assert
    mvc.perform(get("/zulassung2/orga/upload-csv"))
        .andExpect(status().isForbidden());
    mvc.perform(post("/zulassung2/orga/sendmail")
        .with(csrf()))
        .andExpect(status().isForbidden());
    mvc.perform(post("/zulassung2/orga/sendmail/individual")
        .param("count", "0")
        .session(mockHttpSession)
        .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaCSVAsOrgaIsOk() throws Exception {
    mvc.perform(get("/zulassung2/orga/upload-csv"))
        .andExpect(status().isOk());
  }

  @Test
  void orgaCSVAsUnauthorizedIsRedirected() throws Exception {
    mvc.perform(get("/zulassung2/orga/upload-csv"))
        .andExpect(status().is(302));
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
  void testSendMailsToAllStudents() throws Exception {
    mvc.perform(post("/zulassung2/orga/sendmail")
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/orga/upload-csv"));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSendMailToIndividualStudent() throws Exception {
    // Arrange
    MockHttpSession mockHttpSession = new MockHttpSession();
    List<Student> students = new ArrayList<>();
    // Student student = new Student("2727912", "tigeu100@hhu.de", "geuer", "tim");
    students.add(mock(Student.class));
    when(organisatorService.processCSVUpload(mockCsvFile)).thenReturn(students);

    mvc.perform(multipart("/zulassung2/orga/upload-csv")
        .file(mockCsvFile)
        .param("subject", "Propra")
        .param("semester", "WS1920")
        .session(mockHttpSession)
        .with(csrf()));

    // Act and Assert
    mvc.perform(post("/zulassung2/orga/sendmail/individual")
        .param("count", "0")
        .session(mockHttpSession)
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/orga/upload-csv"));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSendMailsWithInvalidCsrfToken() throws Exception {
    mvc.perform(post("/zulassung2/orga/sendmail")
        .with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden());
  }

}
