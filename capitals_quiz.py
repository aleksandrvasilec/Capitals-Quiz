# capitals_quiz.py
import json
import os
import random
import sys
from typing import Dict, List, Tuple

DATA_FILE = "capitals_data.json"
STATS_FILE = "capitals_stats.json"

COUNTRIES = {
    "France": "Paris",
    "Spain": "Madrid",
    "Italy": "Rome",
    "Germany": "Berlin",
    "United Kingdom": "London",
    "Portugal": "Lisbon",
    "Netherlands": "Amsterdam",
    "Belgium": "Brussels",
    "Switzerland": "Bern",
    "Austria": "Vienna",
    "Greece": "Athens",
    "Turkey": "Ankara",
    "Russia": "Moscow",
    "Ukraine": "Kyiv",
    "Poland": "Warsaw",
    "Sweden": "Stockholm",
    "Norway": "Oslo",
    "Denmark": "Copenhagen",
    "Finland": "Helsinki",
    "Ireland": "Dublin",
    "USA": "Washington",
    "Canada": "Ottawa",
    "Mexico": "Mexico City",
    "Brazil": "Brasilia",
    "Argentina": "Buenos Aires",
    "Chile": "Santiago",
    "Peru": "Lima",
    "Colombia": "Bogota",
    "Venezuela": "Caracas",
    "Australia": "Canberra",
    "New Zealand": "Wellington",
    "China": "Beijing",
    "Japan": "Tokyo",
    "South Korea": "Seoul",
    "India": "New Delhi",
    "Egypt": "Cairo",
    "South Africa": "Pretoria",
    "Nigeria": "Abuja",
    "Kenya": "Nairobi",
}

class CapitalsQuiz:
    def __init__(self):
        self.countries = COUNTRIES
        self.stats = {"correct": 0, "incorrect": 0, "total": 0}
        self.load_stats()

    def load_stats(self):
        if os.path.exists(STATS_FILE):
            with open(STATS_FILE, "r") as f:
                self.stats = json.load(f)

    def save_stats(self):
        with open(STATS_FILE, "w") as f:
            json.dump(self.stats, f)

    def get_hint(self, capital: str, level: int) -> str:
        if level == 1:
            return f"First letter is '{capital[0]}'"
        elif level == 2:
            return f"Has {len(capital)} letters"
        elif level == 3:
            # Generate 3 random wrong capitals
            wrong = random.sample([c for c in self.countries.values() if c != capital], 2)
            options = wrong + [capital]
            random.shuffle(options)
            return "Choose one: " + ", ".join(options)
        return ""

    def run_quiz(self, rounds: int = 10):
        print("\n🌍 Capitals Quiz")
        print(f"Round 1/{rounds}\n")
        correct, incorrect = 0, 0
        countries_list = list(self.countries.keys())
        random.shuffle(countries_list)
        selected = countries_list[:rounds]

        for i, country in enumerate(selected, 1):
            capital = self.countries[country]
            hint_level = 0
            print(f"Country: {country}")
            while True:
                answer = input("Your answer (or 'hint', 'skip', 'quit'): ").strip()
                if answer.lower() == "quit":
                    print("Quitting...")
                    return
                if answer.lower() == "skip":
                    print(f"Skipped. The capital is {capital}")
                    incorrect += 1
                    break
                if answer.lower() == "hint":
                    hint_level += 1
                    if hint_level > 3:
                        print("No more hints available.")
                    else:
                        print("💡 Hint:", self.get_hint(capital, hint_level))
                    continue
                if answer.lower() == capital.lower():
                    print("✅ Correct!")
                    correct += 1
                    break
                else:
                    print("❌ Wrong. Try again or type 'skip' to skip.")
                    # We don't count incorrect immediately, allow retry
            # After each round, show score
            print(f"Score: {correct}/{i} ({correct/(correct+incorrect)*100 if correct+incorrect>0 else 0:.1f}%)\n")

        self.stats["correct"] += correct
        self.stats["incorrect"] += incorrect
        self.stats["total"] += correct + incorrect
        self.save_stats()
        print(f"Quiz finished! Correct: {correct}, Incorrect: {incorrect}")

    def show_stats(self):
        print("\n📊 Statistics")
        print(f"Correct: {self.stats['correct']}")
        print(f"Incorrect: {self.stats['incorrect']}")
        print(f"Total: {self.stats['total']}")
        if self.stats['total'] > 0:
            pct = self.stats['correct'] / self.stats['total'] * 100
            print(f"Accuracy: {pct:.1f}%")

def main():
    quiz = CapitalsQuiz()
    if len(sys.argv) < 2:
        print("Usage: capitals_quiz.py [start|stats|help]")
        return
    cmd = sys.argv[1].lower()
    if cmd == "start":
        quiz.run_quiz(10)
    elif cmd == "stats":
        quiz.show_stats()
    elif cmd == "help":
        print("Commands: start, stats, help")
    else:
        print("Unknown command")

if __name__ == "__main__":
    main()
