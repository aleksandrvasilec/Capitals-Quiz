# capitals_quiz.rb
#!/usr/bin/env ruby
require 'json'

COUNTRIES = {
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
}

STATS_FILE = "capitals_stats.json"

class CapitalsQuiz
  attr_reader :stats

  def initialize
    @stats = { "correct" => 0, "incorrect" => 0, "total" => 0 }
    load_stats
  end

  def load_stats
    if File.exist?(STATS_FILE)
      begin
        @stats = JSON.parse(File.read(STATS_FILE))
      rescue
        @stats = { "correct" => 0, "incorrect" => 0, "total" => 0 }
      end
    end
  end

  def save_stats
    File.write(STATS_FILE, JSON.pretty_generate(@stats))
  end

  def get_hint(capital, level)
    case level
    when 1
      "First letter is '#{capital[0]}'"
    when 2
      "Has #{capital.length} letters"
    when 3
      wrong = COUNTRIES.values.reject { |c| c == capital }.sample(2)
      options = (wrong + [capital]).shuffle
      "Choose one: #{options.join(', ')}"
    else
      ""
    end
  end

  def run_quiz(rounds = 10)
    puts "\n🌍 Capitals Quiz"
    puts "Round 1/#{rounds}\n"

    countries_list = COUNTRIES.keys.shuffle
    selected = countries_list.first(rounds)

    correct = 0
    incorrect = 0

    selected.each_with_index do |country, idx|
      capital = COUNTRIES[country]
      hint_level = 0
      puts "Country: #{country}"
      loop do
        print "Your answer (or 'hint', 'skip', 'quit'): "
        input = gets.chomp
        case input.downcase
        when "quit"
          puts "Quitting..."
          return
        when "skip"
          puts "Skipped. The capital is #{capital}"
          incorrect += 1
          break
        when "hint"
          hint_level += 1
          if hint_level > 3
            puts "No more hints available."
          else
            puts "💡 Hint: #{get_hint(capital, hint_level)}"
          end
          next
        else
          if input.downcase == capital.downcase
            puts "✅ Correct!"
            correct += 1
            break
          else
            puts "❌ Wrong. Try again or type 'skip'."
          end
        end
      end
      puts "Score: #{correct}/#{idx+1} (#{((correct.to_f/(idx+1))*100).round(1)}%)\n"
    end

    @stats["correct"] += correct
    @stats["incorrect"] += incorrect
    @stats["total"] += correct + incorrect
    save_stats
    puts "Quiz finished! Correct: #{correct}, Incorrect: #{incorrect}"
  end

  def show_stats
    puts "\n📊 Statistics"
    puts "Correct: #{@stats['correct']}"
    puts "Incorrect: #{@stats['incorrect']}"
    puts "Total: #{@stats['total']}"
    if @stats['total'] > 0
      pct = @stats['correct'].to_f / @stats['total'] * 100
      puts "Accuracy: #{pct.round(1)}%"
    end
  end
end

if ARGV.empty?
  puts "Usage: capitals_quiz.rb [start|stats|help]"
  exit
end

quiz = CapitalsQuiz.new
case ARGV[0].downcase
when "start"
  quiz.run_quiz(10)
when "stats"
  quiz.show_stats
when "help"
  puts "Commands: start, stats, help"
else
  puts "Unknown command"
end
