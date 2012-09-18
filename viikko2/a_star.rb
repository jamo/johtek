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
  attr_accessor :pysakki, :matka, :parent, :linja, :aika_maaliin, :valimatkan_aika, :aika_yhteensa, :odotus_aika, :klo_nyt

=begin
  @klo_nyt

  def klo_nyt= aika
    @klo_nyt=aika
  end

  def klo_nyt
    if @aika_yhteensa and @klo_nyt
      @klo_nyt + @aika_yhteensa
    else
      0
    end
  end
=end

  def initialize pysakki, matka, parent, linja, heur_aika_maaliin
    @pysakki = pysakki
    @matka = matka
    @parent = parent
    @linja = linja
    @aika_maaliin = heur_aika_maaliin
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
    lopputulos << " odotusaika: #{@odotus_aika}"
    lopputulos << " aika_yhteensa #{@aika_yhteensa}"
    lopputulos << " kello_nyt #{@klo_nyt}"
    lopputulos
  end

end


class AStar

  attr_accessor :json_verkko, :json_linjat, :pysakki_array, :pysakit, :linjat_array, :linjat, :visited, :stack, :x, :y, :jonossa_olleet, :jx, :jy
  attr_accessor :nodes

  def initialize
    @pysakki_array, @pysakit, @linjat_array = [], {}, []
    @visited = []
    @stack = Stack.new
    @linjat = Hash.new
    @jonossa_olleet = []
    @jx = []
    @jx = []
    @nodes = Hash.new

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

  def valimatkan_aika edeltaja, nykyinen, linja
    toka_indeksi = linja.pysKoodit.index nykyinen[0]
    eka_indeksi = linja.pysKoodit.index edeltaja.koodi
    linja.psAjat[toka_indeksi]-linja.psAjat[eka_indeksi]
  end

  def odotusaika pysakki_nyt, linja
    indeksi = linja.pysKoodit.index(pysakki_nyt.koodi)
    montako_minuuttia_pysakille = linja.psAjat[indeksi]
    odotus=montako_minuuttia_pysakille%10-pysakki_nyt.aika_yhteensa%10
    #puts "odotus: #{odotus}"
    odotus+=10 if odotus < 0
    odotus
  end

  def create_node pysakki, matka, parent, linja, heur_arvio
    palautettava = @nodes[pysakki.koodi]
    return palautettava if palautettava
    Node.new pysakki, matka, parent, linja, heur_arvio
  end

  def haku alku="1250429", loppu="1121480", alku_aika=0
    puts "alku #{alku} --- loppu #{loppu} --- alkuaika #{alku_aika}"
    queue = Containers::PriorityQueue.new
    alku_node = Node.new @pysakit[alku], 0, nil, nil, (heur @pysakit[alku], loppu)
    alku_node.valimatkan_aika=0
    alku_node.aika_yhteensa=0
    alku_node.odotus_aika=0
    alku_node.klo_nyt=alku_aika
    @nodes[alku_node.koodi] = alku_node
    queue.push alku_node, alku_node.aika_maaliin
    @jonossa_olleet.push alku_node

    #välimatkan_aika on parent..self
    while !queue.empty?
      pysakki_nyt = queue.pop
      unless @visited.include? pysakki_nyt.koodi
        @visited.push pysakki_nyt.koodi
        pysakki_nyt.naapurit.each do |naapuri|
          naapuri_pysakki = @pysakit[naapuri[0]]
          naapuri_node = create_node naapuri_pysakki, pysakki_nyt.matka+1, pysakki_nyt, @linjat[naapuri[1][0]], heur(naapuri_pysakki, loppu)
          #                       pysakki,          matka,               parent       linja                    matka linnuntietä
          naapuri_node.valimatkan_aika = valimatkan_aika pysakki_nyt, naapuri, @linjat[naapuri[1][0]]
          #         pysäkki jonka naapureita läpikäydään, yksi naapuri, linja
          naapuri_node.odotus_aika=odotusaika(pysakki_nyt, @linjat[naapuri[1][0]])
          naapuri_node.aika_yhteensa=pysakki_nyt.aika_yhteensa+naapuri_node.odotus_aika+naapuri_node.valimatkan_aika
          naapuri_node.klo_nyt=naapuri_node.aika_yhteensa+alku_aika
          return naapuri_node if naapuri_node.koodi == loppu
          unless @visited.include? naapuri_node.koodi
            puts "arvostus: #{naapuri_node.aika_maaliin.to_s.gsub('-','')} nyt: #{naapuri_pysakki.koodi}, loppu: #{loppu}"
            queue.push(naapuri_node, (naapuri_node.aika_maaliin + -(naapuri_node.aika_yhteensa)))
            @jonossa_olleet.push naapuri_node
          end
        end
      end

    end
  end

  #
  #Printtaillaan koko reitti
  def hae_reitti alku, loppu, aika=0
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

   # puts
   # puts "etsityt pysakit"
   # puts @jx
   # puts @jy
  end

  def write_data
    file = File.open 'reitti.txt', 'w'
    file.puts
    file.puts @x
    file.puts @y
    file.puts %Q{lines(x,y, lwd = 2, col = "orange")}
    #file.puts
    #file.puts @jx
    #file.puts @jy
    #file.puts %Q{points(x,y, lwd = 2, col = "green")}


    file.close
  end

  def create_rplot_pdf
    write_data
    `cat rplot.txt reitti.txt | r --save Rplots.pdf`
    puts "rplot created"
  end


end

a = AStar.new
a.hae_reitti "1250429", "1121480", 2
a.create_rplot_pdf
#AStar.new.hae_reitti "1230407", "1203410", 0

#Pitka, oik -> vas reunaan "1230407", "1203410"
#Keskustassa... "1010424", "1220433"