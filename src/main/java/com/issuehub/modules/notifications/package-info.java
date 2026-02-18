@ApplicationModule(
        displayName = "Notifications",
        allowedDependencies = {
                "shared",
                "modules.auth::events"
        }
)
package com.issuehub.modules.notifications;

import org.springframework.modulith.ApplicationModule;