package io.github.rieske.dbtest;

public final class Environment {
    private Environment() {
    }

    public static String getEnvOrDefault(String envVarName, String defaultValue) {
        String envValue = System.getenv(envVarName);
        return envValue != null ? envValue : defaultValue;
    }

}
