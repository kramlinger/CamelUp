import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class GreedyPlayer extends AIPlayer {

    public GreedyPlayer(int playerID, String name) {
        super(playerID, name);
        this.playerType = "Greedy";
    }

    @Override
    public void takeTurn() {
        int move = getGreedyMove();
        makeMove(move);
    }

    private int getGreedyMove() {

        // Now get the expectation of all moves. Choose the one with the largest expectation.
        float[] expectation = new float[16];
        expectation[0] = 1;

        // For all subsequent ones, compute the expected Payoff
        CamelMoveGenerator.calculateProbabilities(game.getCamels());
        System.arraycopy(lapWinnerExpectation(), 0, expectation, 1, 5);
        System.arraycopy(raceWinnerExpectation(), 0, expectation, 6, 5);
        System.arraycopy(raceLoserExpectation(), 0, expectation, 11, 5);

        System.out.println(Arrays.toString(expectation));

        // Choose the best index
        int move = -1;
        float maxValue = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < expectation.length; i++) {
            if (isLegal(i) && expectation[i] > maxValue) {
                maxValue = expectation[i];
                move = i;
            }
        }
        return move;
    }

    private float[] lapWinnerExpectation() {

        Camel[] camels = game.getCamels();

        float[] exp = new float[camels.length];
        for (int i = 0; i < camels.length; i++) {
            Camel c = camels[i];
            int payoff = game.getHighestValueCard(c).value();
            float prob = c.getLapProbability();
            exp[i] = payoff * prob + (1 - prob) * (-1);

            System.out.printf("%s: %f ,", c.getName(), prob);
        }

        return exp;
    }

    private float[] raceWinnerExpectation() {

        Camel[] camels = game.getCamels();

        float[] exp = new float[camels.length];
        for (int i = 0; i < camels.length; i++) {
            Camel c = camels[i];

            List<RaceCards> winnerCamelCards = game.getWinnerCamelCards();
            int playedCards =  winnerCamelCards.size();
            float prob = c.getRaceProbability();

            if (playedCards == 0) {
                exp[i] = 8 * prob + (1 - prob) * (-1);
            }
            if (playedCards == 1) {
                exp[i] = 5 * prob + (1 - prob) * (-1);
            }
            if (playedCards == 2) {
                exp[i] = 3 * prob + (1 - prob) * (-1);
            }
            if (playedCards == 3) {
                exp[i] = 2 * prob + (1 - prob) * (-1);
            }
            if (playedCards >= 4) {
                exp[i] = 1 * prob + (1 - prob) * (-1);
            }
        }

        return exp;
    }

    private float[] raceLoserExpectation() {
        Camel[] camels = game.getCamels();

        float[] exp = new float[camels.length];
        for (int i = 0; i < camels.length; i++) {
            exp[i] = 0;
        }

        return exp;
    }


}
