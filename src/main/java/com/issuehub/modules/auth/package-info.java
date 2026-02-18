@ApplicationModule(
        displayName = "Auth",
        allowedDependencies = {
                "shared",
                "modules.developers::events"
        }
)
package com.issuehub.modules.auth;

import org.springframework.modulith.ApplicationModule;