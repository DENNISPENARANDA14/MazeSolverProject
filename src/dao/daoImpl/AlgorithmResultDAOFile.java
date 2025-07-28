package dao.daoImpl;

import dao.AlgorithmResultDAO;
import models.AlgorithmResult;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AlgorithmResultDAOFile implements AlgorithmResultDAO {

    private static final String FILE_NAME = "results.csv";

    @Override
    public void saveResult(AlgorithmResult result) {
        List<AlgorithmResult> results = getAllResults();
        boolean updated = false;

        for (int i = 0; i < results.size(); i++) {
            AlgorithmResult r = results.get(i);
            if (r.getAlgorithmName().equals(result.getAlgorithmName()) && r.getMazeSize() == result.getMazeSize()) {
                results.set(i, result);
                updated = true;
                break;
            }
        }
        if (!updated) {
            results.add(result);
        }
        writeResultsToFile(results);
    }

    @Override
    public List<AlgorithmResult> getAllResults() {
        List<AlgorithmResult> results = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) {
            return results;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    AlgorithmResult result = parseCsvLine(line);
                    if (result != null) {
                        results.add(result);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading results: " + e.getMessage());
        }
        return results;
    }

    @Override
    public void clearAllResults() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            // Sobrescribe el archivo con vacío
        } catch (IOException e) {
            System.err.println("Error clearing results: " + e.getMessage());
        }
    }

    private void writeResultsToFile(List<AlgorithmResult> results) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (AlgorithmResult r : results) {
                bw.write(formatToCsv(r));
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving results: " + e.getMessage());
        }
    }

    private String formatToCsv(AlgorithmResult r) {
        return r.getAlgorithmName() + "," + r.getPathLength() + "," + r.getTimeTakenMillis() + "," + r.getMazeSize();
    }

    private AlgorithmResult parseCsvLine(String line) {
        String[] parts = line.split(",");
        if (parts.length != 4) return null;

        try {
            String name = parts[0];
            int pathLength = Integer.parseInt(parts[1]);
            long time = Long.parseLong(parts[2]);
            int size = Integer.parseInt(parts[3]);
            return new AlgorithmResult(name, pathLength, time, size);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing CSV line: " + line);
            return null;
        }
    }
}
