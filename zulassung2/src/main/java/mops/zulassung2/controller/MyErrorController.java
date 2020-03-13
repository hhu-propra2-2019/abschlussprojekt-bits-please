package mops.zulassung2.controller;

import org.keycloak.authorization.client.util.Http;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {

  @Override
  public String getErrorPath() {
    return "/error";
  }

  /**
   * Bei einer Request die einen Error verursacht wird diese Funktion aufgerufen.
   * *
   *
   * @param request Art der Htttp Request
   * @return gibt eine entsprechende error view zurück
   */
  @RequestMapping("/error")
  public String handleError(HttpServletRequest request) {
    Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

    if (status != null) {
      Integer statusCode = Integer.valueOf(status.toString());

      if (statusCode == HttpStatus.NOT_FOUND.value()) {
        return "error-404";
      } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
        return "error-500";
      } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
        return "error-403";
      }
    }
    return "error-500";
  }
}