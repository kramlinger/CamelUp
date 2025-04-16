public class Main {

    public static void main(String[] args) {
        // Create a new Game instance
        Game game = new Game(5);

        // Create a new Board and pass the Game instance to it
        Board board = new Board(game);

        // Show the board (which initializes the window and shows the grid)
        board.showBoard();
    }
}
