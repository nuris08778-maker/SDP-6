package edu.aitu.fs;

/** Fixtures shared by the tests. */
final class TestTrees {

    private TestTrees() {
    }

    /**
     * project (root)
     * ├── README.md            100
     * ├── empty/               (empty directory)
     * ├── src/
     * │   ├── App.java        2000
     * │   ├── App.md           300
     * │   └── util/
     * │       ├── Helper.java 1500
     * │       └── deep/
     * │           └── Notes.md 50
     * └── target.log           10
     */
    static DirectoryNode sampleProject() {
        DirectoryNode root = new DirectoryNode("project");
        root.add(new FileNode("README.md", 100));
        root.add(new DirectoryNode("empty"));
        root.add(new FileNode("target.log", 10));
        DirectoryNode src = root.add(new DirectoryNode("src"));
        src.add(new FileNode("App.java", 2000));
        src.add(new FileNode("App.md", 300));
        DirectoryNode util = src.add(new DirectoryNode("util"));
        util.add(new FileNode("Helper.java", 1500));
        DirectoryNode deep = util.add(new DirectoryNode("deep"));
        deep.add(new FileNode("Notes.md", 50));
        return root;
    }
}
