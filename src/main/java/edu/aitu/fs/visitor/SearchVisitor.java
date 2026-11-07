package edu.aitu.fs.visitor;

import edu.aitu.fs.DirectoryNode;
import edu.aitu.fs.FileNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public final class SearchVisitor extends TraversingVisitor {
    private final Predicate<String> nameCriterion;
    private final long minSizeInBytes;
    private final List<String> matches = new ArrayList<>();

    public SearchVisitor(Predicate<String> nameCriterion, long minSizeInBytes) {
        if (nameCriterion == null) {
            throw new IllegalArgumentException("Name criterion must not be null");
        }
        if (minSizeInBytes < 0) {
            throw new IllegalArgumentException("Minimum size must not be negative");
        }
        this.nameCriterion = nameCriterion;
        this.minSizeInBytes = minSizeInBytes;
    }

    public static SearchVisitor nameEquals(String exactName, long minSizeInBytes) {
        return new SearchVisitor(name -> name.equals(exactName), minSizeInBytes);
    }

    public static SearchVisitor nameContains(String fragment, long minSizeInBytes) {
        return new SearchVisitor(name -> name.contains(fragment), minSizeInBytes);
    }

    public static SearchVisitor nameEndsWith(String suffix, long minSizeInBytes) {
        return new SearchVisitor(name -> name.endsWith(suffix), minSizeInBytes);
    }

    public static SearchVisitor anyName(long minSizeInBytes) {
        return new SearchVisitor(name -> true, minSizeInBytes);
    }

    public List<String> getMatchingPaths() {
        List<String> sorted = new ArrayList<>(matches);
        Collections.sort(sorted);
        return Collections.unmodifiableList(sorted);
    }

    @Override
    protected void resetState() {
        matches.clear();
    }

    @Override
    protected void onFile(FileNode file, String relativePath, int depth) {
        if (isMatch(file)) {
            matches.add(relativePath);
        }
    }

    @Override
    protected void onDirectory(DirectoryNode directory, int depth) {
    }

    private boolean isMatch(FileNode file) {
        return nameCriterion.test(file.getName()) && file.getSizeInBytes() >= minSizeInBytes;
    }
}
