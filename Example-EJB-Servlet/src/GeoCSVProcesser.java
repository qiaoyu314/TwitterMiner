import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GeoCSVProcesser {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {
		BufferedReader inStream = null;
		PrintWriter outStream = null;
		try {
			inStream = new BufferedReader(new FileReader("geo.txt"));
			outStream = new PrintWriter(new BufferedWriter(new FileWriter(
					"geo_output.txt")));
			String line;
			inStream.readLine();		//skip the header line
			outStream.println("GEO_ID\tREGION\tDIVISION\tSTATEFP\tSTATENS\tSTUSPS" +
					"\tNAME\tLAT\tLON\tLAT,LON\tID\tADDRESS\tSTREET\tCITY\tCOUNTRY");
			//print the header line in the output file
			String country;
			String city;
			String street;
			while ((line = inStream.readLine()) != null) {
				//GEO_ID	REGION	DIVISION	STATEFP	STATENS	STUSPS	NAME	LAT	LON	LAT,LON	ID	ADDRESS
				line=line.replaceAll("\"", "");
				line=line.replaceAll(", ", ",");
				String[] values=line.split("\t");	
				String[] addresses=values[11].split(",");
				country="";
				city="";
				street="";
				country=addresses[addresses.length-1];
				if(addresses.length>=4){
					city=addresses[addresses.length-3];
					for(int i=0;i<addresses.length-3;i++){
						street+=addresses[i];
					}
					//System.out.println(country+"\t"+zip+"\t"+city+"\t"+street);
				}else if(addresses.length==3){
					city=addresses[0];
				}else{
					city=addresses[0];
				}
				String out_line=line;
				out_line+="\t"+street+"\t"+city+"\t"+country;
				System.out.println(out_line);
				outStream.println(out_line);
			}
			System.out.print("Done!");
		}
		finally {
			if (inStream != null) {
				inStream.close();
			}
			if (outStream != null) {
				outStream.close();
			}
		}
	}
}
