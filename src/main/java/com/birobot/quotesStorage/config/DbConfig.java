package com.birobot.quotesStorage.config;

import javax.xml.bind.annotation.XmlElement;
import java.nio.file.Path;

public class DbConfig {
    private int connectionPoolSize;
    private String schemaName;
    private Path appDir;

    public DbConfig() {
    }

    @XmlElement
    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public Path getDir() {
        return appDir.resolve("data").resolve("db");
    }

    public void setAppDir(Path appDir) {
        this.appDir = appDir;
    }

    @XmlElement
    public String getSchemaName() {
        return "QUOTES";
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
}
