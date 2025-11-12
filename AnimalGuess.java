import java.util.Scanner;

public class AnimalGuess {
    private DecisionTree gameBase;
    private Scanner scanner;

    public AnimalGuess() {
        this.scanner = new Scanner(System.in);
        startGameBase();
    }

    private void startGameBase() {
        DecisionTree mouse = new DecisionTree("Mouse");
        DecisionTree crocodile = new DecisionTree("Crocodile");
        DecisionTree root = new DecisionTree("Is it a mammal?", mouse, crocodile);
        this.gameBase = root; //setting start position in the tree to be at the root
    }

    private String readUserInput() {
        try {
            return scanner.nextLine().trim();
        } catch (Exception e) {
            return "Error in reading input. Please try again.";
        }
    }

    private boolean getYesOrNoAnswer(String prompt) {
        while (true) {
            System.out.println(prompt + " (yes or no): ");
            String answer = readUserInput().toLowerCase();

            if (answer.equals("yes") || answer.equals("y")) {
                return true;
            } else if (answer.equals("no") || answer.equals("n")) {
                return false;
            } else {
                System.out.println("Please answer yes or no."); //returns to top of loop if nonsensical/misspelled input is given
            }
        }
    }

    public void playOneGame() {
        System.out.println("Think of an animal.");
        System.out.println("I'll try to guess it.");
        System.out.println();

        DecisionTree currentNode = gameBase;
        DecisionTree parentNode = null;
        boolean lastAnswerWasYes = false;
        StringBuilder pathBuilder = new StringBuilder();

        while (currentNode != null && !currentNode.isLeaf()) {
            String question = currentNode.getData();

            String currentPath = pathBuilder.toString(); //saves path through tree as a string
            if (currentPath.isEmpty()) {
                System.out.println(question);
            } else {
                System.out.println(currentPath + " " + question);
            }

            boolean response = getYesOrNoAnswer("");

            pathBuilder.append(response ? 'Y' : 'N'); //if boolean returned is true add 'Y' to pathBuilder or else if false add 'N'

            parentNode = currentNode;
            if (response) {
                currentNode = currentNode.getLeft(); //move to parent node's left child
                lastAnswerWasYes = true;
            } else {
                currentNode = currentNode.getRight(); //move to parent node's right child
                lastAnswerWasYes = false;
            }
        }

        // reached a leaf
        if (currentNode != null) {
            String guess = currentNode.getData();
            String currentPath = pathBuilder.toString();

            System.out.println(currentPath + " " + guess);

            boolean isGuessCorrect = getYesOrNoAnswer("");

            if (isGuessCorrect) {
                System.out.println("Yay I guessed right!");
            } else {
                System.out.println("Oh no, I guessed wrong.");
                //method to learn from wrong guess
            }
        }
    }


}