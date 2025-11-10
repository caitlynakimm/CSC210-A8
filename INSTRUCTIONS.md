# Assignment 8: Decision Trees
​
*This assignment explores an application of binary decision trees. For some background on how decision trees can be used to classify items into categories, I recommend [this chapter](https://www-users.cse.umn.edu/~kumar001/dmbook/ch4.pdf), up through and including section 4.3.1. (Sections 4.3.2 and beyond talk about how to build a decision tree from a preexisting set of data, which is not what we plan to do in this assignment.)*

Decision trees organize information in a hierarchical structure. In class we have focused on hierarchical problems that can be approached with binary search, but other problems can also benefit from the structure provided by binary trees. Specifically, you can use binary trees to make a decision tree that will narrow down options until you reach a single one.

In this week's assignment, we will use binary decision trees to play a simple ["20 Questions"](https://en.wikipedia.org/wiki/Twenty_questions) style guessing game with Java. The program will try to guess what animal the user is thinking of. It will ask the user yes-or-no questions to narrow down options until it arrives at a single candidate.

How does it know what to ask? The user will teach it! Every time the program gets an answer wrong, it will ask the user to tell it what question would have distinguished the wrong anser from the right answer. It will then add this question to the binary decision tree for use in future games.

Here is an example session, with the user's responses indicated with ">":

    $ java AnimalGuess
    Think of an animal.
    I'll try to guess it.
    Is your animal a Mouse?
    > no
    I got it wrong.
    Please help me to learn.
    What was your animal?
    > Crocodile
    Type a yes or no question that would distinguish between a Crocodile and a Mouse
    > Is it a mammal?
    Would you answer yes to this question for the Crocodile?
    > no
    Play again?
    yes
    Think of an animal.
    I'll try to guess it.
    Is it a mammal?
    > no
    Is your animal a Crocodile?
    > yes
    I guessed it!
    Play again?
    > no
    $


## Description of the Goal

You will implement a `DecisionTree` class as a subclass of `BinaryTree<String>`. Your program will try to guess the animal by:
1. asking the question stored on the current node in the decision tree, then
2. following the appropriate **branch** for the answer given by the user (yes = left, no = right)

It will repeat 1 and 2 until it reaches a **leaf** of the tree, which does not contain a question. Instead, it will contain the name of a single animal. The program will return the animal as its guess. 

You will start off with a very simple tree. As the user plays the game, they will help create a more complex tree that is better at guessing.  Speficially, when the system guesses wrong, it should ask the user to provide a question that distinguishes the wrong guess from the correct answer. This question will be used to extend the decision tree. 

Your program should be able to read and write the decision tree from a text file so that questions added during one session can be saved for later.

## Implementation

Organize your program into two classes:
- `DecisionTree`: a data class used to represent the knowledge used by the program. It is a subclass of `BinaryTree<String>` (as described in this week's slides).
- `AnimalGuess`: contains the static methods (including `main`) that will run the game. 

We suggest designing `DecisionTree` to implement a reusable decision tree-- exclude anything specialized to this particular application. Elements that are specific to guessing animals should be stored in `AnimalGuess`.

### Phase 1:  `DecisionTree`

The first step is to write the `DecisionTree` class, where you build a subclass of the `BinaryTree` class. Most of the work on this class will be writing revised constructors, accessors, and manipulators as discussed in class.  Look through the `BinaryTree` class and identify the methods that you will need to override.

Before moving onward, it is a good idea to spend some time becoming accustomed to working with a tree.  Write some test code that builds a sample decision tree with at least six nodes.  (You can use the example below for inspiration.)  Practice accessing the data at individual nodes via the root.  For example, what node is accessed by the expression `tree.getLeft().getRight().getData()`?  Move on to the next steps only when you feel reasonably comfortable with navigating through a tree.

You should also write and test a function called `followPath` that accepts a `String` like *YNNYNYY* that it uses to trace a path through the tree from the root.  A *Y* means to follow the left child and a *N* means to follow the right child.  The method should return the node that is reached after all the characters in the input string have been followed.  Note that you can write this function either iteratively or recursively.  (You can even try it both ways for practice!)


### Phase 2:  `AnimalGuess`

Once the tree class is defined, the next step is to write the code that runs the guessing game:
- Write the code to play the game just once at first.  Later on you can add an outer loop in case the player wants to play again after the first game is finished.
- The basic structure of the code will be a loop, where each time through the loop represents a single question asked and answered.
- Beginning at the root, you need to keep track of the position in the tree as the player's responses dictate whether to move left or right. Before coding, thinks about how you can do this in a binary tree.

You may also wish to define some short utility methods in `AnimalGuess`. For example: 
- One might read a line of input with a `try`/`catch` block to handle possible exceptions or invalid responses. 
- Another might take a prompt and elicit a yes or no answer from the user, returning a boolean. This would have several applications in the program.  You should probably accept several forms of responses -- *yes*, *Yes*, *y* and *Y* for a positive answer, and the equivalent for a negative.
- Your program should also respond gracefully if the user enters something nonsensical, perhaps due to a typo.  Ambiguous answers should not be assumed to be 'yes' or 'no'.
- Your program should never stop running as a result of bad input; try to catch any exception that might occur.

## Phase 3:  Reading and Writing Trees

Your program should learn as it plays, but it's never going to amount to much if it forgets everything the next time you run it.  We'd like to have the program remember what it learned between sessions.  

To allow for this, your program should be able to read a decision tree in from a text file when it starts up, and write it out again at the end. The name of this file will be specified as a command line argument, but for consistency's sake you should name it `AnimalTree.txt`. 

The format of the file is one node per line, in breadth-first order for full credit. Each line contains the path string of the node, a space, and the node's value (either a question or an animal name). Here is a simple example:

     Is it a mammal?
    Y Does it have hooves?
    N Is it a reptile?
    YY Does it give milk?
    YN Is it a carnivore?
    NY Crocodile
    NN Mosquito
    YYY Cow
    YYN Horse
    YNY Is it in the dog family?
    YNN Mouse
    YNYY Dog
    YNYN Cat

(Note that the space at the front of the first line is not a typo.  It indicates that this is the data for the node with an empty path string -- the root!)

### Output

Start by writing the method to write the tree to a file.  Put it in class `DecisionTree`.  You can test this using the hard-coded tree you created for phase 1.  It should print the tree nodes line by line using breadth-first traversal.  In addition to the queue of nodes to be visited, you will also need a parallel queue holding their paths.  

We haven't written to files yet, but it's similar to how we've worked with files before.  You can create an output channel like so:

    PrintWriter out = new PrintWriter(new FileWriter(filename));

Then you can write to it using `out.println("Hello World")` -- or whatever message you want to send.  The syntax of printing is exactly the same as what you are used to with `System.out`.  When you are all done, call `out.close()` to finish writing the file.

### Input

The method to read in the tree will read a line at a time, split the line at the first space character to separate the path string from the node data (using `indexOf` and `substring`), and then use all but the final character of the path string to find the parent of the new node-to-be. The final character then determines whether the new node becomes the left child or the right.

Once you have both the reading and the writing, you will call the program as shown below:

    java AnimalGuess AnimalTree.txt
    
It will read the tree in to start.  When the user has finished playing the game, your program should write out the tree again using the same file name (overwriting the previous file contents).
