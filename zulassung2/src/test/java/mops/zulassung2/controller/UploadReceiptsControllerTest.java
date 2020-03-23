package mops.zulassung2.controller;

import com.c4_soft.springaddons.test.security.context.support.WithMockKeycloackAuth;
import mops.zulassung2.services.FileService;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UploadReceiptsController.class)
@ComponentScan(basePackageClasses = {KeycloakSecurityComponents.class,
    KeycloakSpringBootConfigResolver.class})
class UploadReceiptsControllerTest {

  MockMultipartFile mockMultipartFile;
  @Autowired
  MockMvc mvc;
  @MockBean
  FileService fileService;
  @MockBean
  SignatureService signatureService;

  @BeforeEach
  void createFile() {
    mockMultipartFile = new MockMultipartFile(
        "receipt",
        "Receipt.txt",
        "text/plain",
        "MockData".getBytes(StandardCharsets.UTF_8));

    when(fileService.processTXTUpload(mockMultipartFile)).thenReturn(null);
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaGetIsOk() throws Exception {
    mvc.perform(get("/zulassung2/upload-receipt"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaPostWithValidCsrfOneFileIsOk() throws Exception {
    mvc.perform(multipart("/zulassung2/upload-receipt")
        .file(mockMultipartFile)
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/upload-receipt"));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaPostWithValidCsrfMultipleFilesIsOk() throws Exception {

    MockMultipartFile mockMultipartFile1 = new MockMultipartFile(
        "receipt",
        "Receipt.txt",
        "text/plain",
        "MockData1".getBytes(StandardCharsets.UTF_8));
    MockMultipartFile mockMultipartFile2 = new MockMultipartFile(
        "receipt",
        "Receipt.txt",
        "text/plain",
        "MockData2".getBytes(StandardCharsets.UTF_8));

    when(fileService.processTXTUpload(mockMultipartFile1)).thenReturn(null);
    when(fileService.processTXTUpload(mockMultipartFile2)).thenReturn(null);

    mvc.perform(multipart("/zulassung2/upload-receipt")
        .file(mockMultipartFile)
        .file(mockMultipartFile1)
        .file(mockMultipartFile2)
        .with(csrf()))
        .andExpect(redirectedUrl("/zulassung2/upload-receipt"));
  }

  @Test
  @WithMockKeycloackAuth("orga")
  void orgaPostWithInvalidCsrfIsRefused() throws Exception {
    mvc.perform(multipart("/zulassung2/upload-receipt")
        .file(mockMultipartFile)
        .with(csrf().useInvalidToken()))
        .andExpect(status().isForbidden());
  }

  @Test
  void unauthorizedIsRedirectedToLogin() throws Exception {
    mvc.perform(get("/zulassung2/upload-receipt"))
        .andExpect(redirectedUrl("/sso/login"));
    mvc.perform(multipart("/zulassung2/upload-receipt")
        .file(mockMultipartFile)
        .with(csrf()))
        .andExpect(redirectedUrl("/sso/login"));
  }
}