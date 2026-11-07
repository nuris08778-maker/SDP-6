package edu.aitu.fs;

/** Single place that decides which node names are legal. */
final class NameValidator {

    private NameValidator() {
    }

    static String requireValid(String name) {
        if (name == null || name.isBlank()) {
            throw new TreeIntegrityException("Name must not be null or blank");
        }
        if (name.contains("/")) {
            throw new TreeIntegrityException("Name must not contain '/': " + name);
        }
        if (name.equals(".") || name.equals("..")) {
            throw new TreeIntegrityException("Reserved name: " + name);
        }
        return name;
    }
}
