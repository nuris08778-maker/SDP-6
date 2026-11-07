package edu.aitu.fs;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DoubleDispatchTest {

    /** Does NOT traverse: it only records which overload each element selected. */
    private static final class RecordingVisitor implements Visitor {
        final List<String> calls = new ArrayList<>();

        @Override
        public void visit(FileNode file) {
            calls.add("file:" + file.getName());
        }

        @Override
        public void visit(DirectoryNode directory) {
            calls.add("dir:" + directory.getName());
        }
    }

    @Test
    void fileReachesFileOverload() {
        RecordingVisitor visitor = new RecordingVisitor();
        new FileNode("a.txt", 1).accept(visitor);
        assertEquals(List.of("file:a.txt"), visitor.calls);
    }

    @Test
    void directoryReachesDirectoryOverload() {
        RecordingVisitor visitor = new RecordingVisitor();
        DirectoryNode dir = new DirectoryNode("docs");
        dir.add(new FileNode("a.txt", 1));
        dir.accept(visitor);
        // accept dispatches exactly once; descending is the visitor's job, not the element's
        assertEquals(List.of("dir:docs"), visitor.calls);
    }

    @Test
    void dispatchWorksThroughElementReference() {
        RecordingVisitor visitor = new RecordingVisitor();
        Element asFile = new FileNode("x", 0);
        Element asDirectory = new DirectoryNode("y");
        asFile.accept(visitor);
        asDirectory.accept(visitor);
        assertEquals(List.of("file:x", "dir:y"), visitor.calls);
    }
}
