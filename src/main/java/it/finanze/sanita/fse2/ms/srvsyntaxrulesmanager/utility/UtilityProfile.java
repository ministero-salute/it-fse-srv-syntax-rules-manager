package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.utility;

import it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class UtilityProfile {
    @Autowired
    private Environment environment;

    public boolean isTestProfile() {
        if (environment != null && environment.getActiveProfiles().length > 0) {
            return environment.getActiveProfiles()[0].toLowerCase().contains(Constants.Profile.TEST);
        }
        return false;
    }
}