package com.anpk.firstDemoLearnSpring.infrastructure.config.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.mail")
@Getter
@Setter
public class MailProperties {
    private String from;
    // Base URL chung của web, ví dụ: https://myapp.com
    private String baseUrl;
}

