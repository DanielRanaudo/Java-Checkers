# Java Checkers Game
A classic checkers game built in Java using Swing Library as my final project for my second-level introductory programming course (CIS 1200). The project demonstrates object-oriented programming using a GUI as well as game state management.

# Features
* Classic Checkers Gameplay: Implements standard checkers rules, including chain-jumps, promotions, and limitations on moves when jumps are available
* Undo Functionality: Allows the player to undo their move, implemented by tracking changing game states
* Save and Load Functionality: Allows the player to save their game and load it back up when they'd like to continue playing

# Code Overview
The project was designed with a clear separation between frontend and backend logic.
* Board: The core of the backend. Manages the 2D array representing the checkerboard, enforces game rules, handles piece movement, and tracks game history.
* Piece: Represents a single piece object, holding all necessary states. Includes funcitons to be able to draw itself
* Board History: A helper class meant to store the Board state, which allows for undo functionality
* Game & CheckersGame: These manage the frontend. They render the game board (including various functional buttons) and handles user input.

# Design Challenges and Reflection
The most challenging part of this project was separating the backend logic from the frontend. Initially, my game logic was intertwined with the mouse listener. This made it impossible to test my backend functions independently.

**What I Learned:** I refractored my code, creating a clear boundary, which allowed me to test the mechanics of the game efficiently. It showed me the importance of modular design.

# How to Run: 
1. Clone the Repository
2. Compile the source files: javac src/*.java
3. Run the main application: java src/CheckersGame

**Note** 
Please ensure the JUnit 4 library is installed and added to the project's dependencies.
