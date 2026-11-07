package edu.aitu.fs;

/**
 * Visitor contract: one overloaded {@code visit} method per concrete element type.
 * This is the second step of double dispatch.
 */
public interface Visitor {

    void visit(FileNode file);

    void visit(DirectoryNode directory);
}
