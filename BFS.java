import java.util.HashMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/** Lataa ja avaa gson-2.2.2.zip osoitteesta 
 *  http://google-gson.googlecode.com/files/google-gson-2.2.2-release.zip
 *  
 *  Komentorivilta kaantaessasi kutsu 
 *  javac -cp /PATH/TO/google-gson-2.2.2.jar BFS.java
 *  
 *  Jos kaytat IDEa, niin hae googlella miten saat sen omaan IDEesi kiinni
 *  "linking external jar files to <IDE> project", tjsp rimpsulla.
 */
import com.google.gson.*;

public class BFS {
	
	/** Kaikki pysakit taulukossa. 
	 * 
	 * 	Pari huomioita pysakkien muodostamasta verkosta: 
	 *  1.) verkko on osittain suunnattu, eli jossain kohdissa pysakkien valin
	 *  paasee kulkemaan vain toiseen suuntaan.
	 *  
	 *  2.) Kahden pysakin valin kaaren paino voi vaihtua riippuen siita kumpaan
	 *  suuntaan se kuljetaan (A->B 2 min, mutta B->A 3 min). 
	 *  (Huom. Kaarten painoja ei tarvita ensimmaisen viikon laskuharjoituksissa.)
	 * */
	Pysakki[] pysakit;
	
	/** Pysakit key:koodi value:olio hakupareina. Oliot ovat samat kuin 
	 *  pysakit-taulukossa. Pysakki p = this.psMap.get(Pysakki.koodi)
	 * */
	HashMap<String, Pysakki> psMap;
	
	/** Konstruktori joka alustaa 'pysakit' ja 'psMap' tietorakenteet. 
	 *  Lukee 'verkko.json' tiedoston ja erittelee seka muuntaa sielta kaikki
	 *  Pysakki-oliot.
	 * 
	 * @param filePath /PATH/TO/verkko.json 
 	 */
	public BFS(String filePath) {
		JsonArray psArr = readJSON(filePath);
		Gson gson = new Gson();
		this.pysakit = new Pysakki[psArr.size()];
		for (int i = 0; i < psArr.size(); i++) {
			this.pysakit[i] = gson.fromJson(psArr.get(i), Pysakki.class);
			System.out.println(this.pysakit[i].koodi + " " + this.pysakit[i].nimi);
		}
		this.psMap = new HashMap<String, Pysakki>();
		for (Pysakki p: pysakit) this.psMap.put(p.koodi, p);
		
		for (Pysakki p: this.pysakit) {
			for (String s: p.naapurit.keySet()) {
				System.out.println(p.koodi +" "+ p.nimi +" -> " + s + " " + this.psMap.get(s).nimi + " " + p.naapurit.get(s) +  " min"); 	
			}
		}

	}
	
	/** Apumetodi, kayta konstruktoria. Lukee annetun tiedoston palautettavaan
	 *  Stringiin.
	 * 
	 * @param filePath path to file.
	 * @return file as string
	 * @throws java.io.IOException
	 */
	private static String readFileAsString(String filePath) throws java.io.IOException { 
		byte[] buffer = new byte[(int) new File(filePath).length()]; 
		BufferedInputStream f = null; 
		
		try { 
			f = new BufferedInputStream(new FileInputStream(filePath)); 
			f.read(buffer); 
		} 
		finally { 
			if (f != null) 
				try { 
					f.close();
				} 
				catch (IOException ignored) { } 
		} 
		return new String(buffer); 
	}
	
	/** Apumetodi, kayta konstruktoria. Parsii annetusta tiedostosta JSON
	 *  taulukon.
	 *
	 * @param filePath tiedostopolku luettavaan tiedostoon
	 * @return JsonArray, joka edustaa tiedostosta luettuja JSON-olioita.
	 */
	private static JsonArray readJSON(String filePath) {
		
		JsonParser parser = new JsonParser();
		String json = "";
		try {
			json = readFileAsString(filePath);
		}
		catch (Exception e) { }
		
		JsonArray arr = parser.parse(json).getAsJsonArray();
		return arr;	
	}
	
	/** Tulostaa annetun reitin R plot komennot. Kutsu tata metodia, ja 
	 * 	copypastea tulostuvat kolmerivia R:n komentoriville sen jalkeen kun 
	 *  olet kirjoittanut source("/PATH/TO/rplot.txt") - komennon ja R on 
	 *  piirtany raitiovaunuverkon uuteen ikkunaan. Valitsemasi reitin tulisi 
	 *  nakya verkon paalla oranssina. 
	 *  
	 *  Vapaavalintainen visualisointityokalu jos haluaa kayttaa.
	 * 
	 * @param x reitin (pysakkien) x-koordinaatit
	 * @param y reitin (pysakkien) y-koordinaatit
	 */
	public void rLine(int[] x, int[] y) {
		String rx = "x <- c(";
		String ry = "y <- c(";
		
		for (int i = 0; i< x.length; i++) {
			rx = rx + x[i] + ", ";
			ry = ry + y[i] + ", ";
		}
		rx = rx.substring(0, rx.length() -2) +")";
		ry = ry.substring(0, ry.length() -2) +")";
		System.out.println(rx);
		System.out.println(ry);
		System.out.println("lines(x,y, lwd = 2, col = \"orange\")");
	}

	
	/** Toteuta leveyssuuntainen haku.
	 *  Pida muistissa omassa hakutilaoliossasi mika oli edeltava tila, josta
	 *  ko. tilaan paastiin. 
	 *  
	 *  Maaliin paastyasi tulosta lapikaytyjen Pysakkien koodit (ja nimet)
	 *  Voit halutessasi myos visualisoida kuljettua reittia rLine-metodin avulla.
	 *  
	 * @param lahto Lahtopysakin koodi
	 * @param maali Maalipysakin koodi
	 */
	public void haku(String lahto, String maali) {
	
	}
	
	public static void main(String[] args) {
		BFS bfs = new BFS("./verkko.json");
		System.out.println("");
		bfs.haku("1250429", "1121480");
	}

}
