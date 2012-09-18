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
#Aika_maaliin on heuristinen arvio, ettå paljonko matkaa on vähintään(minuuteissa)
#
#Odotusaika, paljonko pysakilla pitää odotella
# valimatkan_aika kuljetun välin kesto
# odotusaika pysakilla=parent? ennen matkaa
class Node
  attr_accessor :pysakki, :matka, :parent, :linja, :aika_maaliin, :valimatkan_aika, :odotusaika, :aika_yhteensa

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


  def to_ss
    lopputulos = "Koodi: #{@pysakki.koodi} "
    lopputulos << "Linja: #{@linja.koodi}" if @linja
    lopputulos << " Valimatka: #{@valimatkan_aika} Matka(askelia):#{@matka}"
    lopputulos << " odotusaika: #{@odotusaika}"
    lopputulos << " aika_yhteensa #{@aika_yhteensa}"
    lopputulos
  end

end


class AStar

  attr_accessor :json_verkko, :json_linjat, :pysakki_array, :pysakit, :linjat_array, :linjat, :visited, :stack, :x, :y, :jonossa_olleet, :jx, :jy

  def initialize
    @pysakki_array, @pysakit, @linjat_array = [], {}, []
    @visited = []
    @stack = Stack.new
    @linjat = Hash.new
    @jonossa_olleet = []
    @jx = []
    @jx = []

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

  #Priorityqueuen takia arvot negatiivisina :)
  def heur curr, goal
    -((curr.x- @pysakit[goal].x).abs + (curr.y - @pysakit[goal].y).abs)/526
  end

  def aika curr, seur, linja
    toka_indeksi = linja.pysKoodit.index seur[0]
    eka_indeksi = linja.pysKoodit.index curr.koodi
    linja.psAjat[toka_indeksi]-linja.psAjat[eka_indeksi]
  end

  def odotusaika pysakki_nyt, linja
    indeksi = linja.pysKoodit.index pysakki_nyt.koodi
    vali = linja.psAjat[indeksi] %10
    kohta = pysakki_nyt.aika_yhteensa%10

    binding.pry
    (vali-kohta).abs
  end

  def haku alku="1250429", loppu="1121480", alku_aika=0
    puts "alku #{alku} --- loppu #{loppu}"
    queue = Containers::PriorityQueue.new
    alku_node = Node.new @pysakit[alku], 0, nil, nil, (heur @pysakit[alku], loppu)
    alku_node.valimatkan_aika=0
    alku_node.aika_yhteensa=0
    alku_node.odotusaika=0
    queue.push alku_node, alku_node.aika_maaliin
    @jonossa_olleet.push alku_node


    #välimatkan_aika on parent..self
    while !queue.empty?
      pysakki_nyt = queue.pop
      unless @visited.include? pysakki_nyt.koodi
        @visited.push pysakki_nyt.koodi
        pysakki_nyt.naapurit.each do |naapuri|
          naapuri_pysakki = @pysakit[naapuri[0]]
          naapuri_node = Node.new naapuri_pysakki, pysakki_nyt.matka+1, pysakki_nyt, @linjat[naapuri[1][0]], heur(naapuri_pysakki, loppu)

          naapuri_node.odotusaika=odotusaika(pysakki_nyt, @linjat[naapuri[1][0]])
          naapuri_node.valimatkan_aika=aika pysakki_nyt, naapuri, @linjat[naapuri[1][0]]
          naapuri_node.aika_yhteensa=pysakki_nyt.aika_yhteensa+naapuri_node.odotusaika+naapuri_node.valimatkan_aika
          #                       pysakki,          matka,               parent
          if naapuri_node.koodi == loppu
            return naapuri_node
          end
          unless @visited.include? naapuri_node.koodi
            puts "arvostus: #{naapuri_node.aika_maaliin} nyt: #{naapuri_pysakki.koodi}, loppu: #{loppu}"
            queue.push naapuri_node, naapuri_node.aika_maaliin
            @jonossa_olleet.push naapuri_node
            #binding.pry
          end
        end
      end

    end
  end

  #
  #Printtaillaan koko reitti
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
      puts poimittu.to_ss
      #puts "#{poimittu.pysakki.koodi} #{poimittu.pysakki.nimi} Pysakki: #{poimittu.pysakki.koodi} --- Matka: #{poimittu.matka}"
    end
    kaikki.each do |p|
      x_koord<< p.pysakki.x
      y_koord<< p.pysakki.y
    end
    puts "\nLopullinen reitti:"
    @x = "x <- c(" + x_koord.join(', ') + ")"
    puts @x
    @y = "y <- c(" + y_koord.join(', ') + ")"
    puts @y
    puts

    jykoord = []
    jxkoord=[]
    @jonossa_olleet.each do |j|
      jxkoord << j.pysakki.x
      jykoord << j.pysakki.y
    end
    @jx = "x <- c(" + jxkoord.join(', ') +")"
    @jy = "x <- c(" + jykoord.join(', ') +")"

    puts
    puts "etsityt pysakit"
    puts @jx
    puts @jy
  end


end

AStar.new.hae_reitti "1250429", "1121480", 0
#AStar.new.hae_reitti "1230407", "1203410", 0