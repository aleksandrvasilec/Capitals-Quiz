// capitals_quiz.go
package main

import (
	"bufio"
	"encoding/json"
	"fmt"
	"math/rand"
	"os"
	"strings"
	"time"
)

type Stats struct {
	Correct   int `json:"correct"`
	Incorrect int `json:"incorrect"`
	Total     int `json:"total"`
}

var countries = map[string]string{
	"France": "Paris", "Spain": "Madrid", "Italy": "Rome",
	"Germany": "Berlin", "United Kingdom": "London",
	"Portugal": "Lisbon", "Netherlands": "Amsterdam",
	"Belgium": "Brussels", "Switzerland": "Bern",
	"Austria": "Vienna", "Greece": "Athens",
	"Turkey": "Ankara", "Russia": "Moscow",
	"Ukraine": "Kyiv", "Poland": "Warsaw",
	"Sweden": "Stockholm", "Norway": "Oslo",
	"Denmark": "Copenhagen", "Finland": "Helsinki",
	"Ireland": "Dublin", "USA": "Washington",
	"Canada": "Ottawa", "Mexico": "Mexico City",
	"Brazil": "Brasilia", "Argentina": "Buenos Aires",
	"Chile": "Santiago", "Peru": "Lima",
	"Colombia": "Bogota", "Venezuela": "Caracas",
	"Australia": "Canberra", "New Zealand": "Wellington",
	"China": "Beijing", "Japan": "Tokyo",
	"South Korea": "Seoul", "India": "New Delhi",
	"Egypt": "Cairo", "South Africa": "Pretoria",
	"Nigeria": "Abuja", "Kenya": "Nairobi",
}

const statsFile = "capitals_stats.json"

func loadStats() Stats {
	var stats Stats
	data, err := os.ReadFile(statsFile)
	if err != nil {
		return stats
	}
	json.Unmarshal(data, &stats)
	return stats
}

func saveStats(stats Stats) {
	data, _ := json.MarshalIndent(stats, "", "  ")
	os.WriteFile(statsFile, data, 0644)
}

func getHint(capital string, level int) string {
	switch level {
	case 1:
		return fmt.Sprintf("First letter is '%c'", capital[0])
	case 2:
		return fmt.Sprintf("Has %d letters", len(capital))
	case 3:
		// Get two random wrong capitals
		var wrong []string
		for _, c := range countries {
			if c != capital {
				wrong = append(wrong, c)
				if len(wrong) >= 2 {
					break
				}
			}
		}
		options := append(wrong, capital)
		rand.Shuffle(len(options), func(i, j int) { options[i], options[j] = options[j], options[i] })
		return "Choose one: " + strings.Join(options, ", ")
	}
	return ""
}

func runQuiz(rounds int) {
	stats := loadStats()
	fmt.Println("\n🌍 Capitals Quiz")
	fmt.Printf("Round 1/%d\n\n", rounds)

	var countryList []string
	for k := range countries {
		countryList = append(countryList, k)
	}
	rand.Shuffle(len(countryList), func(i, j int) { countryList[i], countryList[j] = countryList[j], countryList[i] })
	if len(countryList) > rounds {
		countryList = countryList[:rounds]
	}

	correct, incorrect := 0, 0
	reader := bufio.NewReader(os.Stdin)

	for i, country := range countryList {
		capital := countries[country]
		hintLevel := 0
		fmt.Printf("Country: %s\n", country)
		for {
			fmt.Print("Your answer (or 'hint', 'skip', 'quit'): ")
			input, _ := reader.ReadString('\n')
			input = strings.TrimSpace(input)

			switch strings.ToLower(input) {
			case "quit":
				fmt.Println("Quitting...")
				return
			case "skip":
				fmt.Printf("Skipped. The capital is %s\n", capital)
				incorrect++
				goto next
			case "hint":
				hintLevel++
				if hintLevel > 3 {
					fmt.Println("No more hints available.")
				} else {
					fmt.Printf("💡 Hint: %s\n", getHint(capital, hintLevel))
				}
				continue
			default:
				if strings.EqualFold(input, capital) {
					fmt.Println("✅ Correct!")
					correct++
					goto next
				} else {
					fmt.Println("❌ Wrong. Try again or type 'skip'.")
				}
			}
		}
	next:
		fmt.Printf("Score: %d/%d (%.1f%%)\n\n", correct, i+1, float64(correct)/float64(i+1)*100)
	}

	stats.Correct += correct
	stats.Incorrect += incorrect
	stats.Total += correct + incorrect
	saveStats(stats)
	fmt.Printf("Quiz finished! Correct: %d, Incorrect: %d\n", correct, incorrect)
}

func showStats() {
	stats := loadStats()
	fmt.Println("\n📊 Statistics")
	fmt.Printf("Correct: %d\n", stats.Correct)
	fmt.Printf("Incorrect: %d\n", stats.Incorrect)
	fmt.Printf("Total: %d\n", stats.Total)
	if stats.Total > 0 {
		pct := float64(stats.Correct) / float64(stats.Total) * 100
		fmt.Printf("Accuracy: %.1f%%\n", pct)
	}
}

func main() {
	if len(os.Args) < 2 {
		fmt.Println("Usage: capitals_quiz [start|stats|help]")
		return
	}
	rand.Seed(time.Now().UnixNano())
	cmd := os.Args[1]
	switch cmd {
	case "start":
		runQuiz(10)
	case "stats":
		showStats()
	case "help":
		fmt.Println("Commands: start, stats, help")
	default:
		fmt.Println("Unknown command")
	}
}
