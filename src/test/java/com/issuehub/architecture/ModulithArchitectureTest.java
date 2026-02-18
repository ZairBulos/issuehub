package com.issuehub.architecture;

import com.issuehub.IssueHubApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithArchitectureTest {

    @Test
    void verifyModularStructure() {
        ApplicationModules.of(IssueHubApplication.class).verify();
    }

    @Test
    void writeDocumentation() {
        var modules = ApplicationModules.of(IssueHubApplication.class);
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

}
