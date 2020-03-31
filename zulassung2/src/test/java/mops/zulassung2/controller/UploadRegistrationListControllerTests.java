package mops.zulassung2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.zulassung2.model.dataobjects.Student;
import mops.zulassung2.model.dataobjects.UploadCSVForm;
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

  @BeforeEach
  void createFile() {
    mockCsvFile = new MockMultipartFile(
        "file",
        "file.csv",
        "text/csv",
        "Test Data".getBytes(StandardCharsets.UTF_8));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void uploadListAsOrgaIsOk() throws Exception {
    mvc.perform(get("/zulassung2/upload-registrationlist"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSubmitCsvFile() throws Exception {
    // Arrange
    UploadCSVForm form = new UploadCSVForm();
    form.setMultipartFile(mockCsvFile);
    form.setSubject("Propra2");
    form.setSemester("WS1920");
    // Act and Assert
    mvc.perform(multipart("/zulassung2/upload-registrationlist")
        .file(mockCsvFile)
        .flashAttr("form", form)
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/upload-registrationlist"));

  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSubmitCsvFileWithInvalidCsrf() throws Exception {
    // Arrange
    UploadCSVForm form = new UploadCSVForm();
    form.setMultipartFile(mockCsvFile);
    form.setSubject("Propra2");
    form.setSemester("WS1920");
    // Act and Assert
    mvc.perform(multipart("/zulassung2/upload-registrationlist")
        .file(mockCsvFile)
        .flashAttr("form", form)
        .with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden());

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

  @Test
  @WithMockKeycloackAuth("orga")
  void testSendWarningMailsIndividual() throws Exception {
    // Arrange
    MockHttpSession mockHttpSession = new MockHttpSession();

    UploadCSVForm form = new UploadCSVForm();
    form.setMultipartFile(mockCsvFile);
    form.setSubject("Propra");
    form.setSemester("WS1920");
    form.setDeadline("Deadline");

    List<Student> students = new ArrayList<>();
    students.add(mock(Student.class));
    when(fileService.processCSVUpload(mockCsvFile)).thenReturn(students);

    mvc.perform(multipart("/zulassung2/upload-registrationlist")
        .file(mockCsvFile)
        .flashAttr("form", form)
        .session(mockHttpSession)
        .with(csrf()));

    // Act and Assert
    mvc.perform(post("/zulassung2/sendmailreglist/individual")
        .param("count", "0")
        .session(mockHttpSession)
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/upload-registrationlist"));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSendWarningMailsIndividualWithInvalidCsrf() throws Exception {
    // Arrange
    MockHttpSession mockHttpSession = new MockHttpSession();

    UploadCSVForm form = new UploadCSVForm();
    form.setMultipartFile(mockCsvFile);
    form.setSubject("Propra");
    form.setSemester("WS1920");
    form.setDeadline("Deadline");

    List<Student> students = new ArrayList<>();
    students.add(mock(Student.class));
    when(fileService.processCSVUpload(mockCsvFile)).thenReturn(students);

    mvc.perform(multipart("/zulassung2/upload-registrationlist")
        .file(mockCsvFile)
        .flashAttr("form", form)
        .session(mockHttpSession)
        .with(csrf()));

    // Act and Assert
    mvc.perform(post("/zulassung2/sendmailreglist/individual")
        .param("count", "0")
        .session(mockHttpSession)
        .with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testExportAllowed() throws Exception {
    mvc.perform(post("/zulassung2/export-allowed")
        .with(csrf()))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testExportAllowedWithInvalidCrsf() throws Exception {
    mvc.perform(post("/zulassung2/export-allowed")
        .with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSetDeadline() throws Exception {
    //Arrange
    UploadCSVForm form = new UploadCSVForm();
    form.setDeadline("Deadline");

    //Act & Assert
    mvc.perform(post("/zulassung2/upload-registrationlist/set-deadline")
        .flashAttr("form", form)
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/upload-registrationlist"));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void testSetDeadlineWithInvalidCrsf() throws Exception {
    //Arrange
    UploadCSVForm form = new UploadCSVForm();
    form.setDeadline("Deadline");

    //Act & Assert
    mvc.perform(post("/zulassung2/upload-registrationlist/set-deadline")
        .flashAttr("form", form)
        .with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithMockKeycloackAuth("studentin")
  void studentIsRefused() throws Exception {
    // Arrange
    List<Student> students = new ArrayList<>();
    students.add(mock(Student.class));
    when(fileService.processCSVUpload(mockCsvFile)).thenReturn(students);

    // Act and Assert
    mvc.perform(get("/zulassung2/upload-registrationlist"))
        .andExpect(status().isForbidden());
    mvc.perform(multipart("/zulassung2/upload-registrationlist")
        .file(mockCsvFile))
        .andExpect(status().isForbidden());
    mvc.perform(post("/zulassung2/sendmailreglist")
        .with(csrf()))
        .andExpect(status().isForbidden());
    mvc.perform(post("/zulassung2/sendmailreglist/individual")
        .param("count", "0")
        .with(csrf()))
        .andExpect(status().isForbidden());
    mvc.perform(post("/zulassung2/export-allowed")
        .with(csrf()))
        .andExpect(status().isForbidden());
    mvc.perform(post("/zulassung2/upload-registrationlist/set-deadline")
        .with(csrf()))
        .andExpect(status().isForbidden());
  }

  @Test
  void unauthorizedIsRedirected() throws Exception {
    //Arrange
    List<Student> students = new ArrayList<>();
    students.add(mock(Student.class));
    when(fileService.processCSVUpload(mockCsvFile)).thenReturn(students);

    //Act & Assert
    mvc.perform(get("/zulassung2/upload-registrationlist"))
        .andExpect(redirectedUrl("/sso/login"));
    mvc.perform(multipart("/zulassung2/upload-registrationlist")
        .file(mockCsvFile)
        .with(csrf()))
        .andExpect(redirectedUrl("/sso/login"));
    mvc.perform(post("/zulassung2/sendmailreglist")
        .with(csrf()))
        .andExpect(redirectedUrl("/sso/login"));
    mvc.perform(post("/zulassung2/sendmailreglist/individual")
        .param("count", "0")
        .with(csrf()))
        .andExpect(redirectedUrl("/sso/login"));
    mvc.perform(post("/zulassung2/export-allowed")
        .with(csrf()))
        .andExpect(redirectedUrl("/sso/login"));
    mvc.perform(post("/zulassung2/upload-registrationlist/set-deadline")
        .with(csrf()))
        .andExpect(redirectedUrl("/sso/login"));
  }
}