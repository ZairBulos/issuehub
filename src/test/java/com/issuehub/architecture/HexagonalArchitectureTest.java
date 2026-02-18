package com.issuehub.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(
        packages = "com.issuehub",
        importOptions = ImportOption.DoNotIncludeTests.class
)
class HexagonalArchitectureTest {

    @ArchTest
    static final ArchRule domain_should_only_depend_on_domain =
            classes().that().resideInAPackage("..domain..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "..domain..",
                            "java..",
                            "org.springframework.modulith.."
                    )
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule application_should_only_depend_on_domain =
            classes().that().resideInAPackage("..application..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "..application..",
                            "..domain..",
                            "java..",
                            "org.springframework.modulith.."
                    )
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule infrastructure_can_depend_on_everything_above =
            classes().that().resideInAPackage("..infrastructure..")
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(
                            "..infrastructure..",
                            "..application..",
                            "..domain..",
                            "java..",
                            "org.springframework..",
                            "org.hibernate..",
                            "jakarta..",
                            "org.slf4j..",
                            "lombok..",
                            "com.fasterxml.."
                    )
                    .allowEmptyShould(true);

}
