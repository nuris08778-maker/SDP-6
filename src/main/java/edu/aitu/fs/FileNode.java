package edu.aitu.fs;

/** Leaf element: a file with a name and an immutable, non-negative size in bytes. */
public final class FileNode extends AbstractNode {

    private final long sizeInBytes;

    public FileNode(String name, long sizeInBytes) {
        super(name);
        if (sizeInBytes < 0) {
            throw new TreeIntegrityException("File size must not be negative: " + sizeInBytes);
        }
        this.sizeInBytes = sizeInBytes;
    }

    public long getSizeInBytes() {
        return sizeInBytes;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}
