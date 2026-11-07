package edu.aitu.fs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Composite element: a directory holding uniquely named children.
 * Children are kept sorted by name, so every traversal order is deterministic.
 * All invariants are enforced here; the child collection is never exposed as writable.
 */
public final class DirectoryNode extends AbstractNode {

    private final Map<String, AbstractNode> children = new TreeMap<>();

    public DirectoryNode(String name) {
        super(name);
    }

    /**
     * Adds a child. Rejected (and nothing changes) if the child is null, already has a
     * parent, has the same name as a sibling, or would create a cycle.
     *
     * @return the added child, to allow fluent construction
     */
    public <T extends AbstractNode> T add(T child) {
        requireAddable(child);
        children.put(child.getName(), child);
        child.attachTo(this);
        return child;
    }

    /** Detaches and returns the named child so that it can be re-attached elsewhere. */
    public Optional<AbstractNode> remove(String childName) {
        AbstractNode removed = children.remove(childName);
        if (removed != null) {
            removed.detach();
        }
        return Optional.ofNullable(removed);
    }

    /** Read-only snapshot of the children, sorted by name. */
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

    /** A cycle appears only if the new child is this directory or one of its ancestors. */
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
