package com.brokilone.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.maven.plugin.logging.Log;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;

public class PomVersionUpdater {

    public void executeUpdate(String pomFilePath,
                              Log log,
                              String repositoryUrl,
                              String selectPattern,
                              Integer rowNumber,
                              String sortPattern,
                              String format) {

        try {
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(new File(pomFilePath));
            log.info(document.toString());

            Element root = document.getRootElement();

            List<Element> children = root.getChildren();
            Element dependenciesElement = null;
            for (Element child : children) {
                if (Objects.equals(child.getName(), "dependencies")) {
                    dependenciesElement = child;
                    log.info(dependenciesElement.toString());
                }
            }

            if (dependenciesElement != null) {
                List<Element> dependency = dependenciesElement.getChildren();
                for (Element element : dependency) {
                    updateVersion(log, element, repositoryUrl, selectPattern,
                            rowNumber, sortPattern, format);
                }
            } else {
                log.error("Тег <version> не найден.");
            }

            XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
            try (FileOutputStream fos = new FileOutputStream(pomFilePath)) {
                xmlOutputter.output(document, fos);
            }

        } catch (Exception e) {
            log.error("Не удалось вывести версии", e);
        }
    }

    private void updateVersion(Log log, Element element, String repositoryUrl,
                               String selectPattern, Integer rowNumber, String sortPattern,
                               String format) {
        String currentVersion = null;
        String groupId = null;
        String artifactId = null;
        Element currentVersionElement = null;

        for (Element child : element.getChildren()) {
            if (Objects.equals(child.getName(), "version")) {
                currentVersion = child.getText();
                currentVersionElement = child;
            }
            if (Objects.equals(child.getName(), "groupId")) {
                groupId = child.getText();
            }
            if (Objects.equals(child.getName(), "artifactId")) {
                artifactId = child.getText();
            }
        }
        log.info("Current dependency %s:%s:%s".formatted(groupId, artifactId, currentVersion));

        try {
            URL url = new URL(repositoryUrl + selectPattern.formatted(groupId,
                    artifactId, sortPattern, rowNumber, format));
            log.info("Url: " + url);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            int responseCode = con.getResponseCode();
            log.info("responseCode " + responseCode);
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                log.info("responseContent " + content);

                ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                MavenRepoResponseDto mavenRepoResponseDto = objectMapper.readValue(content.toString(),
                        MavenRepoResponseDto.class);
                if (mavenRepoResponseDto != null && mavenRepoResponseDto.getResponse() != null) {
                    List<DocDto> docs = mavenRepoResponseDto.getResponse().getDocs();
                    if (docs != null && !docs.isEmpty()) {
                        DocDto docDto = docs.get(0);
                        String responseArtifactId = docDto.getA();
                        String responseGroupId = docDto.getG();
                        String lastVersion = docDto.getV();

                        if (Objects.equals(artifactId, responseArtifactId) &&
                                Objects.equals(groupId, responseGroupId) && currentVersionElement != null &&
                                lastVersion != null) {
                            log.info("updated dependency %s:%s:%s".formatted(
                                    responseGroupId, responseArtifactId, lastVersion
                            ));
                            currentVersionElement.setText(lastVersion);
                        }
                    }
                } else {
                    log.error("Не удалось обнаружить последнюю версию зависимости");

                }
            }
        } catch (IOException e) {
            log.error("Не удалось выполнить запрос", e);
        }
    }

}
