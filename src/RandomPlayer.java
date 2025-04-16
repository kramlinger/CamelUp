import java.util.*;
import java.util.stream.*;

public class RandomPlayer extends AIPlayer {

    public RandomPlayer(int playerID, String name) {
        super(playerID, name);
    }

    @Override
    public void takeTurn() {
        int move = drawCandidateMove();
        makeMove(move);
    }

    private int drawCandidateMove() {

        // First, select all legal moves, ...
        List<Integer> legalMoves = new ArrayList<>();

        for (int i = 0; i < 16; i++) {
            if (isLegal(i, game)) {
                legalMoves.add(i);
            }
        }

        // Sanity check
        if (legalMoves.isEmpty()) {
            throw new IllegalStateException("No legal moves available.");
        }

        // Now draw amongst the legal moves.
        Random random = new Random();
        return legalMoves.get(random.nextInt(legalMoves.size()));
    }



}
