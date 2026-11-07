package edu.aitu.fs;

import edu.aitu.fs.visitor.SearchVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SearchVisitorTest {
    @Test
    void combinesNameAndSizeConditions() {
        SearchVisitor visitor = SearchVisitor.nameEndsWith(".java", 1600);
        TestTrees.sampleProject().accept(visitor);

        assertEquals(List.of("src/App.java"), visitor.getMatchingPaths());
    }

    @Test
    void thresholdIsInclusive() {
        SearchVisitor visitor = SearchVisitor.nameEndsWith(".java", 1500);
        TestTrees.sampleProject().accept(visitor);
        assertEquals(List.of("src/App.java", "src/util/Helper.java"), visitor.getMatchingPaths());
    }

    @Test
    void pathsAreRootRelativeSlashSeparatedAndSorted() {
        SearchVisitor visitor = SearchVisitor.anyName(0);
        TestTrees.sampleProject().accept(visitor);
        assertEquals(List.of(
                "README.md",
                "src/App.java",
                "src/App.md",
                "src/util/Helper.java",
                "src/util/deep/Notes.md",
                "target.log"), visitor.getMatchingPaths());
    }

    @Test
    void resultOrderDoesNotDependOnInsertionOrder() {
        DirectoryNode a = new DirectoryNode("r");
        a.add(new FileNode("z.txt", 1));
        a.add(new FileNode("a.txt", 1));
        DirectoryNode b = new DirectoryNode("r");
        b.add(new FileNode("a.txt", 1));
        b.add(new FileNode("z.txt", 1));

        SearchVisitor first = SearchVisitor.anyName(0);
        SearchVisitor second = SearchVisitor.anyName(0);
        a.accept(first);
        b.accept(second);
        assertEquals(first.getMatchingPaths(), second.getMatchingPaths());
        assertEquals(List.of("a.txt", "z.txt"), first.getMatchingPaths());
    }

    @Test
    void noMatchGivesEmptyList() {
        SearchVisitor visitor = SearchVisitor.nameEquals("missing.txt", 0);
        TestTrees.sampleProject().accept(visitor);
        assertTrue(visitor.getMatchingPaths().isEmpty());
    }

    @Test
    void emptyRootDirectoryGivesEmptyList() {
        SearchVisitor visitor = SearchVisitor.anyName(0);
        new DirectoryNode("empty").accept(visitor);
        assertTrue(visitor.getMatchingPaths().isEmpty());
    }

    @Test
    void fileAsRootIsReportedByItsOwnName() {
        SearchVisitor visitor = SearchVisitor.nameEquals("solo.txt", 5);
        new FileNode("solo.txt", 5).accept(visitor);
        assertEquals(List.of("solo.txt"), visitor.getMatchingPaths());
    }

    @Test
    void customPredicateCanBeConfigured() {
        SearchVisitor visitor = new SearchVisitor(name -> name.startsWith("App"), 0);
        TestTrees.sampleProject().accept(visitor);
        assertEquals(List.of("src/App.java", "src/App.md"), visitor.getMatchingPaths());
    }

    @Test
    void invalidConfigurationIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> SearchVisitor.anyName(-1));
        assertThrows(IllegalArgumentException.class, () -> new SearchVisitor(null, 0));
    }
}
