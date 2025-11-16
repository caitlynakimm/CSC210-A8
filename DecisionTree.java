import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * A binary decision tree for animal guessing game
 * Extends BinaryTree to store questions and animal names
 * Allows for tree traversal, navigation via node paths, and file I/O
 */
public class DecisionTree extends BinaryTree<String> {
    
    /**
     * Creates a leaf node with given data
     * @param data animal name or question for this node
     */
    public DecisionTree(String data) {
        super(data);
    }

    /**
     * Creates branch node with left and right children
     * @param data question for this node
     * @param left left child of node
     * @param right right child of node
     */
    public DecisionTree(String data, BinaryTree<String> left, BinaryTree<String> right) {
        super(data);
        this.setLeft(left); //checks that left is type DecisionTree
        this.setRight(right); //checks that right is type DecisionTree
    }

    /**
     * Copy constructor makes deep copy of tree
     * @param tree tree to copy
     */
    public DecisionTree(DecisionTree tree) {
        super(tree.getData());
        this.setLeft(tree.getLeft());
        this.setRight(tree.getRight());
    }

    /**
     * Gets left child as a DecisionTree
     * @return left child node
     */
    public DecisionTree getLeft() {
        return (DecisionTree)super.getLeft();
    }

    /**
     * Gets right child as a DecisionTree
     * @return right child node
     */
    public DecisionTree getRight() {
        return (DecisionTree)super.getRight();
    }

    /**
     * Sets left child and checks type
     * @param left node to set as left child
     * @throws UnsupportedOperationException if left isn't DecisionTree type
     */
    public void setLeft(BinaryTree<String> left) {
        if (left == null || left instanceof DecisionTree) {
            super.setLeft(left);
        } else {
            throw new UnsupportedOperationException("Left child needs to be a DecisionTree or null");
        }
    }

    /**
     * Sets right child and checks type
     * @param right node to set as right child
     * @throws UnsupportedOperationException if right isn't DecisionTree type
     */
    public void setRight(BinaryTree<String> right) {
        if (right == null || right instanceof DecisionTree) {
            super.setRight(right);
        } else {
            throw new UnsupportedOperationException("Left child needs to be a DecisionTree or null");
        }
    }

    /**
     * Navigates through tree following path consisting of Y/N directions
     * @param path string of 'Y' (yes/left) and 'N' (no/right) characters
     * @return node at end of path
     * @throws IllegalArgumentException for invalid paths
     */
    public DecisionTree followPath(String path) {
        DecisionTree current = this;

        for (int i = 0; i < path.length(); i++) {
            char direction = path.charAt(i);

            if (direction == 'Y') {
                current = current.getLeft();
            } else if (direction == 'N') {
                current = current.getRight();
            } else {
                throw new IllegalArgumentException("Path must only include 'Y' or 'N' characters.");
            }

            if (current == null) { //if new node you are at is null, throw exception
                throw new IllegalArgumentException("Path leads to a null node.");
            }
        }

        return current;
    }

    /**
     * Writes decision tree to a file in breadth-first order
     * Each line has a path and node data separated by a space
     * @param filename file to write to
     * @throws IOException if file writing fails
     */
    public void writeToFile(String filename) throws IOException{
        PrintWriter out = new PrintWriter(new FileWriter(filename));

        //two parallel queues - one for nodes and one for their paths
        Queue<DecisionTree> nodeQueue = new LinkedList<>();
        Queue<String> pathQueue = new LinkedList<>();

        //beginning with root and its empty path
        nodeQueue.add(this);
        pathQueue.add("");

        while(!nodeQueue.isEmpty()) {
            DecisionTree currentNode = nodeQueue.poll(); //returns and removes first element of queue
            String currentPath = pathQueue.poll();

            out.println(currentPath + " " + currentNode.getData());

            //if adding left child, add "Y" to path
            if (currentNode.getLeft() != null) {
                nodeQueue.add(currentNode.getLeft());
                pathQueue.add(currentPath + "Y");
            }
            
            //if adding right child, add "N" to path
            if (currentNode.getRight() != null) {
                nodeQueue.add(currentNode.getRight());
                pathQueue.add(currentPath + "N");
            }

        }
        out.close();
    }

    /**
     * Reads a decision tree from a file and reconstructs tree structure
     * @param filename file to read from
     * @return root node of reconstructed tree
     * @throws IOException if file reading fails or format is invalid
     */
    public static DecisionTree readFile(String filename) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Map<String, DecisionTree> nodeMap = new HashMap<>();
        
        String line;

        while((line = reader.readLine()) != null) {
            if (line.isEmpty()) continue;

            String path;
            String nodeData;

            if (line.startsWith(" ")) {
                path = ""; //root has empty path
                nodeData = line.substring(1).trim(); //remove any leading space from string
            } else {
                int firstSpace = line.indexOf(' ');

                if (firstSpace == -1) {
                    reader.close();
                    throw new IOException("Invalid file format: " + line);
                }

                path = line.substring(0, firstSpace);
                nodeData = line.substring(firstSpace + 1).trim();

            }
    

            DecisionTree newNode = new DecisionTree(nodeData);
            nodeMap.put(path, newNode);

            //if new node isn't the root (doesn't have parent), find parent, and 
            //determine if node is left or right child and insert it
            if (!path.isEmpty()) {
                String parentPath = path.substring(0, path.length()-1);
                char lastChar = path.charAt(path.length()-1);

                DecisionTree parentNode = nodeMap.get(parentPath);

                if (parentNode == null) {
                    reader.close();
                    throw new IOException("Parent node not found for path:" + parentPath);
                }

                if (lastChar == 'Y') {
                    parentNode.setLeft(newNode);
                } else if (lastChar == 'N') {
                    parentNode.setRight(newNode);
                } else {
                    reader.close();
                    throw new IOException("Last character of child's path is invalid: " + path);
                }
            }
        }

        reader.close();
        return nodeMap.get(""); //return root
    } 

    /**
     * Main method that tests DecisionTree operations and file I/O
     * @param args command line arguments
     */
    public static void main(String[] args) {
        //sample decision tree with six nodes
        DecisionTree dog = new DecisionTree("Dog");
        DecisionTree platypus = new DecisionTree("Platypus");
        DecisionTree lizard = new DecisionTree("Lizard");

        DecisionTree mammalQuestionTwo = new DecisionTree("Does it give milk?", dog, platypus);
        DecisionTree reptileQuestionOne = new DecisionTree("Is it a reptile?", lizard, null);
        
        DecisionTree root = new DecisionTree("Is it a mammal?", mammalQuestionTwo, reptileQuestionOne);
        
        System.out.println();

        //traversing and accessing individual nodes' data
        System.out.println("Accessing nodes from tree:");
        System.out.println(root.getData());
        System.out.println(root.getLeft().getData());
        System.out.println(root.getRight().getData());
        System.out.println(root.getLeft().getRight().getData());

        System.out.println();

        //testing followPath() on sample decision tree
        System.out.println("Testing followPath() on tree:");
        System.out.println("Path 'Y': " + root.followPath("Y").getData());
        System.out.println("Path 'N': " + root.followPath("N").getData());
        System.out.println("Path 'YY': " + root.followPath("YY").getData()); //telling the directions or path you want to take through the tree from the root
        System.out.println("Path 'YN': " + root.followPath("YN").getData());
        System.out.println("Path 'NY': " + root.followPath("NY").getData());
        
        System.out.println();

        //Test cases that throw exceptions
        //System.out.println("Data from path 'NN': " + root.followPath("NN").getData());
        //System.out.println("Data from path 'NZ': " + root.followPath("NZ").getData());

        //test file I/O methods
        System.out.println("--------Testing File I/O--------");
        String testFileName = "AnimalTree.txt";

        try {
            //write tree via the root to file
            System.out.println("Writing tree to file...");
            root.writeToFile(testFileName);
            System.out.println("Write was successful.");
            System.out.println();

            //read tree back from file
            System.out.println("Reading tree from file...");
            DecisionTree loadedTree = DecisionTree.readFile(testFileName);
            System.out.println("Read was successful.");
            System.out.println();

            //check if loaded tree's main structure is the same
            System.out.println("Loaded tree verification:");
            System.out.println("Root: " + loadedTree.getData());
            System.out.println("Left parent: " + loadedTree.getLeft().getData());
            System.out.println("Right parent: " + loadedTree.getRight().getData());
            System.out.println("Left parent's left child: " + loadedTree.getLeft().getLeft().getData());
            System.out.println("Left parent's right child: " + loadedTree.getLeft().getRight().getData());
            System.out.println("Right parent's left child: " + loadedTree.getRight().getLeft().getData());
            System.out.println();

            //test followPath() on loaded tree
            System.out.println("Testing followPath() on loaded tree:");
            System.out.println("Path 'Y': " + loadedTree.followPath("Y").getData());
            System.out.println("Path 'N': " + loadedTree.followPath("N").getData());
            System.out.println("Path 'YY': " + loadedTree.followPath("YY").getData());
            System.out.println("Path 'YN': " + loadedTree.followPath("YN").getData());
            System.out.println("Path 'NY': " + loadedTree.followPath("NY").getData());
            System.out.println();

            System.out.println("File I/O test successfully completed!");
        } catch (Exception e) {
            System.out.println();
            System.out.println("Error in file I/O test: " + e.getMessage());
        }
    }
}
