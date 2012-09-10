require 'json'
require 'pp'
require 'thread'
require_relative 'stack.rb'

class Pysakki
  attr_accessor :koodi, :osoite, :nimi, :x, :y, :naapurit

  def initialize args
    @koodi = args["koodi"]
    @osoite = args["osoite"]
    @nimi = args["nimi"]
    @x = args["x"]
    @y = args["y"]
    @naapurit = args["naapurit"]
  end

end

class Node
  attr_accessor :pysakki, :visited, :matka, :parent

  def initialize pysakki, matka, parent, visited = true
    @pysakki = pysakki
    @matka = matka
    @parent = parent
    @visited = visited
  end

  def naapurit
    pysakki.naapurit
  end

end


class BFS
  attr_accessor :pysakki_array, :pysakit, :json, :stack

  def initialize
    @pysakki_array = []
    @pysakit = Hash.new
    @stack = Stack.new
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
    puts "alku #{alku}"
    puts "loppu #{loppu}"
    queue = Queue.new
    queue << Node.new(@pysakit[alku], 0, nil, true)
    while !queue.empty?
      pysakki_nyt = queue.pop
      pysakki_nyt.visited = true
      pysakki_nyt.naapurit.each do |naapuri|
        naapuri_pysakki = @pysakit[naapuri[0]] #naapuri[1] on etäisyys - ei huomioida nyt
        naapuri_node = Node.new naapuri_pysakki, pysakki_nyt.matka+1, pysakki_nyt, false
       #                       pysakki,         matka,               parent,      visited - miksi :)
        if naapuri_node.pysakki.koodi == loppu
          return naapuri_node
        end
        unless naapuri_node.visited
          queue << naapuri_node
        end
      end
    end
  end

  def hae_reitti alku, loppu
    tulos = haku alku,loppu
    stack.push tulos
    while tulos.parent != nil
      stack.push tulos.parent
      tulos = tulos.parent

    end
    while !stack.empty?
      poimittu = stack.pop
      puts "#{poimittu.pysakki.koodi} --- Matka: #{poimittu.matka}"
    end

  end

end

bfs = BFS.new
bfs.hae_reitti "1250429", "1121480"

#"1250429", "1121480"
