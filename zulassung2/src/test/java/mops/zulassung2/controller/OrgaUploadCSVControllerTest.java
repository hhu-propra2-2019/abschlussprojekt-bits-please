package mops.zulassung2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.services.EmailService;
import mops.zulassung2.services.OrganisatorService;
import mops.zulassung2.services.SignatureService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    String header = "matriculationnumber,email,name,forename\n";
    String student1Data = "2727912,tigeu100@hhu.de,geuer,tim\n";
    String student2Data = "2757144,masie@hhu.de,siewert,markus\n";
    String data = header + student1Data + student2Data;
    byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
    mockCsvFile = new MockMultipartFile("file", "originalFileName", "text/csv", bytes);
  }

  @Test
  @WithMockKeycloackAuth("studentin")
  void orgaCSVAsStudentIsRefused() throws Exception {
    mvc.perform(get("/zulassung2/orga/upload-csv"))
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
    Student stud = new Student("2727912", "tigeu100@hhu.de", "geuer", "tim");
    mvc.perform(multipart("/zulassung2/orga/upload-csv")
        .file(mockCsvFile)
        .param("subject", "Propra")
        .param("semester", "WS1920")
        .with(csrf()));

    ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
    ArgumentCaptor<File> fileCaptor = ArgumentCaptor.forClass(File.class);
    verify(emailService, times(1)).sendMail(
        studentCaptor.capture(), "Ihr Zulassungsnachweis zum Fach: ", fileCaptor.capture());
    // assertThat(studentCaptor.getValue().getForeName()).isEqualTo("sth");

    /*    OrgaUploadCSVController orgaUploadCSVController
        = new OrgaUploadCSVController(organisatorService, emailService);

    orgaUploadCSVController.students.add(new Student(
        "2727912",
        "tigeu100@hhu.de",
        "geuer",
        "tim"));*/


    // Act and Assert
    mvc.perform(post("/zulassung2/orga/sendmail/individual")
        .param("count", "0")
        .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSendMailsWithInvalidCsrfToken() throws Exception {
    mvc.perform(post("/zulassung2/orga/sendmail")
        .with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden());
  }

}
