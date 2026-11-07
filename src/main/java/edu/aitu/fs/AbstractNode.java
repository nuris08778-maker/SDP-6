package edu.aitu.fs;

import java.util.Optional;

/**
 * Shared name and parent bookkeeping for the two element types.
 * The parent link is only writable from inside this package (by {@link DirectoryNode}),
 * so ownership invariants cannot be bypassed through the public API.
 */
public abstract class AbstractNode implements Element {

    private final String name;
    private DirectoryNode parent;

    protected AbstractNode(String name) {
        this.name = NameValidator.requireValid(name);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Optional<DirectoryNode> getParent() {
        return Optional.ofNullable(parent);
    }

    void attachTo(DirectoryNode newParent) {
        this.parent = newParent;
    }

    void detach() {
        this.parent = null;
    }
}
