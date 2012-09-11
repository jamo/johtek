require 'json'
require 'pp'
require 'thread'
require_relative 'stack.rb'

class Pysakki
  attr_accessor :koodi, :osoite, :nimi, :x, :y, :naapurit

  def initialize args
    @koodi, @osoite, @nimi, @x, @y, @naapurit = args["koodi"], args["osoite"], args["nimi"], args["x"], args["y"], args["naapurit"]
  end

end

class Node
  attr_accessor :pysakki, :visited, :matka, :parent

  def initialize pysakki, matka, parent
    @pysakki = pysakki
    @matka = matka
    @parent = parent
  end

  def naapurit
    pysakki.naapurit
  end

  def vieraile
    @visited = true
  end

  def visited?
    @visited
  end

  def koodi
    @pysakki.koodi
  end

end


class BFS
  attr_accessor :pysakki_array, :pysakit, :json, :stack, :x, :y, :dbg, :visited

  def initialize
    @pysakki_array, @pysakit, @stack = [], Hash.new, Stack.new
    @dbg = true
    @visited = []
    read_json
  end

  def read_json
    f = File.new 'verkko.json', 'r'
    @json = JSON.parse f.gets
    @json.each do |j|
      @pysakki_array.push Pysakki.new(j)
    end
    @pysakki_array.each do |p|
      @pysakit[p.koodi] = p
    end
  end

  def haku alku, loppu
    matkat = []
    puts "alku #{alku} --- loppu #{loppu}"
    queue = Queue.new
    queue << Node.new(@pysakit[alku], 0, nil)
    while !queue.empty?
      pysakki_nyt = queue.pop
      unless @visited.include? pysakki_nyt.pysakki.koodi
        @visited << pysakki_nyt.pysakki.koodi
        pysakki_nyt.naapurit.each do |naapuri|
          naapuri_pysakki = @pysakit[naapuri[0]] #naapuri[1] on etäisyys - ei huomioida nyt
          naapuri_node = Node.new naapuri_pysakki, pysakki_nyt.matka+1, pysakki_nyt
         #                        pysakki,          matka,               parent
          if naapuri_node.koodi == loppu
            return naapuri_node
          end
          unless @visited.include? naapuri_node.koodi
            queue << naapuri_node
          end
        end
      end
    end

  end

  def hae_reitti alku, loppu
    tulos = haku alku, loppu
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
      puts "#{poimittu.pysakki.koodi} #{poimittu.pysakki.nimi} --- Matka: #{poimittu.matka}"
    end
    kaikki.each do |p|
      x_koord<< p.pysakki.x
      y_koord<< p.pysakki.y
    end
    @x = "x <- c(" + x_koord.join(', ') + ")"
    @y = "y <- c(" + y_koord.join(', ') + ")"
    puts @x
    puts @y
  end

  def write_data
    file = File.open 'reitti.txt', 'w'
    file.puts
    file.puts x
    file.puts y
    file.puts %Q{lines(x,y, lwd = 2, col = "orange")}
    file.close
  end

  def create_rplot_pdf
    write_data
    `cat rplot.txt reitti.txt | r --save Rplots.pdf`
    puts "rplot created" if @dbg
  end


end

bfs = BFS.new
tulos = bfs.hae_reitti "1230407", "1203410"
#"1010424", "1220433" #"1250429", "1140436"
bfs.create_rplot_pdf

#hidas "1230407", "1203410"
# ok "1010424", "1220433"

#"1010424", "1220433"

#"1230407", "1203410"

#"1250429", "1121480"
#nopea ja helppo: "1250429", "1121480"
