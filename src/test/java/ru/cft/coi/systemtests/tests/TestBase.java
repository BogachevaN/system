package ru.cft.coi.systemtests.tests;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.spi.json.JacksonJsonNodeJsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import lombok.val;
import ru.cft.coi.systemtests.auth.FincertAuth;
import ru.cft.coi.systemtests.auth.PasswordAuth;

import java.io.File;
import java.io.IOException;

public abstract class TestBase {

    protected static final PasswordAuth COI_AUTH = new PasswordAuth(
            getUsername(),
            getPassword(),
            getRealm(),
            "webapp");

    protected static final FincertAuth FINCERT_AUTH = new FincertAuth("api-test@zoe", "!Welcome1");

    private static final Configuration JSON_PATH_CONFIG = Configuration.builder()
            .jsonProvider(new JacksonJsonNodeJsonProvider())
            .mappingProvider(new JacksonMappingProvider())
            .build();

    private static final String DEFAULT_REALM = "coi-beta";
    private static final String DEFAULT_USERNAME = "systemtests";
    private static final String DEFAULT_PASSWORD = "systemtests";

    protected static DocumentContext getJsonResource(String name) throws IOException {
        val loader = TestBase.class.getClassLoader();
        val file = new File(loader.getResource(name).getFile());

        return JsonPath.using(JSON_PATH_CONFIG).parse(file);
    }

    protected static String getRealm() {
        return getEnvVarOrDefault("COI_REALM", DEFAULT_REALM);
    }

    protected static String getUsername() {
        return getEnvVarOrDefault("COI_USERNAME", DEFAULT_USERNAME);
    }

    protected static String getPassword() {
        return getEnvVarOrDefault("COI_PASSWORD", DEFAULT_PASSWORD);
    }

    private static String getEnvVarOrDefault(String name, String defaultValue) {
        val value = System.getenv(name);
        return value != null ? value : defaultValue;
    }
}
