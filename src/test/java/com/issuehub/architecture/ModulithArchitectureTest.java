package com.issuehub.architecture;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModulithArchitectureTest {

    @Test
    void verifyModularStructure() {
        ApplicationModules.of("com.issuehub.modules").verify();
    }

    @Test
    void writeDocumentation() {
        var modules = ApplicationModules.of("com.issuehub.modules");
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

}
