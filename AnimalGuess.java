import java.io.*;
import java.util.Scanner;

/**
 * Animal guessing game that uses a decision tree to guess animals
 * Learns from user input and saves knowledge between sessions
 */
public class AnimalGuess {
    private DecisionTree gameBase;
    private Scanner scanner;
    private String filename;

    /**
     * Creates game with specified knowledge file
     * @param filename the file to load/save decision tree
     */
    public AnimalGuess(String filename) {
        this.scanner = new Scanner(System.in);
        this.filename = filename;
        loadGameBase();
    }

    /*
     * Loads decision tree from file or creates default tree if file doesn't exist
     */
    private void loadGameBase() {
        try { //load file
            System.out.println("Loading decision tree from: " + filename);
            this.gameBase = DecisionTree.readFile(filename);
            System.out.println("Tree loaded successfully.");
            System.out.println();
        } catch (IOException e) { //file doesn't exist or issue in reading
            System.out.println("Couldn't load tree from file. Using default tree.");
            startDefaultGameBase();
            System.out.println();
        }
    }

    /**
     * Initializes game with a simple default tree
     */
    private void startDefaultGameBase() {
        DecisionTree mouse = new DecisionTree("Mouse");
        this.gameBase = mouse; //setting root in the tree to be Mouse
    }
    
    /**
     * Saves current decision tree to file
     */
    private void saveGameBase() {
        try {
            gameBase.writeToFile(filename);
            System.out.println("Game knowledge saved to: " + filename);
        } catch (IOException e) {
            System.out.println("Error in saving knowledge to file: " + e.getMessage());
        }
    }

    /**
     * Reads line of user input and catches errors
     * @return trimmed user input
     */
    private String readUserInput() {
        try {
            return scanner.nextLine().trim();
        } catch (Exception e) {
            return "Error in reading input. Please try again.";
        }
    }

    /**
     * Asks user for yes/no answer and checks if input is valid
     * @param prompt question to ask user
     * @return true for yes or false for no
     */
    private boolean getYesOrNoAnswer(String prompt) {
        while (true) {
            System.out.println(prompt);
            String answer = readUserInput().toLowerCase();

            System.out.println("received answer: " + answer);

            if (answer.equals("yes") || answer.equals("y")) {
                return true;
            } else if (answer.equals("no") || answer.equals("n")) {
                return false;
            } else {
                System.out.println("Please answer 'yes' or 'no'."); //returns to top of loop if misspelled/different input is given
            }
        }
    }

    /**
     * Plays one complete round of animal guessing game
     */
    public void playOneGame() {
        System.out.println("Think of an animal.");
        System.out.println("I'll try to guess it.");

        DecisionTree currentNode = gameBase;
        DecisionTree parentNode = null;
        boolean lastAnswerWasYes = false;

        //traverse through tree asking questions
        while (currentNode != null && !currentNode.isLeaf()) {
            String question = currentNode.getData();
            boolean response = getYesOrNoAnswer(question + " ");

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
            boolean isGuessCorrect = getYesOrNoAnswer("Is your animal a " + guess + "? ");

            if (isGuessCorrect) {
                System.out.println("Yay I guessed it!");
            } else {
                System.out.println("Oh no, I guessed wrong.");
                learnFromWrongGuess(currentNode, parentNode, lastAnswerWasYes);
            }
        }
    }

    /**
     * Learns from wrong guess by adding new question to tree
     * @param wrongNode node containing wrong guess
     * @param parentNode parent of wrong node
     * @param cameFromYesBranch whether wrongNode was left child
     */
    private void learnFromWrongGuess(DecisionTree wrongNode, DecisionTree parentNode, boolean cameFromYesBranch) {
        System.out.println("Please help me to learn.");
        System.out.println("What was your animal?");

        String userAnimal = readUserInput();
        String wrongGuess = wrongNode.getData();

        System.out.println("Type a yes or no question that would distinguish between a " + userAnimal + " and a " + wrongGuess + ": ");

        String newQuestion = readUserInput();

        boolean answerNewQuestion = getYesOrNoAnswer("Would you answer yes to this question for the " + userAnimal + "?");

        DecisionTree correctAnimalNode = new DecisionTree(userAnimal);
        DecisionTree oldAnimalNode = new DecisionTree(wrongGuess);
        DecisionTree newQuestionNode;

        if (answerNewQuestion) {
            newQuestionNode = new DecisionTree(newQuestion, correctAnimalNode, oldAnimalNode);
        } else {
            newQuestionNode = new DecisionTree(newQuestion, oldAnimalNode, correctAnimalNode);
        }

        if (parentNode == null) {
            gameBase = newQuestionNode; //newQuestionNode is now new root
        } else {
            if (cameFromYesBranch) {
                parentNode.setLeft(newQuestionNode);
            } else {
                parentNode.setRight(newQuestionNode);
            }
        }

        System.out.println("Thank you! I will remember this information next time.");

    }

    /**
     * Main method to run animal guessing game
     * @param args command line arguments (optional filename)
     */
    public static void main(String[] args) {
        //using command line argument if given, else use default filename (AnimalTree.txt)
        String filename = (args.length > 0) ? args[0] : "AnimalTree.txt";
        AnimalGuess game = new AnimalGuess(filename);

        boolean playAgain = true;

        while (playAgain) {
            game.playOneGame();
            playAgain = game.getYesOrNoAnswer("Play again?");
        }

        System.out.println("Thank you for playing!");
        game.saveGameBase();
        game.scanner.close();
    }

}