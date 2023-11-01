package com.vsl700.nitflex.components;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("nitflex")
@Getter
@Setter
public class SharedProperties {
    private String moviesFolder;
    private int downloadInterval;
    private String movieRequestPrivilege;
}
