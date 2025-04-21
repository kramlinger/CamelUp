import java.util.*;

public class CamelMoveGenerator {

    public record CamelMoveOption(Camel camel, int stepSize, int moveOrder) {}

    public static List<CamelMoveOption[]> camelMoveOptions(Camel[] camels) {
        // Filter out camels that have already moved
        List<Camel> availableCamels = new ArrayList<>();
        for (Camel camel : camels) {
            if (!camel.getHasMoved()) {
                availableCamels.add(camel);
            }
        }

        List<Integer> moveOrderList = new ArrayList<>();
        for (int i = 1; i <= availableCamels.size(); i++) {
            moveOrderList.add(i);
        }

        // Generate all possible configurations
        List<CamelMoveOption[]> allConfigurations = new ArrayList<>();
        List<List<Integer>> allMoveOrderPermutations = new ArrayList<>();
        permute(moveOrderList, 0, allMoveOrderPermutations);

        // allMoveOrderPermutations has factorial(n) entries.

        int totalConfigs = 0;

        // Generate step size combinations
        for (List<Integer> moveOrder : allMoveOrderPermutations) {
            generateStepSizeCombinations(availableCamels.size(), new int[availableCamels.size()], 0, moveOrder, availableCamels, allConfigurations);
            totalConfigs += Math.pow(3, availableCamels.size());  // 3^n step configs per moveOrder
        }

/*
        // For now, printing the result
        for (CamelMoveOption[] config : allConfigurations) {

            System.out.println(Arrays.toString(config));
        }

 */

        //System.out.printf("Total configurations: %d for %d Camels%n",  totalConfigs, availableCamels.size());

        //System.out.println("Total configurations: " + totalConfigs);
        //System.out.println("Total configurations: " + allConfigurations.size());
        return allConfigurations;
    }

    // Generate permutations using backtracking
    private static void permute(List<Integer> arr, int index, List<List<Integer>> result) {
        if (index == arr.size()) {
            result.add(new ArrayList<>(arr));
            return;
        }

        for (int i = index; i < arr.size(); i++) {
            Collections.swap(arr, i, index);
            permute(arr, index + 1, result);
            Collections.swap(arr, i, index); // backtrack
        }
    }

    // Generate all step size combinations recursively (3^n total)
    private static void generateStepSizeCombinations(int length, int[] combo, int index, List<Integer> moveOrder, List<Camel> availableCamels, List<CamelMoveOption[]> allConfigurations) {
        if (index == length) {
            CamelMoveOption[] options = new CamelMoveOption[length];
            for (int i = 0; i < length; i++) {
                options[i] = new CamelMoveOption(availableCamels.get(i), combo[i], moveOrder.get(i));
            }
            allConfigurations.add(options);
            return;
        }

        for (int step = 1; step <= 3; step++) {
            combo[index] = step;
            generateStepSizeCombinations(length, combo, index + 1, moveOrder, availableCamels, allConfigurations);
        }
    }

    private record CamelCard(Camel camel, boolean raceWinner, boolean raceLoser){};

    public static CamelCard getFirstCamel(Camel[] camels, CamelMoveOption[] configuration) {

        CamelCard firstCamelCard = new  CamelCard(camels[0], false, false);

        Camel[] copyCamels = new Camel[camels.length];
        for (int i = 0; i < camels.length; i++) {
            copyCamels[i] = camels[i].clone();
        }


        for (int i = 0; i < camels.length; i++) {
            for (CamelMoveOption cmo : configuration) {
                if (cmo.moveOrder() == i) {
                    for (Camel c : copyCamels) {
                        if (c.getName() == cmo.camel().getName()) {
                            c.advanceCamel(copyCamels, cmo.stepSize());
                        }
                    }
                }
            }
        }

        for (Camel c : copyCamels) {
            if (c.camelRanking(copyCamels) == 1) {
                if (Game.hasCamelFinished(c)) {
                    firstCamelCard = new CamelCard(c, true, false);
                } else {
                    firstCamelCard = new CamelCard(c, false, false);
                }
            }
        }

        return firstCamelCard;
    }

    public static CamelCard[] getFirstCamels(Camel[] camels, List<CamelMoveOption[]> configurations) {

        CamelCard[] firstCamels = new CamelCard[configurations.size()];

        int i = 0;
        for (CamelMoveOption[] config : configurations) {
            firstCamels[i++] = getFirstCamel(camels, config);
        }

        return firstCamels;
    }


    public static void calculateProbabilities (Camel[] camels) {

        // get combs
        List<CamelMoveOption[]> configurations = camelMoveOptions(camels);
        CamelCard[] firstCamels = getFirstCamels(camels, configurations);


        //System.out.printf("%d %n", firstCamels.length);

        for (Camel camel : camels) {
            int lapCount = 0;
            int raceCount = 0;
            for (CamelCard card : firstCamels) {
                if (card.camel().getName() == camel.getName()) { // might be non-identical items, so check by name!
                    lapCount++;
                }
                if (card.raceWinner()) {raceCount++; }
            }
            float lapProb = (float) lapCount / firstCamels.length;
            float raceProb = (float) raceCount / firstCamels.length;

            //System.out.printf("%f ", lapProb);

            camel.setLapProbability(lapProb);
            camel.setRaceProbability(raceProb);
        }

    }




}
