// capitals_quiz.js
#!/usr/bin/env node
const fs = require('fs');
const readline = require('readline');
const { program } = require('commander');

const COUNTRIES = {
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
    "Nigeria": "Abuja", "Kenya": "Nairobi"
};

const STATS_FILE = 'capitals_stats.json';

class CapitalsQuiz {
    constructor() {
        this.stats = { correct: 0, incorrect: 0, total: 0 };
        this.loadStats();
        this.rl = readline.createInterface({
            input: process.stdin,
            output: process.stdout
        });
    }

    loadStats() {
        if (fs.existsSync(STATS_FILE)) {
            try {
                this.stats = JSON.parse(fs.readFileSync(STATS_FILE));
            } catch (e) {}
        }
    }

    saveStats() {
        fs.writeFileSync(STATS_FILE, JSON.stringify(this.stats, null, 2));
    }

    getHint(capital, level) {
        if (level === 1) return `First letter is '${capital[0]}'`;
        if (level === 2) return `Has ${capital.length} letters`;
        if (level === 3) {
            const wrong = Object.values(COUNTRIES).filter(c => c !== capital).slice(0, 2);
            const options = [...wrong, capital];
            // shuffle
            for (let i = options.length - 1; i > 0; i--) {
                const j = Math.floor(Math.random() * (i + 1));
                [options[i], options[j]] = [options[j], options[i]];
            }
            return `Choose one: ${options.join(', ')}`;
        }
        return '';
    }

    question(query) {
        return new Promise(resolve => {
            this.rl.question(query, answer => resolve(answer));
        });
    }

    async runQuiz(rounds = 10) {
        console.log('\n🌍 Capitals Quiz');
        console.log(`Round 1/${rounds}\n`);

        const countryList = Object.keys(COUNTRIES);
        // shuffle
        for (let i = countryList.length - 1; i > 0; i--) {
            const j = Math.floor(Math.random() * (i + 1));
            [countryList[i], countryList[j]] = [countryList[j], countryList[i]];
        }
        const selected = countryList.slice(0, rounds);

        let correct = 0, incorrect = 0;

        for (let i = 0; i < selected.length; i++) {
            const country = selected[i];
            const capital = COUNTRIES[country];
            let hintLevel = 0;
            console.log(`Country: ${country}`);
            while (true) {
                const answer = await this.question('Your answer (or \'hint\', \'skip\', \'quit\'): ');
                const cmd = answer.trim().toLowerCase();
                if (cmd === 'quit') {
                    console.log('Quitting...');
                    this.rl.close();
                    return;
                }
                if (cmd === 'skip') {
                    console.log(`Skipped. The capital is ${capital}`);
                    incorrect++;
                    break;
                }
                if (cmd === 'hint') {
                    hintLevel++;
                    if (hintLevel > 3) {
                        console.log('No more hints available.');
                    } else {
                        console.log(`💡 Hint: ${this.getHint(capital, hintLevel)}`);
                    }
                    continue;
                }
                if (cmd === capital.toLowerCase()) {
                    console.log('✅ Correct!');
                    correct++;
                    break;
                } else {
                    console.log('❌ Wrong. Try again or type \'skip\'.');
                }
            }
            console.log(`Score: ${correct}/${i+1} (${(correct/(i+1)*100).toFixed(1)}%)\n`);
        }

        this.stats.correct += correct;
        this.stats.incorrect += incorrect;
        this.stats.total += correct + incorrect;
        this.saveStats();
        console.log(`Quiz finished! Correct: ${correct}, Incorrect: ${incorrect}`);
        this.rl.close();
    }

    showStats() {
        console.log('\n📊 Statistics');
        console.log(`Correct: ${this.stats.correct}`);
        console.log(`Incorrect: ${this.stats.incorrect}`);
        console.log(`Total: ${this.stats.total}`);
        if (this.stats.total > 0) {
            const pct = this.stats.correct / this.stats.total * 100;
            console.log(`Accuracy: ${pct.toFixed(1)}%`);
        }
    }
}

program
    .command('start')
    .description('Start a quiz')
    .action(() => {
        const quiz = new CapitalsQuiz();
        quiz.runQuiz(10);
    });

program
    .command('stats')
    .description('Show statistics')
    .action(() => {
        const quiz = new CapitalsQuiz();
        quiz.showStats();
    });

program
    .command('help')
    .description('Show help')
    .action(() => {
        console.log('Commands: start, stats, help');
    });

program.parse(process.argv);
