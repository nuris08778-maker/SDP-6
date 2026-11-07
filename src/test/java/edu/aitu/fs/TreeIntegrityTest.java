package edu.aitu.fs;

import edu.aitu.fs.visitor.SearchVisitor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TreeIntegrityTest {

    @Test
    void negativeFileSizeIsRejected() {
        assertThrows(TreeIntegrityException.class, () -> new FileNode("bad", -1));
    }

    @Test
    void invalidNamesAreRejected() {
        assertThrows(TreeIntegrityException.class, () -> new FileNode("", 1));
        assertThrows(TreeIntegrityException.class, () -> new FileNode("a/b", 1));
        assertThrows(TreeIntegrityException.class, () -> new DirectoryNode(null));
        assertThrows(TreeIntegrityException.class, () -> new DirectoryNode(".."));
    }

    @Test
    void duplicateSiblingNameIsRejectedEvenAcrossElementTypes() {
        DirectoryNode dir = new DirectoryNode("d");
        dir.add(new FileNode("same", 1));
        assertThrows(TreeIntegrityException.class, () -> dir.add(new FileNode("same", 2)));
        assertThrows(TreeIntegrityException.class, () -> dir.add(new DirectoryNode("same")));
        assertEquals(1, dir.getChildren().size());
    }

    @Test
    void sameNameInDifferentDirectoriesIsAllowed() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode a = root.add(new DirectoryNode("a"));
        DirectoryNode b = root.add(new DirectoryNode("b"));
        a.add(new FileNode("same", 1));
        b.add(new FileNode("same", 1));
        assertEquals(1, a.getChildren().size());
        assertEquals(1, b.getChildren().size());
    }

    @Test
    void secondParentIsRejected() {
        DirectoryNode first = new DirectoryNode("first");
        DirectoryNode second = new DirectoryNode("second");
        FileNode file = first.add(new FileNode("f", 1));
        assertThrows(TreeIntegrityException.class, () -> second.add(file));
        assertTrue(second.isEmpty());
        assertSame(first, file.getParent().orElseThrow());
    }

    @Test
    void directoryCannotBeAddedToItself() {
        DirectoryNode dir = new DirectoryNode("d");
        assertThrows(TreeIntegrityException.class, () -> dir.add(dir));
    }

    @Test
    void cycleThroughAncestorIsRejected() {
        DirectoryNode root = new DirectoryNode("root");
        DirectoryNode child = root.add(new DirectoryNode("child"));
        DirectoryNode grandChild = child.add(new DirectoryNode("grandChild"));
        // root has no parent, so only the cycle rule can stop this
        assertThrows(TreeIntegrityException.class, () -> grandChild.add(root));
        assertTrue(root.getParent().isEmpty());
    }

    @Test
    void nullChildIsRejected() {
        assertThrows(TreeIntegrityException.class, () -> new DirectoryNode("d").add(null));
    }

    @Test
    void validDeepNestingIsSupported() {
        DirectoryNode current = new DirectoryNode("root");
        DirectoryNode root = current;
        for (int i = 0; i < 50; i++) {
            current = current.add(new DirectoryNode("level" + i));
        }
        current.add(new FileNode("leaf", 7));
        SearchVisitor visitor = SearchVisitor.nameEquals("leaf", 0);
        root.accept(visitor);
        assertEquals(1, visitor.getMatchingPaths().size());
    }

    @Test
    void removedChildCanBeMovedToAnotherDirectory() {
        DirectoryNode from = new DirectoryNode("from");
        DirectoryNode to = new DirectoryNode("to");
        from.add(new FileNode("f", 1));
        AbstractNode moved = from.remove("f").orElseThrow();
        assertTrue(moved.getParent().isEmpty());
        to.add(moved);
        assertTrue(from.isEmpty());
        assertEquals(List.of("f"), to.getChildren().stream().map(Element::getName).toList());
    }

    @Test
    void childListIsReadOnly() {
        DirectoryNode dir = new DirectoryNode("d");
        dir.add(new FileNode("f", 1));
        assertThrows(UnsupportedOperationException.class,
                () -> dir.getChildren().add(new FileNode("g", 1)));
        assertThrows(UnsupportedOperationException.class, () -> dir.getChildren().clear());
    }

    @Test
    void failedAddLeavesTreeUnchanged() {
        DirectoryNode dir = new DirectoryNode("d");
        FileNode original = dir.add(new FileNode("f", 1));
        assertThrows(TreeIntegrityException.class, () -> dir.add(new FileNode("f", 2)));
        assertSame(original, dir.getChildren().get(0));
    }
}
