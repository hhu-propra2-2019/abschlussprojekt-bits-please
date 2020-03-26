package mops.zulassung2;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "mops")
public class OnionArchitectureTest {

  @ArchTest
  static final ArchRule servicePackageRule = classes()
      .that().haveSimpleNameEndingWith("Service")
      .should().resideInAPackage("..zulassung2.services..");

  @ArchTest
  static final ArchRule servicesDontGripInController = noClasses()
      .that().haveSimpleNameEndingWith("Service")
      .should().accessClassesThat().resideInAPackage("..zulassung2.controller..");

  @ArchTest
  static final ArchRule servicesDontGripInTasks = noClasses()
      .that().haveSimpleNameEndingWith("Service")
      .should().accessClassesThat().resideInAPackage("..zulassung2.tasks..");

  @ArchTest
  static final ArchRule servicesDontGripInT = noClasses()
      .that().haveSimpleNameEndingWith("Service")
      .should().accessClassesThat().resideInAPackage("..zulassung2.config..");

  @ArchTest
  static final ArchRule modelDontGripInController = noClasses()
      .that().resideInAPackage("..zulassung2.model..")
      .should().accessClassesThat().resideInAPackage("..zulassung2.controller..");

  @ArchTest
  static final ArchRule modelDontGripInServices = noClasses()
      .that().resideInAPackage("..zulassung2.model..")
      .should().accessClassesThat().resideInAPackage("..zulassung2.services..");

  @ArchTest
  static final ArchRule modelDontGripInConfig = noClasses()
      .that().resideInAPackage("..zulassung2.model..")
      .should().accessClassesThat().resideInAPackage("..zulassung2.config..");

  @ArchTest
  static final ArchRule modelDontGripInTasks = noClasses()
      .that().resideInAPackage("..zulassung2.model..")
      .should().accessClassesThat().resideInAPackage("..zulassung2.tasks..");

  @ArchTest
  static final ArchRule controllerPackageRule = classes()
      .that().haveSimpleNameEndingWith("Controller")
      .should().resideInAPackage("..zulassung2.controller..");

  @ArchTest
  static final ArchRule taskPackageRule = classes()
      .that().haveSimpleNameEndingWith("Task")
      .should().resideInAPackage("..zulassung2.tasks..");

  @ArchTest
  static final ArchRule configPackageRule = classes()
      .that().haveSimpleNameEndingWith("Config")
      .should().resideInAPackage("..zulassung2.config..");

  @ArchTest
  static final ArchRule modelPackageRule = noClasses()
      .that().haveSimpleNameEndingWith("Task")
      .or().haveSimpleNameEndingWith("Controller")
      .or().haveSimpleNameEndingWith("Service")
      .or().haveSimpleNameEndingWith("Config")
      .should().resideInAPackage("..zulassung2.model..");
}
