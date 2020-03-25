package mops.zulassung2;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.Architectures.onionArchitecture;

public class OnionArchitectureTest {

  @ArchTest
  static final ArchRule rule4 = onionArchitecture()
          .domainModels("propra.domain..")
          .domainServices("propra.domain..")
          .applicationServices("propra.services..")
          .adapter("web", "propra.web")
          .adapter("persistence", "propra.db");
}
