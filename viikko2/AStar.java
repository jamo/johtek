 import java.util.HashMap;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/** Lataa ja avaa gson-2.2.2.zip osoitteesta 
 *  http://google-gson.googlecode.com/files/google-gson-2.2.2-release.zip
 *  
 *  Komentoriviltä kääntäessäsi kutsu 
 *  javac -cp /PATH/TO/google-gson-2.2.2.jar BFS.java
 *  
 *  Jos käytät IDEä, niin hae googlella miten saat sen omaan IDEesi kiinni
 *  "linking external jar files to <IDE> project", tjsp rimpsulla.
 */
import com.google.gson.*;

public class AStar {
	
	/** Kaikki pysäkit taulukossa. 
	 *  
	 *  Verkko on osittain suunnattu, eli jossain kohdissa pysäkkien välin
	 *  pääsee kulkemaan vain toiseen suuntaan.
	 *  */
	Pysakki[] pysakit;
	
	/** Pysäkit key:koodi value:olio hakupareina. Oliot ovat samat kuin 
	 *  pysakit-taulukossa. Pysakki p = this.psMap.get(Pysakki.koodi)
	 * */
	HashMap<String, Pysakki> psMap;
	
	/**	Kaikki linjat taulukossa.
	 * 
	 * 	Linjan molemmat suunnat ovat erikseen taulukossa ja ne ovat erotetta-
	 *  vissa Linja.koodi:lla. 
	 */
	Linja[] linjat;
	
	/** Linjat key:koodi value:olio hakupareina. Oliot ovat samat kuin 
	 *  linjat-taulukossa. 
	 */
	HashMap<String, Linja> lnMap;
	
	/** Konstruktori joka alustaa 'pysakit', 'psMap', 'linjat' sekä 'lnMap' 
	 *  tietorakenteet. Lukee 'verkko.json' sekä 'linjat.json' tiedostot, 
	 *  erittelee sekä muuntaa niistä kaikki Pysakki- ja Linja-oliot.
	 * 
	 * @param verkkoPolku /PATH/TO/verkko.json 
	 * @param linjaPolku /PATH/TO/linjat.json
 	 */
	public AStar(String verkkoPolku, String linjaPolku) {
		Gson gson = new Gson();
		
		// Luetaan pysäkit verkko.json tiedostosta.
		JsonArray psArr = readJSON(verkkoPolku);
		this.pysakit = new Pysakki[psArr.size()];
		for (int i = 0; i < psArr.size(); i++) {
			this.pysakit[i] = gson.fromJson(psArr.get(i), Pysakki.class);
		}
		this.psMap = new HashMap<String, Pysakki>();
		for (Pysakki p: pysakit) this.psMap.put(p.koodi, p);
		
		/*
		for (Pysakki p: this.pysakit) {
			for (String s: p.naapurit.keySet()) {
				String ls = "";
				for (String c: p.naapurit.get(s)) ls = ls + c + " ";
				System.out.println(p.koodi +" "+ p.nimi +" -> " + s + " " + this.psMap.get(s).nimi + " " + ls); 	
			}
		}
		*/
		
		// Luetaan raitiovaunulinjat linjat.json tiedostosta.
		JsonArray lnArr = readJSON(linjaPolku);
		this.linjat = new Linja[lnArr.size()];
		
		for (int i = 0; i < lnArr.size(); i++) {
			this.linjat[i] = gson.fromJson(lnArr.get(i), Linja.class);
		}
		this.lnMap = new HashMap<String, Linja>();
		for (Linja l: this.linjat) this.lnMap.put(l.koodi, l);
	}
	
	/** Apumetodi, käytä konstruktoria. Lukee annetun tiedoston palautettavaan
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
	
	/** Apumetodi, käytä konstruktoria. Parsii annetusta tiedostosta JSON
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
	
	/** Tulostaa annetun reitin R:n piirtokomennot. Kutsu tätä metodia, ja 
	 * 	copypastea tulostuvat kolmeriviä R:n komentoriville sen jälkeen kun 
	 *  olet kirjoittanut source("/PATH/TO/rplot.txt") - komennon ja R on 
	 *  piirtänyt raitiovaunuverkon uuteen ikkunaan. Valitsemasi reitin tulisi 
	 *  näkyä verkon päällä oranssina. 
	 *  
	 *  Vapaavalintainen visualisointi työkalu jos haluaa käyttää.
	 * 
	 * @param x reitin (pysäkkien) x-koordinaatit
	 * @param y reitin (pysäkkien) y-koordinaatit
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

	
	/** Toteuta reittiopas A*-algoritmin avulla.
	 * 
	 *  Viime viikon leveyssuuntaiseen hakuun lisätään nyt raitiovaunulinjat, 
	 *  joiden avulla kuljetaan pysäkiltä toiselle. Pysäkki-oliot tietävät 
	 *  viereiset pysäkin ja kaikki niille matkustavat linjat. Linja-oliot 
	 *  tietävät kunkin pysäkin välisen ajan (voi vaihdella linjakohtaisesti). 
	 *  Mielikuvitusmaailmassamme kaikki raitiovaunulinjat lähtevät 10 minuutin 
	 *  välein linjan ensimmäiseltä pysäkiltä ja matkustaminen lähtöpysäkiltä 
	 *  aloitetaan 0-9 minuutin kohdalla. 
	 * 
	 * 	Leveyssuuntaisesta hausta poiketen tarvitset nyt pitää muistissa omassa 
	 *  hakutila-oliossasi ainakin:
	 *  	- hakutila, josta tähän tilaan on tultu 
	 *  	- aika, joka on jo kulunut lähdästä
	 *  	- kellon aika joka tällä hetkellä on (kulunut aika + lähtäaika)
	 *  	- heuristinen arvio kauan tästä hakutilasta kuluu aikaa maaliin
	 *  	  (voit olettaa maksiminopeuden olevan 526 koordinaattipistettä
	 *  	  minuutissa)
	 *  
	 *  Lisäksi reitin tulostusta varten on hyädyllistä muistaa:
	 *  	- millä linjalla tähän tilaan tultiin
	 *  	- kauan odotettiin viime pysäkillä
	 *  	- kauan matkustettiin viime pysäkiltä
	 *  
	 *  Järjestä käytävät tilat kuluneen ajan + heuristisen arvion perusteella
	 *  nopein ensimmäiseksi.
	 *  
	 *  Maaliin päästyäsi tulosta reitin jokaisen Pysäkin koodi, nimi
	 *  ja aika jolloin sillä pysäkillä ollaan sekä millä linjalla pysäkkien 
	 *  väli matkustetaan (odotusaika ei ole välttämätän). Leveyssuuntaisen 
	 *  haun tapaan voit halutessasi visualisoida kuljettua reittiä 
	 *  rLine-metodin avulla.
	 *  
	 *  Vinkki: käytä hakutila-listauksessa PriorityQueue:ta ja toteuta omalle
	 *  hakutila-oliollesi Comparable<hakutila>-rajapinta.
	 *  
	 *  http://docs.oracle.com/javase/6/docs/api/java/util/PriorityQueue.html
	 *  http://docs.oracle.com/javase/6/docs/api/java/lang/Comparable.html 
	 * 
	 * @param lahto Lähtöpysäkin koodi
	 * @param maali Maalipysäkin koodi
	 * @param aika  Lähtöaika (voit olettaa sen olevan 0-9 minuuttia) 
	 */
	public void haku(String lahto, String maali, int aika) {
	
	}
	
	public static void main(String[] args) {
		AStar astar = new AStar("./src/verkko.json", "./src/linjat.json");
		System.out.println("");
		astar.haku("1250429", "1121480", 2);
	}

}
