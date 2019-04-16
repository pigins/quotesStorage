package com.birobot.quotes_storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="database")
public class DatabaseConfig {
    private String schemaName;
    private String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    @Override
    public String toString() {
        return "DatabaseConfig{" +
                "schemaName='" + schemaName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}