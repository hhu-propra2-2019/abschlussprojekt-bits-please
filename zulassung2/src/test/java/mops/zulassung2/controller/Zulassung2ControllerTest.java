package mops.zulassung2.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest
class Zulassung2ControllerTest {

  @Autowired
  MockMvc mvc;

  @Test
  void index() throws Exception {
    mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
  }

  @Test
  void orga() throws Exception {

  }

  @Test
  void studentin() {
  }

  @Test
  void personal() {
  }

  @Test
  void logout() {
  }
}