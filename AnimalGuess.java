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
        //DecisionTree crocodile = new DecisionTree("Crocodile");
        //DecisionTree root = new DecisionTree("Is it a mammal?", mouse, crocodile);
        this.gameBase = mouse; //setting root/start position in the tree to be at Mouse
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
            System.out.println(prompt);
            String answer = readUserInput().toLowerCase();

            if (answer.equals("yes") || answer.equals("y")) {
                return true;
            } else if (answer.equals("no") || answer.equals("n")) {
                return false;
            } else {
                System.out.println("Please answer yes or no."); //returns to top of loop if misspelled/different input is given
            }
        }
    }

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

    public static void main(String[] args) {
        AnimalGuess game = new AnimalGuess();

        boolean playAgain = true;

        while (playAgain) {
            game.playOneGame();
            playAgain = game.getYesOrNoAnswer("Play again?");
        }

        System.out.println("Thank you for playing!");
        game.scanner.close();
    }

}