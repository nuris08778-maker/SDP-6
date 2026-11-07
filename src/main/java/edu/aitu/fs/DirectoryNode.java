package edu.aitu.fs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public final class DirectoryNode extends AbstractNode {
    private final Map<String, AbstractNode> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    public <T extends AbstractNode> T add(T child) {
        requireAddable(child);
        children.put(child.getName(), child);
        child.attachTo(this);
        return child;
    }

    public Optional<AbstractNode> remove(String childName) {
        AbstractNode removed = children.remove(childName);
        if (removed != null) {
            removed.detach();
        }
        return Optional.ofNullable(removed);
    }

    public List<Element> getChildren() {
        return Collections.unmodifiableList(new ArrayList<>(children.values()));
    }

    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    private void requireAddable(AbstractNode child) {
        if (child == null) {
            throw new TreeIntegrityException("Child must not be null");
        }
        if (child.getParent().isPresent()) {
            throw new TreeIntegrityException("'" + child.getName() + "' already has a parent");
        }
        if (children.containsKey(child.getName())) {
            throw new TreeIntegrityException("Duplicate sibling name: " + child.getName());
        }
        if (wouldCreateCycle(child)) {
            throw new TreeIntegrityException("Adding '" + child.getName() + "' would create a cycle");
        }
    }

    private boolean wouldCreateCycle(AbstractNode child) {
        Optional<DirectoryNode> current = Optional.of(this);
        while (current.isPresent()) {
            if (current.get() == child) {
                return true;
            }
            current = current.get().getParent();
        }
        return false;
    }
}
