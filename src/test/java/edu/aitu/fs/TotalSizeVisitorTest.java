package edu.aitu.fs;

import edu.aitu.fs.visitor.TotalSizeVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TotalSizeVisitorTest {
    @Test
    void sumsFilesAcrossAllNestingLevels() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        TestTrees.sampleProject().accept(visitor);
        assertEquals(100 + 10 + 2000 + 300 + 1500 + 50, visitor.getTotalBytes());
    }

    @Test
    void subtreeSizeCountsOnlyThatSubtree() {
        DirectoryNode root = new DirectoryNode("root");
        root.add(new FileNode("outside.bin", 999));
        DirectoryNode src = root.add(new DirectoryNode("src"));
        src.add(new FileNode("a", 2000));
        DirectoryNode util = src.add(new DirectoryNode("util"));
        util.add(new FileNode("b", 1500));

        TotalSizeVisitor visitor = new TotalSizeVisitor();
        src.accept(visitor);
        assertEquals(3500, visitor.getTotalBytes());
    }

    @Test
    void emptyDirectoryHasZeroSize() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        new DirectoryNode("empty").accept(visitor);
        assertEquals(0, visitor.getTotalBytes());
    }

    @Test
    void singleFileRootCountsItsOwnSize() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        new FileNode("solo.bin", 42).accept(visitor);
        assertEquals(42, visitor.getTotalBytes());
    }

    @Test
    void zeroByteFilesAreAllowedAndContributeNothing() {
        DirectoryNode dir = new DirectoryNode("d");
        dir.add(new FileNode("zero", 0));
        dir.add(new FileNode("one", 1));
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        dir.accept(visitor);
        assertEquals(1, visitor.getTotalBytes());
    }
}
