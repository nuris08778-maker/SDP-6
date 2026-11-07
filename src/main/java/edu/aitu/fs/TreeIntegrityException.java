package edu.aitu.fs;

/**
 * Thrown when an operation would break a file-system invariant: negative size,
 * invalid or duplicate name, second parent, or a cycle. The tree is never
 * modified when this exception is thrown.
 */
public class TreeIntegrityException extends RuntimeException {

    public TreeIntegrityException(String message) {
        super(message);
    }
}
