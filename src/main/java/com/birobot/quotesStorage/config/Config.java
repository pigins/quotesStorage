package com.birobot.quotesStorage.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Config {
    private static Logger logger = LogManager.getLogger();

    private final String[] args;
    private final Path homeDir;
    private FileConfig fileConfig;

    public Config(String[] args) {
        this.args = args;
        homeDir = Paths.get(System.getProperty("user.home")).resolve(".quotes_store");
    }

    public void init() {
        try {
            createHomeDirectoryIfNotExists();
            if (checkConfigFileExists()) {
                readConfigFile();
            } else {
                writeDefaultConfigFile();
            }
        } catch (Exception e) {
            logger.error(e);
            throw new RuntimeException(e);
        }
    }

    public Path getHomeDir() {
        return homeDir;
    }

    public FileConfig getFileConfig() {
        return fileConfig;
    }

    private void createHomeDirectoryIfNotExists() {
        if (!homeDir.toFile().exists()) {
            boolean mkdir = homeDir.toFile().mkdir();
            if (!mkdir) {
                throw new RuntimeException("directory " + homeDir + "can't be created");
            }
        }
    }

    private void readConfigFile() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(FileConfig.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        fileConfig = (FileConfig) unmarshaller.unmarshal(getConfigFile());
        fileConfig.getDbConfig().setAppDir(homeDir);
    }

    private void writeDefaultConfigFile() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(FileConfig.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        fileConfig = new FileConfig();
        fileConfig.setSymbols(new HashSet<>(Collections.singletonList("BTCUSDT")));
        DbConfig defaultDbConfig = new DbConfig();
        defaultDbConfig.setConnectionPoolSize(100);
        defaultDbConfig.setSchemaName("QUOTES");
        defaultDbConfig.setAppDir(homeDir);
        fileConfig.setDbConfig(defaultDbConfig);
        List<ProxyServer> defaultProxyServers = new ArrayList<>();
        defaultProxyServers.add(new ProxyServer("someValidHttpsProxy", 1080));
        fileConfig.setProxyServers(defaultProxyServers);
        File configFile = getConfigFile();
        configFile.createNewFile();
        marshaller.marshal(fileConfig, configFile);
    }

    private boolean checkConfigFileExists() {
        return getConfigFile().exists();
    }

    private File getConfigFile() {
        return homeDir.resolve("config.xml").toFile();
    }
}