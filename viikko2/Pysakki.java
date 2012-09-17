import java.util.HashMap;

public class Pysakki {
	
	/** Pysäkin yksiselitteinen koodi. ID, jota voit käyttää pysäkin 
	 *  tunnistamiseen.
	 */
	public String koodi;
	
	/** Pysäkin osoite. Jotkin osoitteet sisältävät kadun ja numeron, toiset
	 * vain kadun nimen. */
	public String osoite;
	
	/** Pysäkin nimi. Tämä ei välttämättä ole yksiselitteinen. */
	public String nimi;
	
	/** Pysäkin x-koordinaatti. */
	public int x;
	
	/** Pysäkin y-koordinaatti. */
	public int y;
	
	/** Pysäkin naapuripysäkit ja kaikki ko. pysäkille kulkevat linjat (niiden
	 *  yksikäsitteiset koodit). key: Pysakki.koodi, value: Linja.koodi-taulukko. 
	 *  */
	public HashMap<String, String[]> naapurit;

	public Pysakki() {
		this.koodi = "";
		this.osoite = "";
		this.nimi = "";
		this.x = 0;
		this.y = 0;
		this.naapurit = new HashMap<String, String[]>();
	}
	
}
