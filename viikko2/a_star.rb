require 'pry'
require 'json'
require 'pp'
require 'thread'
require 'algorithms'
require_relative '../viikko1/stack.rb'

class Pysakki
  attr_accessor :koodi, :osoite, :nimi, :x, :y, :naapurit

  def initialize args
    @koodi, @osoite, @nimi, @x, @y, @naapurit = args["koodi"], args["osoite"], args["nimi"], args["x"], args["y"], args["naapurit"]
  end

end

class Linja
  attr_accessor :koodi, :koodiLynyt, :nimi, :x, :y, :pysKoodit, :psAjat

  def initialize args
    @koodi, @koodiLyhyt, @nimi, @x, @y, @pysKoodit, @psAjat = args["koodi"], args["koodiLyhyt"], args["nimi"], args["x"], args["y"], args["psKoodit"], args["psAjat"]
  end

end


##
#Hakutila
class Node
  attr_accessor :pysakki, :matka, :parent, :linja, :aika_maaliin, :kulunut_aika

  def initialize pysakki, matka, parent, linja, aika_maaliin
    @pysakki = pysakki
    @matka = matka
    @parent = parent
    @linja = linja
    @aika_maaliin = aika_maaliin
  end


  def naapurit
    pysakki.naapurit
  end

  def koodi
    @pysakki.koodi
  end

  #välimatka väliltä parent..self
  def valimatka
    indeksi = @linja.psKoodit.index @parent.koodi if linja
    return @linja.psAjat[indeksi]-@linja.psAjat[indeksi+1] if @linja.psAjat[indeksi+1] if linja
    0
  end


  def to_s
    lopputulos = "Koodi: #{@pysakki.koodi} "
    lopputulos << "Linja: #{@linja.koodi}" if @linja
    lopputulos << " Aikaa kulunut: #{@kulunut_aika} Matka:#{@matka}"
    lopputulos
  end

end


class AStar

  attr_accessor :json_verkko, :json_linjat, :pysakki_array, :pysakit, :linjat_array, :linjat, :visited, :stack

  def initialize
    @pysakki_array, @pysakit, @linjat_array = [], {}, []
    @visited = []
    @stack = Stack.new
    @linjat = Hash.new

    read_json
  end

  def read_json
    f = File.new 'verkko.json', 'r'
    @json_verkko = JSON.parse f.gets
    @json_verkko.each do |j|
      @pysakki_array.push Pysakki.new(j)
    end
    @pysakki_array.each do |p|
      @pysakit[p.koodi] = p
    end

    f = File.new 'linjat.json', 'r'
    @json_linjat = JSON.parse f.gets
    @json_linjat.each do |l|
      @linjat_array.push Linja.new l
    end
    @linjat_array.each do |l|
      @linjat[l.koodi] = l
    end
  end

  def heur curr, goal
    -((curr.x- @pysakit[goal].x).abs + (curr.y - @pysakit[goal].y).abs)/526
  end

  def aika curr, seur, linja
    toka_indeksi = linja.pysKoodit.index seur[0]
    eka_indeksi = linja.pysKoodit.index curr.koodi
    linja.psAjat[toka_indeksi]-linja.psAjat[eka_indeksi]
  end


  def haku alku="1250429", loppu="1121480", aika=0
    puts "alku #{alku} --- loppu #{loppu}"
    queue = Containers::PriorityQueue.new
    alku_node = Node.new @pysakit[alku], 0, nil, nil, (heur @pysakit[alku], loppu)
    alku_node.kulunut_aika=0
    queue.push alku_node, alku_node.aika_maaliin


    while !queue.empty?
      pysakki_nyt = queue.pop
      unless @visited.include? pysakki_nyt.koodi
        @visited.push pysakki_nyt.koodi
        pysakki_nyt.naapurit.each do |naapuri|
          naapuri_pysakki = @pysakit[naapuri[0]]
          naapuri_node = Node.new naapuri_pysakki, pysakki_nyt.matka+1, pysakki_nyt, @linjat[naapuri[1][0]], heur(naapuri_pysakki, loppu)
          naapuri_node.kulunut_aika=pysakki_nyt.kulunut_aika+aika(pysakki_nyt, naapuri, @linjat[naapuri[1][0]])
          #                       pysakki,          matka,               parent
          if naapuri_node.koodi == loppu
            return naapuri_node
          end
          unless @visited.include? naapuri_node.koodi
            puts "arvostus: #{naapuri_node.aika_maaliin} nyt: #{naapuri_pysakki.koodi}, loppu: #{loppu}"
            queue.push naapuri_node, naapuri_node.aika_maaliin
            #binding.pry
          end
        end
      end

    end
  end

  def hae_reitti alku, loppu, aika
    tulos = haku alku, loppu, aika
    puts "Reitti haettu. Nyt tulostukseen"
    stack.push tulos
    while tulos.parent != nil
      stack.push tulos.parent
      tulos = tulos.parent
    end
    x_koord = []
    y_koord = []
    kaikki = []
    while !stack.empty?
      poimittu = stack.pop
      kaikki << poimittu
      puts poimittu
      #puts "#{poimittu.pysakki.koodi} #{poimittu.pysakki.nimi} Pysakki: #{poimittu.pysakki.koodi} --- Matka: #{poimittu.matka}"
    end
    kaikki.each do |p|
      x_koord<< p.pysakki.x
      y_koord<< p.pysakki.y
    end
    @x = "x <- c(" + x_koord.join(', ') + ")"
    @y = "y <- c(" + y_koord.join(', ') + ")"
  end


end

AStar.new.hae_reitti "1250429", "1121480", 0