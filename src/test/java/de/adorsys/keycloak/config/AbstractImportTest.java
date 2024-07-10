package de.adorsys.keycloak.config;

import de.adorsys.keycloak.config.configuration.TestConfiguration;
import de.adorsys.keycloak.config.extensions.GithubActionsExtension;
import de.adorsys.keycloak.config.model.RealmImport;
import de.adorsys.keycloak.config.provider.KeycloakImportProvider;
import de.adorsys.keycloak.config.service.RealmImportService;
import de.adorsys.keycloak.config.test.util.KeycloakAuthentication;
import de.adorsys.keycloak.config.test.util.KeycloakRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

@ExtendWith(SpringExtension.class)
@ExtendWith(GithubActionsExtension.class)
@ContextConfiguration(
        classes = {TestConfiguration.class},
        initializers = {ConfigDataApplicationContextInitializer.class}
)
@ActiveProfiles("IT")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@Timeout(value = 30, unit = SECONDS)
abstract public class AbstractImportTest {
    @Autowired
    public RealmImportService realmImportService;

    @Autowired
    public KeycloakImportProvider keycloakImportProvider;

    @Autowired
    public KeycloakRepository keycloakRepository;

    @Autowired
    public KeycloakAuthentication keycloakAuthentication;

    public String resourcePath;

    public void doImport(String fileName) throws IOException {
        doImport(fileName, realmImportService);
    }

    public void doImport(String fileName, RealmImportService _realmImportService) throws IOException {
        List<RealmImport> realmImports = getImport(fileName);

        for (RealmImport realmImport : realmImports) {
            _realmImportService.doImport(realmImport);
        }
    }

    public RealmImport getFirstImport(String fileName) throws IOException {
        return getImport(fileName).get(0);
    }

    public List<RealmImport> getImport(String fileName) {
        String location = buildLocation(fileName);
        Map<String, List<RealmImport>> realmImportMap = readRealmImports(location);

        return getFirstEntryValue(realmImportMap, location);
    }

    private String buildLocation(String fileName) {
        return "classpath:" + this.resourcePath + '/' + fileName;
    }

    private Map<String, List<RealmImport>> readRealmImports(String location) {
        Map<String, Map<String, List<RealmImport>>> nestedMap = getNestedMap(location);
        return flattenNestedMap(nestedMap);
    }

    private Map<String, Map<String, List<RealmImport>>> getNestedMap(String location) {
        return keycloakImportProvider.readFromLocations(location).getRealmImports();
    }

    private Map<String, List<RealmImport>> flattenNestedMap(Map<String, Map<String, List<RealmImport>>> nestedMap) {
        return nestedMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().values().stream().flatMap(List::stream).collect(Collectors.toList())
                ));
    }

    private List<RealmImport> getFirstEntryValue(Map<String, List<RealmImport>> realmImportMap, String location) {
        List<RealmImport> realmImports = realmImportMap.get(location);
        if (realmImports == null || realmImports.isEmpty()) {
            throw new IllegalArgumentException("No realm imports found for location: " + location);
        }
        return realmImports;
    }
}
