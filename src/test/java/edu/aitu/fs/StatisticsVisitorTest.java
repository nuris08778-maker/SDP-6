package edu.aitu.fs;

import edu.aitu.fs.visitor.StatisticsVisitor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatisticsVisitorTest {

    @Test
    void countsFilesDirectoriesAndDepthForNestedTree() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        TestTrees.sampleProject().accept(visitor);
        assertEquals(6, visitor.getFileCount());
        // project, empty, src, util, deep
        assertEquals(5, visitor.getDirectoryCount());
        // project(0) -> src(1) -> util(2) -> deep(3) -> Notes.md(4)
        assertEquals(4, visitor.getMaxDepth());
    }

    @Test
    void emptyRootDirectoryCountsItselfAtDepthZero() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        new DirectoryNode("empty").accept(visitor);
        assertEquals(0, visitor.getFileCount());
        assertEquals(1, visitor.getDirectoryCount());
        assertEquals(0, visitor.getMaxDepth());
    }

    @Test
    void fileRootHasDepthZeroAndNoDirectories() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        new FileNode("solo", 3).accept(visitor);
        assertEquals(1, visitor.getFileCount());
        assertEquals(0, visitor.getDirectoryCount());
        assertEquals(0, visitor.getMaxDepth());
    }

    @Test
    void emptyNestedDirectoriesAreCountedAndDeepenTheTree() {
        DirectoryNode root = new DirectoryNode("r");
        root.add(new DirectoryNode("a")).add(new DirectoryNode("b")).add(new DirectoryNode("c"));
        StatisticsVisitor visitor = new StatisticsVisitor();
        root.accept(visitor);
        assertEquals(0, visitor.getFileCount());
        assertEquals(4, visitor.getDirectoryCount());
        assertEquals(3, visitor.getMaxDepth());
    }

    @Test
    void repeatedRunsDoNotAccumulate() {
        DirectoryNode root = TestTrees.sampleProject();
        StatisticsVisitor visitor = new StatisticsVisitor();
        root.accept(visitor);
        root.accept(visitor);
        assertEquals(6, visitor.getFileCount());
        assertEquals(5, visitor.getDirectoryCount());
        assertEquals(4, visitor.getMaxDepth());
    }

    @Test
    void depthIsResetWhenASmallerTreeIsVisitedAfterADeepOne() {
        StatisticsVisitor visitor = new StatisticsVisitor();
        TestTrees.sampleProject().accept(visitor);
        DirectoryNode flat = new DirectoryNode("flat");
        flat.add(new FileNode("f", 1));
        flat.accept(visitor);
        assertEquals(1, visitor.getMaxDepth());
    }
}
