package edu.aitu.fs;

import edu.aitu.fs.visitor.SearchVisitor;
import edu.aitu.fs.visitor.StatisticsVisitor;
import edu.aitu.fs.visitor.TotalSizeVisitor;

/** Deterministic demonstration: fixed data, no input, same output on every run. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        DirectoryNode root = buildSampleTree();

        System.out.println("=== Tree ===");
        printTree(root, "");

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("\n=== TotalSizeVisitor ===");
        System.out.println("Total size: " + sizeVisitor.getTotalBytes() + " bytes");

        SearchVisitor searchVisitor = SearchVisitor.nameEndsWith(".java", 1000);
        root.accept(searchVisitor);
        System.out.println("\n=== SearchVisitor (name ends with .java, size >= 1000) ===");
        searchVisitor.getMatchingPaths().forEach(path -> System.out.println("  " + path));

        StatisticsVisitor statisticsVisitor = new StatisticsVisitor();
        root.accept(statisticsVisitor);
        System.out.println("\n=== StatisticsVisitor ===");
        System.out.println("Files: " + statisticsVisitor.getFileCount());
        System.out.println("Directories: " + statisticsVisitor.getDirectoryCount());
        System.out.println("Max depth: " + statisticsVisitor.getMaxDepth());

        root.accept(sizeVisitor);
        System.out.println("\n=== Reuse check: same visitor, second run ===");
        System.out.println("Total size again: " + sizeVisitor.getTotalBytes() + " bytes (no accumulation)");

        System.out.println("\n=== Invalid mutation ===");
        try {
            root.add(new FileNode("README.md", 1));
        } catch (TreeIntegrityException e) {
            System.out.println("Rejected: " + e.getMessage());
        }
    }

    private static DirectoryNode buildSampleTree() {
        DirectoryNode root = new DirectoryNode("project");
        root.add(new FileNode("README.md", 1200));
        root.add(new FileNode("pom.xml", 2400));
        root.add(new DirectoryNode("docs"));
        DirectoryNode src = root.add(new DirectoryNode("src"));
        src.add(new FileNode("Main.java", 3100));
        src.add(new FileNode("Util.java", 800));
        DirectoryNode model = src.add(new DirectoryNode("model"));
        model.add(new FileNode("User.java", 1500));
        model.add(new DirectoryNode("generated")).add(new FileNode("Schema.java", 9000));
        return root;
    }

    private static void printTree(DirectoryNode directory, String indent) {
        System.out.println(indent + directory.getName() + "/");
        for (Element child : directory.getChildren()) {
            child.accept(new TreePrinter(indent + "  "));
        }
    }

    /** A fourth, ad-hoc visitor used only for printing; it needs no change to the elements. */
    private static final class TreePrinter implements Visitor {
        private final String indent;

        TreePrinter(String indent) {
            this.indent = indent;
        }

        @Override
        public void visit(FileNode file) {
            System.out.println(indent + file.getName() + " (" + file.getSizeInBytes() + ")");
        }

        @Override
        public void visit(DirectoryNode directory) {
            printTree(directory, indent);
        }
    }
}
