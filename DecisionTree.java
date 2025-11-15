import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class DecisionTree extends BinaryTree<String> {
    
    public DecisionTree(String data) {
        super(data);
    }

    public DecisionTree(String data, BinaryTree<String> left, BinaryTree<String> right) {
        super(data);
        this.setLeft(left); //checks that left is type DecisionTree
        this.setRight(right); //checks that right is type DecisionTree
    }

    public DecisionTree(DecisionTree tree) {
        super(tree.getData());
        this.setLeft(tree.getLeft());
        this.setRight(tree.getRight());
    }

    public DecisionTree getLeft() {
        return (DecisionTree)super.getLeft();
    }

    public DecisionTree getRight() {
        return (DecisionTree)super.getRight();
    }

    public void setLeft(BinaryTree<String> left) {
        if (left == null || left instanceof DecisionTree) {
            super.setLeft(left);
        } else {
            throw new UnsupportedOperationException("Left child needs to be a DecisionTree or null");
        }
    }

    public void setRight(BinaryTree<String> right) {
        if (right == null || right instanceof DecisionTree) {
            super.setRight(right);
        } else {
            throw new UnsupportedOperationException("Left child needs to be a DecisionTree or null");
        }
    }

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

    public static DecisionTree readFile(String filename) throws IOException{
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        Map<String, DecisionTree> nodeMap = new HashMap<>();
        
        String line;

        while((line = reader.readLine()) != null) {
            line = line.trim(); //removes any leading and trailing space from string
            if (line.isEmpty()) continue;

            int firstSpace = line.indexOf(' ');
            if (firstSpace == -1) {
                reader.close();
                throw new IOException("Invalid file format: " + line);
            }

            String path = line.substring(0, firstSpace);
            String nodeData = line.substring(firstSpace + 1).trim();

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
        System.out.println(root.getData());
        System.out.println(root.getLeft().getData());
        System.out.println(root.getRight().getData());
        System.out.println(root.getLeft().getRight().getData());

        System.out.println();

        //testing followPath() on sample decision tree
        System.out.println("Data from path 'YY': " + root.followPath("YY").getData()); //telling the directions or path you want to take through the tree from the root
        System.out.println("Data from path 'YN': " + root.followPath("YN").getData());
        System.out.println("Data from path 'NY': " + root.followPath("NY").getData());
        
        //System.out.println();

        //Test cases that throw exceptions
        //System.out.println("Data from path 'NN': " + root.followPath("NN").getData());
        //System.out.println("Data from path 'NZ': " + root.followPath("NZ").getData());

        //test file I/O methods
        System.out.println("--------Testing File I/O--------");
        String testFileName = "AnimalTree.txt";

        try {
            root.writeToFile(testFileName);

            DecisionTree loadedTree = DecisionTree.readFile(testFileName);

            
        }        

    }
}
