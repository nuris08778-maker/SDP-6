package edu.aitu.fs.visitor;

import edu.aitu.fs.DirectoryNode;
import edu.aitu.fs.Element;
import edu.aitu.fs.FileNode;
import edu.aitu.fs.Visitor;

import java.util.ArrayDeque;
import java.util.Deque;

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
