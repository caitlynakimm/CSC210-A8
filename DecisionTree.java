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

    }
}
