package it.finanze.sanita.fse2.ms.srvsyntaxrulesmanager.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
@Slf4j
public class PomRuntimeCFG {

    @Autowired
    private BuildProperties entries;

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        log.info("Currently running on: {}", entries.getVersion());
    }

}
