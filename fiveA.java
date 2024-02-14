// This implementation adapts the provided Ant Colony Optimization algorithm to solve the Traveling Salesman 
// Problem. It initializes pheromone and distance matrices, constructs ant paths, updates pheromone trails, and
//  finds the shortest path through iterative iterations.
//  Finally, it demonstrates example usage by finding the shortest path for a given distance matrix.

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class fiveA {
    private double[][] pheromoneMatrix;
    private int[][] distanceMatrix;
    private int numberOfCities;
    private int numberOfAnts;
    private double alpha;
    private double beta;
    private double evaporationRate;
    private double initialPheromone;
    private int startingCity;

    public fiveA(int numberOfCities, int numberOfAnts, double alpha, double beta,
                        double evaporationRate, double initialPheromone, int startingCity) {
        this.numberOfCities = numberOfCities;
        this.numberOfAnts = numberOfAnts;
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.initialPheromone = initialPheromone;
        this.startingCity = startingCity;

        // Initialize pheromone matrix and distance matrix
        pheromoneMatrix = new double[numberOfCities][numberOfCities];
        distanceMatrix = new int[numberOfCities][numberOfCities];
    }

    public void initializePheromoneMatrix() {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                pheromoneMatrix[i][j] = initialPheromone;
            }
        }
    }

    public void initializeDistanceMatrix(int[][] distanceMatrix) {
        this.distanceMatrix = distanceMatrix;
    }

    public List<Integer> findShortestPath() {
        Random random = new Random();
        List<Integer> bestPath = null;
        int bestDistance = Integer.MAX_VALUE;

        for (int iteration = 0; iteration < numberOfAnts; iteration++) {
            List<Integer> antPath = constructAntPath(random);
            int antDistance = calculatePathDistance(antPath);

            if (antDistance < bestDistance) {
                bestDistance = antDistance;
                bestPath = antPath;
            }

            updatePheromoneTrail(antPath, antDistance);
        }

        return bestPath;
    }

    private List<Integer> constructAntPath(Random random) {
        List<Integer> antPath = new ArrayList<>();
        boolean[] visitedCities = new boolean[numberOfCities];
        int currentCity = startingCity;
        antPath.add(currentCity);
        visitedCities[currentCity] = true;

        while (antPath.size() < numberOfCities) {
            int nextCity = selectNextCity(currentCity, visitedCities, random);
            antPath.add(nextCity);
            visitedCities[nextCity] = true;
            currentCity = nextCity;
        }

        antPath.add(startingCity); // Return to the starting city to complete the cycle
        return antPath;
    }

    private int selectNextCity(int currentCity, boolean[] visitedCities, Random random) {
        double[] probabilities = new double[numberOfCities];
        double probabilitiesSum = 0.0;

        for (int city = 0; city < numberOfCities; city++) {
            if (!visitedCities[city]) {
                double pheromoneLevel = Math.pow(pheromoneMatrix[currentCity][city], alpha);
                double distance = 1.0 / Math.pow(distanceMatrix[currentCity][city], beta);
                probabilities[city] = pheromoneLevel * distance;
                probabilitiesSum += probabilities[city];
            }
        }

        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;

        for (int city = 0; city < numberOfCities; city++) {
            if (!visitedCities[city]) {
                double probability = probabilities[city] / probabilitiesSum;
                cumulativeProbability += probability;
                if (randomValue <= cumulativeProbability) {
                    return city;
                }
            }
        }

        return -1; // Unreachable code, should never happen
    }

    private int calculatePathDistance(List<Integer> path) {
        int distance = 0;
        for (int i = 0; i < numberOfCities - 1; i++) {
            int currentCity = path.get(i);
            int nextCity = path.get(i + 1);
            distance += distanceMatrix[currentCity][nextCity];
        }
        distance += distanceMatrix[path.get(numberOfCities - 1)][startingCity]; // Return to the starting city
        return distance;
    }

    private void updatePheromoneTrail(List<Integer> path, int distance) {
        double pheromoneDeposit = 1.0 / distance;

        for (int i = 0; i < numberOfCities - 1; i++) {
            int currentCity = path.get(i);
            int nextCity = path.get(i + 1);
            pheromoneMatrix[currentCity][nextCity] = (1 - evaporationRate) *
                    pheromoneMatrix[currentCity][nextCity] + evaporationRate * pheromoneDeposit;
        }
    }

    public static void main(String[] args) {
        // Example usage
        int[][] distanceMatrix = {
                {0, 2, 9, 10},
                {1, 0, 6, 4},
                {15, 7, 0, 8},
                {6, 3, 12, 0}
        };
        int numberOfCities = 4;
        int numberOfAnts = 10;
        int startingCity = 0;
        double alpha = 1.0;
        double beta = 2.0;
        double evaporationRate = 0.5;
        double initialPheromone = 0.1;

        fiveA antColony = new fiveA(numberOfCities, numberOfAnts, alpha, beta,
                evaporationRate, initialPheromone, startingCity);
        antColony.initializePheromoneMatrix();
        antColony.initializeDistanceMatrix(distanceMatrix);
        List<Integer> shortestPath = antColony.findShortestPath();
        System.out.println("Shortest path: " + shortestPath);
    }
}
// output -Shortest path: [0, 1, 2, 3, 0]