package edu.aitu.fs;

import edu.aitu.fs.visitor.SearchVisitor;
import edu.aitu.fs.visitor.TotalSizeVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VisitorStateTest {

    @Test
    void totalSizeIsNotMultipliedByRepeatedRuns() {
        DirectoryNode root = TestTrees.sampleProject();
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        root.accept(visitor);
        root.accept(visitor);
        assertEquals(3960, visitor.getTotalBytes());
    }

    @Test
    void searchResultsAreNotDuplicatedByRepeatedRuns() {
        DirectoryNode root = TestTrees.sampleProject();
        SearchVisitor visitor = SearchVisitor.nameEndsWith(".md", 0);
        root.accept(visitor);
        root.accept(visitor);
        assertEquals(List.of("README.md", "src/App.md", "src/util/deep/Notes.md"),
                visitor.getMatchingPaths());
    }

    @Test
    void secondRunOnDifferentRootDoesNotLeakFirstRunResults() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        TestTrees.sampleProject().accept(visitor);
        new FileNode("small", 5).accept(visitor);
        assertEquals(5, visitor.getTotalBytes());
    }

    @Test
    void resultReflectsTreeChangesBetweenRuns() {
        DirectoryNode root = new DirectoryNode("r");
        root.add(new FileNode("a", 10));
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);
        assertEquals(10, visitor.getTotalBytes());
        root.add(new FileNode("b", 5));
        root.accept(visitor);
        assertEquals(15, visitor.getTotalBytes());
    }

    @Test
    void visitorInstancesAreIndependentOfRunOrder() {
        DirectoryNode root = TestTrees.sampleProject();
        TotalSizeVisitor size = new TotalSizeVisitor();
        SearchVisitor search = SearchVisitor.anyName(0);
        root.accept(search);
        root.accept(size);
        root.accept(search);
        assertEquals(3960, size.getTotalBytes());
        assertEquals(6, search.getMatchingPaths().size());
    }
}
