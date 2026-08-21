# capitals_quiz.php
#!/usr/bin/env php
<?php

$COUNTRIES = [
    "France" => "Paris", "Spain" => "Madrid", "Italy" => "Rome",
    "Germany" => "Berlin", "United Kingdom" => "London",
    "Portugal" => "Lisbon", "Netherlands" => "Amsterdam",
    "Belgium" => "Brussels", "Switzerland" => "Bern",
    "Austria" => "Vienna", "Greece" => "Athens",
    "Turkey" => "Ankara", "Russia" => "Moscow",
    "Ukraine" => "Kyiv", "Poland" => "Warsaw",
    "Sweden" => "Stockholm", "Norway" => "Oslo",
    "Denmark" => "Copenhagen", "Finland" => "Helsinki",
    "Ireland" => "Dublin", "USA" => "Washington",
    "Canada" => "Ottawa", "Mexico" => "Mexico City",
    "Brazil" => "Brasilia", "Argentina" => "Buenos Aires",
    "Chile" => "Santiago", "Peru" => "Lima",
    "Colombia" => "Bogota", "Venezuela" => "Caracas",
    "Australia" => "Canberra", "New Zealand" => "Wellington",
    "China" => "Beijing", "Japan" => "Tokyo",
    "South Korea" => "Seoul", "India" => "New Delhi",
    "Egypt" => "Cairo", "South Africa" => "Pretoria",
    "Nigeria" => "Abuja", "Kenya" => "Nairobi"
];

const STATS_FILE = "capitals_stats.json";

function loadStats() {
    if (file_exists(STATS_FILE)) {
        $data = file_get_contents(STATS_FILE);
        return json_decode($data, true) ?: ["correct" => 0, "incorrect" => 0, "total" => 0];
    }
    return ["correct" => 0, "incorrect" => 0, "total" => 0];
}

function saveStats($stats) {
    file_put_contents(STATS_FILE, json_encode($stats, JSON_PRETTY_PRINT));
}

function getHint($capital, $level) {
    global $COUNTRIES;
    switch ($level) {
        case 1:
            return "First letter is '" . $capital[0] . "'";
        case 2:
            return "Has " . strlen($capital) . " letters";
        case 3:
            $wrong = [];
            foreach ($COUNTRIES as $c) {
                if ($c != $capital && count($wrong) < 2) {
                    $wrong[] = $c;
                }
            }
            $options = array_merge($wrong, [$capital]);
            shuffle($options);
            return "Choose one: " . implode(", ", $options);
        default:
            return "";
    }
}

function runQuiz($rounds = 10) {
    global $COUNTRIES;
    $stats = loadStats();
    echo "\n🌍 Capitals Quiz\n";
    echo "Round 1/{$rounds}\n\n";

    $countries = array_keys($COUNTRIES);
    shuffle($countries);
    $selected = array_slice($countries, 0, $rounds);

    $correct = 0;
    $incorrect = 0;

    foreach ($selected as $i => $country) {
        $capital = $COUNTRIES[$country];
        $hintLevel = 0;
        echo "Country: {$country}\n";
        while (true) {
            echo "Your answer (or 'hint', 'skip', 'quit'): ";
            $input = trim(fgets(STDIN));
            switch (strtolower($input)) {
                case "quit":
                    echo "Quitting...\n";
                    return;
                case "skip":
                    echo "Skipped. The capital is {$capital}\n";
                    $incorrect++;
                    break 2;
                case "hint":
                    $hintLevel++;
                    if ($hintLevel > 3) {
                        echo "No more hints available.\n";
                    } else {
                        echo "💡 Hint: " . getHint($capital, $hintLevel) . "\n";
                    }
                    break;
                default:
                    if (strtolower($input) == strtolower($capital)) {
                        echo "✅ Correct!\n";
                        $correct++;
                        break 2;
                    } else {
                        echo "❌ Wrong. Try again or type 'skip'.\n";
                    }
            }
        }
        echo "Score: {$correct}/" . ($i+1) . " (" . ($correct/($i+1)*100) . "%)\n\n";
    }

    $stats["correct"] += $correct;
    $stats["incorrect"] += $incorrect;
    $stats["total"] += $correct + $incorrect;
    saveStats($stats);
    echo "Quiz finished! Correct: {$correct}, Incorrect: {$incorrect}\n";
}

function showStats() {
    $stats = loadStats();
    echo "\n📊 Statistics\n";
    echo "Correct: {$stats['correct']}\n";
    echo "Incorrect: {$stats['incorrect']}\n";
    echo "Total: {$stats['total']}\n";
    if ($stats['total'] > 0) {
        $pct = $stats['correct'] / $stats['total'] * 100;
        echo "Accuracy: " . round($pct, 1) . "%\n";
    }
}

if ($argc < 2) {
    echo "Usage: php capitals_quiz.php [start|stats|help]\n";
    exit;
}

$cmd = strtolower($argv[1]);
switch ($cmd) {
    case "start":
        runQuiz(10);
        break;
    case "stats":
        showStats();
        break;
    case "help":
        echo "Commands: start, stats, help\n";
        break;
    default:
        echo "Unknown command\n";
}
?>
