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
  attr_accessor :pysakki, :matka, :parent, :linja

  def initialize pysakki, matka, parent, linja
    @pysakki = pysakki
    @matka = matka
    @parent = parent
    @linja = linja
  end

  def naapurit
    pysakki.naapurit
  end

  def koodi
    @pysakki.koodi
  end

  #välimatka väliltä parent..self
  def valimatka
    indeksi = @linja.psKoodit.index @parent.koodi
    return @linja.psAjat[indeksi]-@linja.psAjat[indeksi+1] if @linja.psAjat[indeksi+1]
    0
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


  def haku alku="1250429", loppu="1121480", aika=0
    puts "alku #{alku} --- loppu #{loppu}"
    queue = Containers::PriorityQueue.new
    queue.push Node.new(@pysakit[alku], 0, nil, nil) , 0#tässä prioriteetti, heuristinen arvio?
    while !queue.empty?
      pysakki_nyt = queue.pop
      unless @visited.include? pysakki_nyt.koodi
        @visited.push pysakki_nyt.koodi
        pysakki_nyt.naapurit.each do |naapuri|
          naapuri_pysakki = @pysakit[naapuri[0]]
          naapuri_node = Node.new naapuri_pysakki, pysakki_nyt.matka+1, pysakki_nyt, @linjat[naapuri[1][0]]
          binding.pry

          #                       pysakki,          matka,               parent
          if naapuri_node.koodi == loppu
            return naapuri_node
          end
          unless @visited.include? naapuri_node.koodi
            queue.push naapuri_node, 1
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
      puts "#{poimittu.pysakki.koodi} #{poimittu.pysakki.nimi} Pysakki: #{poimittu.pysakki.koodi} --- Matka: #{poimittu.matka} Aika: #{poimittu.valimatka}"
    end
  end


end

AStar.new.hae_reitti "1250429", "1121480", 0