require 'json'
require 'pp'
require 'thread'

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
  attr_accessor :visited, :matka, :parent, :pysakki

  def initialize pysakki ,matka, parent, visited = true
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
  attr_accessor :pysakki_array, :pysakit, :json

  def initialize
    @pysakki_array = []
    @pysakit = Hash.new
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

  @vierailtu = Hash.new

  def haku alku, loppu
    queue = Queue.new
    queue << Node.new( @pysakit[alku], 0, nil, true )
    while !queue.empty?
      pysakki = queue.pop
      pysakki.naapurit.each do |pys|
        pys_node = Node.new(pys, pysakki.matka+1, pysakki)
        if pys_node.pysakki.koodi == loppu
          return pys_node
        end
        unless pys_node.visited
          queue << pys_node
        end
      end
    end

    #for jokaiselle solmulle u ∈ V
    #     color[u] = white
    #     distance[u] = ∞
    #tree[u] = NIL
    #color[s] = gray
    #distance[s] = 0
    #enqueue(Q,s)
    #while ( not empty(Q) )
    #  u = dequeue(Q)
    #  for jokaiselle solmulle v ∈ vierus[u]
    #  // kaikille u:n vierussolmuille v // solmua v ei vielä löydetty
    #  if color[v]==white
    #    color[v] = gray
    #    distance[v] = distance[u]+1 tree[v] = u
    #    enqueue(Q,v)
    #    color[u] = black
  end

end

bfs = BFS.new

bfs.haku "1250429", "1121480"
