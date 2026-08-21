// CapitalsQuiz.cs
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text.Json;
using System.Text.Json.Serialization;

class Stats
{
    [JsonPropertyName("correct")]
    public int Correct { get; set; }
    [JsonPropertyName("incorrect")]
    public int Incorrect { get; set; }
    [JsonPropertyName("total")]
    public int Total { get; set; }
}

class CapitalsQuiz
{
    private static readonly Dictionary<string, string> Countries = new Dictionary<string, string>
    {
        {"France", "Paris"}, {"Spain", "Madrid"}, {"Italy", "Rome"},
        {"Germany", "Berlin"}, {"United Kingdom", "London"},
        {"Portugal", "Lisbon"}, {"Netherlands", "Amsterdam"},
        {"Belgium", "Brussels"}, {"Switzerland", "Bern"},
        {"Austria", "Vienna"}, {"Greece", "Athens"},
        {"Turkey", "Ankara"}, {"Russia", "Moscow"},
        {"Ukraine", "Kyiv"}, {"Poland", "Warsaw"},
        {"Sweden", "Stockholm"}, {"Norway", "Oslo"},
        {"Denmark", "Copenhagen"}, {"Finland", "Helsinki"},
        {"Ireland", "Dublin"}, {"USA", "Washington"},
        {"Canada", "Ottawa"}, {"Mexico", "Mexico City"},
        {"Brazil", "Brasilia"}, {"Argentina", "Buenos Aires"},
        {"Chile", "Santiago"}, {"Peru", "Lima"},
        {"Colombia", "Bogota"}, {"Venezuela", "Caracas"},
        {"Australia", "Canberra"}, {"New Zealand", "Wellington"},
        {"China", "Beijing"}, {"Japan", "Tokyo"},
        {"South Korea", "Seoul"}, {"India", "New Delhi"},
        {"Egypt", "Cairo"}, {"South Africa", "Pretoria"},
        {"Nigeria", "Abuja"}, {"Kenya", "Nairobi"}
    };

    private const string StatsFile = "capitals_stats.json";
    private static readonly JsonSerializerOptions Options = new JsonSerializerOptions { WriteIndented = true };

    private Stats stats = new Stats();

    public CapitalsQuiz() => LoadStats();

    private void LoadStats()
    {
        if (!File.Exists(StatsFile)) return;
        string json = File.ReadAllText(StatsFile);
        stats = JsonSerializer.Deserialize<Stats>(json) ?? new Stats();
    }

    private void SaveStats()
    {
        string json = JsonSerializer.Serialize(stats, Options);
        File.WriteAllText(StatsFile, json);
    }

    private string GetHint(string capital, int level)
    {
        switch (level)
        {
            case 1: return $"First letter is '{capital[0]}'";
            case 2: return $"Has {capital.Length} letters";
            case 3:
                var wrong = Countries.Values.Where(c => c != capital).Take(2).ToList();
                var options = wrong.Append(capital).OrderBy(x => Guid.NewGuid()).ToList();
                return $"Choose one: {string.Join(", ", options)}";
            default: return "";
        }
    }

    public void RunQuiz(int rounds)
    {
        Console.WriteLine("\n🌍 Capitals Quiz");
        Console.WriteLine($"Round 1/{rounds}\n");

        var countryList = Countries.Keys.ToList();
        countryList = countryList.OrderBy(x => Guid.NewGuid()).ToList();
        var selected = countryList.Take(rounds).ToList();

        int correct = 0, incorrect = 0;

        for (int i = 0; i < selected.Count; i++)
        {
            string country = selected[i];
            string capital = Countries[country];
            int hintLevel = 0;
            Console.WriteLine($"Country: {country}");
            while (true)
            {
                Console.Write("Your answer (or 'hint', 'skip', 'quit'): ");
                string input = Console.ReadLine()?.Trim().ToLower() ?? "";
                switch (input)
                {
                    case "quit":
                        Console.WriteLine("Quitting...");
                        return;
                    case "skip":
                        Console.WriteLine($"Skipped. The capital is {capital}");
                        incorrect++;
                        break;
                    case "hint":
                        hintLevel++;
                        if (hintLevel > 3)
                            Console.WriteLine("No more hints available.");
                        else
                            Console.WriteLine($"💡 Hint: {GetHint(capital, hintLevel)}");
                        continue;
                    default:
                        if (input == capital.ToLower())
                        {
                            Console.WriteLine("✅ Correct!");
                            correct++;
                            break;
                        }
                        else
                        {
                            Console.WriteLine("❌ Wrong. Try again or type 'skip'.");
                            continue;
                        }
                }
                break;
            }
            Console.WriteLine($"Score: {correct}/{i+1} ({correct/(double)(i+1)*100:F1}%)\n");
        }

        stats.Correct += correct;
        stats.Incorrect += incorrect;
        stats.Total += correct + incorrect;
        SaveStats();
        Console.WriteLine($"Quiz finished! Correct: {correct}, Incorrect: {incorrect}");
    }

    public void ShowStats()
    {
        Console.WriteLine("\n📊 Statistics");
        Console.WriteLine($"Correct: {stats.Correct}");
        Console.WriteLine($"Incorrect: {stats.Incorrect}");
        Console.WriteLine($"Total: {stats.Total}");
        if (stats.Total > 0)
        {
            double pct = (double)stats.Correct / stats.Total * 100;
            Console.WriteLine($"Accuracy: {pct:F1}%");
        }
    }

    static void Main(string[] args)
    {
        if (args.Length < 1)
        {
            Console.WriteLine("Usage: dotnet run -- [start|stats|help]");
            return;
        }
        var quiz = new CapitalsQuiz();
        string cmd = args[0].ToLower();
        switch (cmd)
        {
            case "start":
                quiz.RunQuiz(10);
                break;
            case "stats":
                quiz.ShowStats();
                break;
            case "help":
                Console.WriteLine("Commands: start, stats, help");
                break;
            default:
                Console.WriteLine("Unknown command");
                break;
        }
    }
}
