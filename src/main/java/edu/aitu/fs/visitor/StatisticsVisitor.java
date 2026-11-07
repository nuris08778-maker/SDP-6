package edu.aitu.fs.visitor;

import edu.aitu.fs.DirectoryNode;
import edu.aitu.fs.FileNode;

public final class StatisticsVisitor extends TraversingVisitor {
    private int fileCount;
    private int directoryCount;
    private int maxDepth;

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    @Override
    protected void resetState() {
        fileCount = 0;
        directoryCount = 0;
        maxDepth = 0;
    }

    @Override
    protected void onFile(FileNode file, String relativePath, int depth) {
        fileCount++;
        maxDepth = Math.max(maxDepth, depth);
    }

    @Override
    protected void onDirectory(DirectoryNode directory, int depth) {
        directoryCount++;
        maxDepth = Math.max(maxDepth, depth);
    }
}
