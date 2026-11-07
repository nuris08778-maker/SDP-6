package edu.aitu.fs.visitor;

import edu.aitu.fs.DirectoryNode;
import edu.aitu.fs.Element;
import edu.aitu.fs.FileNode;
import edu.aitu.fs.Visitor;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Traversal owner of this project: the visitors walk the tree themselves.
 *
 * <p>The element's {@code accept} only dispatches; this base class descends into the
 * children of a directory (sorted by name) by calling {@code child.accept(this)}.
 * Every reachable node is therefore visited exactly once per run, in pre-order,
 * with no type checks: directories and files simply get different {@code visit} overloads.
 *
 * <p><b>Run lifecycle.</b> A run starts when {@code accept(visitor)} is called on a node
 * while no other node of this visitor is being processed. At that moment {@link #resetState()}
 * is called, so a visitor instance is <b>reusable</b>: results of an earlier run never leak
 * into the next one, and results do not depend on run order. Results stay readable after
 * the run until the next run starts. Instances must not be shared between threads.
 *
 * <p><b>Paths and depth.</b> The root has depth 0, its direct children depth 1, and so on.
 * The relative path of a node is the names of the directories below the root plus the node's
 * own name, joined with '/'. The root's own name is never part of a path (if the root itself
 * is a file, its path is just its name).
 */
public abstract class TraversingVisitor implements Visitor {

    private static final String SEPARATOR = "/";

    private final Deque<String> namesBelowRoot = new ArrayDeque<>();
    private int activeNodes = 0;

    @Override
    public final void visit(FileNode file) {
        enter(file);
        try {
            onFile(file, relativePathOf(file), currentDepth());
        } finally {
            leave();
        }
    }

    @Override
    public final void visit(DirectoryNode directory) {
        enter(directory);
        try {
            onDirectory(directory, currentDepth());
            for (Element child : directory.getChildren()) {
                child.accept(this);
            }
        } finally {
            leave();
        }
    }

    /** Clears everything accumulated by a previous run. Called once at the start of each run. */
    protected abstract void resetState();

    protected abstract void onFile(FileNode file, String relativePath, int depth);

    protected abstract void onDirectory(DirectoryNode directory, int depth);

    private void enter(Element element) {
        if (activeNodes == 0) {
            namesBelowRoot.clear();
            resetState();
        } else {
            namesBelowRoot.addLast(element.getName());
        }
        activeNodes++;
    }

    private void leave() {
        activeNodes--;
        if (activeNodes > 0) {
            namesBelowRoot.removeLast();
        }
    }

    private int currentDepth() {
        return activeNodes - 1;
    }

    private String relativePathOf(Element element) {
        return namesBelowRoot.isEmpty() ? element.getName() : String.join(SEPARATOR, namesBelowRoot);
    }
}
