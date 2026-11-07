package edu.aitu.fs.visitor;

import edu.aitu.fs.DirectoryNode;
import edu.aitu.fs.FileNode;

public final class TotalSizeVisitor extends TraversingVisitor {
    private long totalBytes;

    public long getTotalBytes() {
        return totalBytes;
    }

    @Override
    protected void resetState() {
        totalBytes = 0;
    }

    @Override
    protected void onFile(FileNode file, String relativePath, int depth) {
        totalBytes += file.getSizeInBytes();
    }

    @Override
    protected void onDirectory(DirectoryNode directory, int depth) {
    }
}
