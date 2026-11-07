package edu.aitu.fs;

import java.util.Optional;

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
