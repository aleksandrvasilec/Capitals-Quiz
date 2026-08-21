// CapitalsQuiz.java
import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;

public class CapitalsQuiz {
    private static final Map<String, String> COUNTRIES = new LinkedHashMap<>();
    private static final String STATS_FILE = "capitals_stats.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static {
        COUNTRIES.put("France", "Paris");
        COUNTRIES.put("Spain", "Madrid");
        COUNTRIES.put("Italy", "Rome");
        COUNTRIES.put("Germany", "Berlin");
        COUNTRIES.put("United Kingdom", "London");
        COUNTRIES.put("Portugal", "Lisbon");
        COUNTRIES.put("Netherlands", "Amsterdam");
        COUNTRIES.put("Belgium", "Brussels");
        COUNTRIES.put("Switzerland", "Bern");
        COUNTRIES.put("Austria", "Vienna");
        COUNTRIES.put("Greece", "Athens");
        COUNTRIES.put("Turkey", "Ankara");
        COUNTRIES.put("Russia", "Moscow");
        COUNTRIES.put("Ukraine", "Kyiv");
        COUNTRIES.put("Poland", "Warsaw");
        COUNTRIES.put("Sweden", "Stockholm");
        COUNTRIES.put("Norway", "Oslo");
        COUNTRIES.put("Denmark", "Copenhagen");
        COUNTRIES.put("Finland", "Helsinki");
        COUNTRIES.put("Ireland", "Dublin");
        COUNTRIES.put("USA", "Washington");
        COUNTRIES.put("Canada", "Ottawa");
        COUNTRIES.put("Mexico", "Mexico City");
        COUNTRIES.put("Brazil", "Brasilia");
        COUNTRIES.put("Argentina", "Buenos Aires");
        COUNTRIES.put("Chile", "Santiago");
        COUNTRIES.put("Peru", "Lima");
        COUNTRIES.put("Colombia", "Bogota");
        COUNTRIES.put("Venezuela", "Caracas");
        COUNTRIES.put("Australia", "Canberra");
        COUNTRIES.put("New Zealand", "Wellington");
        COUNTRIES.put("China", "Beijing");
        COUNTRIES.put("Japan", "Tokyo");
        COUNTRIES.put("South Korea", "Seoul");
        COUNTRIES.put("India", "New Delhi");
        COUNTRIES.put("Egypt", "Cairo");
        COUNTRIES.put("South Africa", "Pretoria");
        COUNTRIES.put("Nigeria", "Abuja");
        COUNTRIES.put("Kenya", "Nairobi");
    }

    static class Stats {
        int correct = 0;
        int incorrect = 0;
        int total = 0;
    }

    private Stats stats = new Stats();

    public CapitalsQuiz() {
        loadStats();
    }

    private void loadStats() {
        try {
            Path path = Paths.get(STATS_FILE);
            if (Files.exists(path)) {
                String json = new String(Files.readAllBytes(path));
                stats = gson.fromJson(json, Stats.class);
            }
        } catch (IOException e) {}
    }

    private void saveStats() {
        try {
            Files.write(Paths.get(STATS_FILE), gson.toJson(stats).getBytes());
        } catch (IOException e) {}
    }

    private String getHint(String capital, int level) {
        switch (level) {
            case 1:
                return "First letter is '" + capital.charAt(0) + "'";
            case 2:
                return "Has " + capital.length() + " letters";
            case 3:
                List<String> wrong = new ArrayList<>();
                for (String c : COUNTRIES.values()) {
                    if (!c.equals(capital) && wrong.size() < 2) {
                        wrong.add(c);
                    }
                }
                List<String> options = new ArrayList<>(wrong);
                options.add(capital);
                Collections.shuffle(options);
                return "Choose one: " + String.join(", ", options);
            default:
                return "";
        }
    }

    private String readLine() {
        try {
            return new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            return "";
        }
    }

    public void runQuiz(int rounds) {
        System.out.println("\n🌍 Capitals Quiz");
        System.out.println("Round 1/" + rounds + "\n");

        List<String> countryList = new ArrayList<>(COUNTRIES.keySet());
        Collections.shuffle(countryList);
        List<String> selected = countryList.subList(0, Math.min(rounds, countryList.size()));

        int correct = 0, incorrect = 0;

        for (int i = 0; i < selected.size(); i++) {
            String country = selected.get(i);
            String capital = COUNTRIES.get(country);
            int hintLevel = 0;
            System.out.println("Country: " + country);
            while (true) {
                System.out.print("Your answer (or 'hint', 'skip', 'quit'): ");
                String input = readLine().trim().toLowerCase();
                switch (input) {
                    case "quit":
                        System.out.println("Quitting...");
                        return;
                    case "skip":
                        System.out.println("Skipped. The capital is " + capital);
                        incorrect++;
                        break;
                    case "hint":
                        hintLevel++;
                        if (hintLevel > 3) {
                            System.out.println("No more hints available.");
                        } else {
                            System.out.println("💡 Hint: " + getHint(capital, hintLevel));
                        }
                        continue;
                    default:
                        if (input.equals(capital.toLowerCase())) {
                            System.out.println("✅ Correct!");
                            correct++;
                            break;
                        } else {
                            System.out.println("❌ Wrong. Try again or type 'skip'.");
                            continue;
                        }
                }
                break;
            }
            System.out.printf("Score: %d/%d (%.1f%%)\n\n", correct, i+1, (correct/(double)(i+1)*100));
        }

        stats.correct += correct;
        stats.incorrect += incorrect;
        stats.total += correct + incorrect;
        saveStats();
        System.out.printf("Quiz finished! Correct: %d, Incorrect: %d\n", correct, incorrect);
    }

    public void showStats() {
        System.out.println("\n📊 Statistics");
        System.out.println("Correct: " + stats.correct);
        System.out.println("Incorrect: " + stats.incorrect);
        System.out.println("Total: " + stats.total);
        if (stats.total > 0) {
            double pct = (double) stats.correct / stats.total * 100;
            System.out.printf("Accuracy: %.1f%%\n", pct);
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java CapitalsQuiz [start|stats|help]");
            return;
        }
        CapitalsQuiz quiz = new CapitalsQuiz();
        String cmd = args[0].toLowerCase();
        switch (cmd) {
            case "start":
                quiz.runQuiz(10);
                break;
            case "stats":
                quiz.showStats();
                break;
            case "help":
                System.out.println("Commands: start, stats, help");
                break;
            default:
                System.out.println("Unknown command");
        }
    }
}
