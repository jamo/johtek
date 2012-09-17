public class Linja {

	/** Yksiselitteinen linjan koodi (eri molempiin suuntiin). Käytä tätä 
	 * tunnistaakseni linja ja sen suunta yksikäsitteisesti. */
	public String koodi;
	
	/** Linjan lyhyt koodi (esim. 4, 3T, jne.), sama molempiin suuntiin. */
	public String koodiLyhyt;
	
	/** Linjan nimi, esim. Kaivopuisto - Kallio - Eläintarha. */
	public String nimi;
	
	/** Linjan kaikkien pysäkkien x-koordinaatit taulukkona. */
	public int[] x;
	
	/** Linjan kaikkien pysäkkien y-koordinaatit taulukkona. */
	public int[] y;
	
	/** Linjan pysäkkien yksiselitteiset koodit taulukkona. */
	public String[] psKoodit;
	
	/** Ajat, jolloin linja on pysäkeillä lähdän jälkeen taulukkona. */
	public int[] psAjat;
	
	public Linja() {
		this.koodi = "";
		this.koodiLyhyt = "";
		this.nimi = "";
		this.x = null;
		this.y = null;
		this.psKoodit = null;
		this.psAjat = null;	
	}
}
