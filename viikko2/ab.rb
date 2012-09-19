class Solmu
  attr_accessor :arvo, :lapset
  
end

class MinMax
  
  def alpha_beta_arvo solmu
    max_arvo solmu,-1,1
  end
  
  def max_arvo solmu, a, b
      return solmu.arvo if solmu ## lopputilassa
      v = -Float::INFINITY
      
      solmu.lapset.each do |l|
        v=[v,min_arvo(l,a,b)].max
        return b if v>=b
        a=[a,v].max
      end
      v
    end
    
    def min_arvo solmu, a, b
      return solmu.arvo if 0 #lopputila
      v = Float::INFINITY
      solmu.lapset.each do |l|
        v = [v,max_arvo(l,a,b)].min
        if v<=
      end
    end
  MIN-ARVO(Solmu,alpha,beta):
      if LOPPUTILA(Solmu) return(ARVO(Solmu))
      v=+Inf
      for each Lapsi in LAPSET(Solmu,’O’)
          v=MIN(v,MAX-ARVO(Lapsi,alpha,beta))
          if v<=alpha return(v)
          beta=MIN(beta,v)
  return(v)
end
