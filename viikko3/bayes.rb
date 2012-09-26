#require 'pry'
=begin
def generoi_monikkoja n, malli
  for i in 1..N
    for X in Malli.muuttujat
      X = satunnaisluku(X.jakauma(X.vanhemmat))
      puts X
    end
  end
end

def satunnaisluku jakauma, k
  r = [0, 1]
  i = 0
  while r > jakauma[i]
    r=r-jakauma[i]
    i = i+1
  end
  i
end
=end
=begin
P(“AKUSSA VIRTAA”)=0.9
P(“RADIO” | “AKUSSA VIRTAA”)=0.9 P(“RADIO” | ¬”AKUSSA VIRTAA”)=0
P(“SYTYTYS” | “AKUSSA VIRTAA”) = 0.95 P(“SYTYTYS” | ¬”AKUSSA VIRTAA”)=0
P(“BENSAA”) = 0.95
P(“KÄYNNISTYY” | “AKUSSA VIRTAA” JA “BENSAA”) = 0.99 P(“KÄYNNISTYY” | ¬”A” TAI ¬”B”) = 0
P(“LIIKKUU” | “KÄYNNISTYY”) = 0.99 P(“LIIKKUU” | ¬”KÄYNNISTYY”) =

Toteuta algoritmi, joka generoi monikkoja (A, R, S, B, K, L), miss ̈a
A=1, joss akussa on virtaa,
R=1, joss radio soi,
S=1, joss sytytys toimii,
B=1, joss tankissa on bensaa,
K=1, joss moottori k äynnistyy,
L=1, joss auto liikkuu.
=end

#testing random

def do_it
  tuple = []
  #Akku 0
  if Random.rand(0..100) <= 90
    tuple[0] = 1
  else
    tuple[0] = 0
  end
  #Radio 1
  if tuple[0] == 1 and Random.rand(0..100) <= 95
    tuple[1] = 1
  else
    tuple[1] = 0
  end
  #Sytytys 2
  if tuple[0] == 1 and Random.rand(0..100) <= 95
    tuple[2]=1
  else
    tuple[2] = 0
  end
  #Bensa 3
  if Random.rand(0..100) <= 95
    tuple[3] = 1
  else
    tuple[3] = 0
  end

#Käynnistyy 4
  if tuple[2] == 1 and tuple[3] == 1 and Random.rand(0..100) <= 99
    tuple[4] = 1
  else
    tuple[4]=0
  end
#liikkuu 5
  if tuple[4] == 1 and Random.rand(0..100) <= 99
    tuple[5]=1
  else
    tuple[5]=0
  end
  tuple.join(', ')
#binding.pry
end

def laske
  al_nollat = 0
  bl_nollat = 0
  l_nolla = 0
  n = 10000000
  n.times do
    tulos = do_it.split(', ')

    al_nollat = al_nollat+1 if tulos[5]=='0' and tulos[0]=='0'
    l_nolla = l_nolla+1 if tulos[5] == '0'
    bl_nollat = bl_nollat+1 if tulos[5]=='0' and tulos[3]=='0'
  end

  puts "Suoritettiin #{n} kertaa"
  puts "A ja L == 0: #{al_nollat} kertaa eli #{al_nollat/l_nolla.to_f}%"
  puts "b ja L == 0: #{bl_nollat} kertaa eli #{bl_nollat/l_nolla.to_f}%"
end

laske

#1000000.times do
#  puts do_it
#end
