import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

public class NNPlayer extends AIPlayer {

    public NNPlayer(int playerID, String name) {
        super(playerID, name);
        this.playerType = "Neural Network";
    }

    @Override
    public void takeTurn() {
        int move = getNNMove();
        if (game.getHeadless()) {
            Writer.writeGameState(game, move);
        }
        makeMove(move);
    }

    public float[] getNNProbabilities() {
        // Build input array as JSON
        int[] gameStateNumeric = game.getGameState().getGameState();
        String jsonPayload = "{\"gameState\": [" + Arrays.stream(gameStateNumeric)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(",")) + "]}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:5050/predict"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            String output = response.body();  // ✅ THIS IS YOUR RESPONSE
            System.out.println("Raw response from Python server: " + output);

            // Parse JSON
            JSONObject obj = new JSONObject(output);
            JSONArray result = obj.getJSONArray("result");

            float[] probabilities = new float[result.length()];
            for (int i = 0; i < result.length(); i++) {
                probabilities[i] = result.getFloat(i);
            }

            return probabilities;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get response from Python server", e);
        }
    }

    public int getNNMove() {

        float[] probability = getNNProbabilities();

        // Choose the best index
        int move = -1;
        float maxValue = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < probability.length; i++) {
            if (isLegal(i) && probability[i] > maxValue) {
                maxValue = probability[i];
                move = i;
            }
        }
        return move;
    }


}