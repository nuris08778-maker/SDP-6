package edu.aitu.fs;

import java.util.Optional;

/** Common interface of everything that can live in the file-system tree. */
public interface Element {

    String getName();

    /** The directory that owns this element, or empty if it is a root. */
    Optional<DirectoryNode> getParent();

    /** First step of double dispatch: the element selects the matching visit overload. */
    void accept(Visitor visitor);
}
