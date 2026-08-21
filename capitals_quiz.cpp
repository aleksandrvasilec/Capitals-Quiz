// capitals_quiz.cpp
#include <iostream>
#include <map>
#include <vector>
#include <string>
#include <algorithm>
#include <random>
#include <fstream>
#include <sstream>
#include <nlohmann/json.hpp>

using namespace std;
using json = nlohmann::json;

const map<string, string> COUNTRIES = {
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

const string STATS_FILE = "capitals_stats.json";

struct Stats {
    int correct = 0;
    int incorrect = 0;
    int total = 0;
};

Stats loadStats() {
    ifstream f(STATS_FILE);
    if (!f.is_open()) return Stats();
    json j;
    f >> j;
    Stats s;
    s.correct = j["correct"];
    s.incorrect = j["incorrect"];
    s.total = j["total"];
    return s;
}

void saveStats(const Stats& s) {
    json j = {{"correct", s.correct}, {"incorrect", s.incorrect}, {"total", s.total}};
    ofstream f(STATS_FILE);
    f << setw(2) << j << endl;
}

string getHint(const string& capital, int level) {
    switch (level) {
        case 1: return "First letter is '" + string(1, capital[0]) + "'";
        case 2: return "Has " + to_string(capital.size()) + " letters";
        case 3: {
            vector<string> wrong;
            for (const auto& kv : COUNTRIES) {
                if (kv.second != capital && wrong.size() < 2)
                    wrong.push_back(kv.second);
            }
            vector<string> options = wrong;
            options.push_back(capital);
            random_device rd;
            mt19937 g(rd());
            shuffle(options.begin(), options.end(), g);
            string result = "Choose one: ";
            for (size_t i = 0; i < options.size(); i++) {
                if (i > 0) result += ", ";
                result += options[i];
            }
            return result;
        }
        default: return "";
    }
}

void runQuiz(int rounds) {
    Stats stats = loadStats();
    cout << "\n🌍 Capitals Quiz\n";
    cout << "Round 1/" << rounds << "\n\n";

    vector<string> countryList;
    for (const auto& kv : COUNTRIES) countryList.push_back(kv.first);
    random_device rd;
    mt19937 g(rd());
    shuffle(countryList.begin(), countryList.end(), g);
    if ((int)countryList.size() > rounds) countryList.resize(rounds);

    int correct = 0, incorrect = 0;

    for (size_t i = 0; i < countryList.size(); i++) {
        string country = countryList[i];
        string capital = COUNTRIES.at(country);
        int hintLevel = 0;
        cout << "Country: " << country << "\n";
        while (true) {
            cout << "Your answer (or 'hint', 'skip', 'quit'): ";
            string input;
            getline(cin, input);
            string cmd = input;
            transform(cmd.begin(), cmd.end(), cmd.begin(), ::tolower);

            if (cmd == "quit") {
                cout << "Quitting...\n";
                return;
            } else if (cmd == "skip") {
                cout << "Skipped. The capital is " << capital << "\n";
                incorrect++;
                break;
            } else if (cmd == "hint") {
                hintLevel++;
                if (hintLevel > 3) {
                    cout << "No more hints available.\n";
                } else {
                    cout << "💡 Hint: " << getHint(capital, hintLevel) << "\n";
                }
                continue;
            } else {
                string answer = input;
                transform(answer.begin(), answer.end(), answer.begin(), ::tolower);
                string capLower = capital;
                transform(capLower.begin(), capLower.end(), capLower.begin(), ::tolower);
                if (answer == capLower) {
                    cout << "✅ Correct!\n";
                    correct++;
                    break;
                } else {
                    cout << "❌ Wrong. Try again or type 'skip'.\n";
                }
            }
        }
        cout << "Score: " << correct << "/" << (i+1) << " (" << (correct/(double)(i+1)*100) << "%)\n\n";
    }

    stats.correct += correct;
    stats.incorrect += incorrect;
    stats.total += correct + incorrect;
    saveStats(stats);
    cout << "Quiz finished! Correct: " << correct << ", Incorrect: " << incorrect << "\n";
}

void showStats() {
    Stats stats = loadStats();
    cout << "\n📊 Statistics\n";
    cout << "Correct: " << stats.correct << "\n";
    cout << "Incorrect: " << stats.incorrect << "\n";
    cout << "Total: " << stats.total << "\n";
    if (stats.total > 0) {
        double pct = (double)stats.correct / stats.total * 100;
        cout << "Accuracy: " << pct << "%\n";
    }
}

int main(int argc, char* argv[]) {
    if (argc < 2) {
        cout << "Usage: capitals_quiz [start|stats|help]\n";
        return 1;
    }
    string cmd = argv[1];
    transform(cmd.begin(), cmd.end(), cmd.begin(), ::tolower);
    if (cmd == "start") {
        runQuiz(10);
    } else if (cmd == "stats") {
        showStats();
    } else if (cmd == "help") {
        cout << "Commands: start, stats, help\n";
    } else {
        cout << "Unknown command\n";
    }
    return 0;
}
