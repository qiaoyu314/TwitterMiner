import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;  
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;  
import java.io.PrintWriter;
import java.net.URL;  
import java.net.URLEncoder;

import org.json.JSONArray;  
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author Wei Chen
 *
 */
public class TwitterDownloader {
	private static Connection conn = null;
	private static Statement stmt = null;
	private static ResultSet rs = null; 
    private static List<Integer> skippedGeoIDList=new ArrayList<Integer>();
	private static final Logger logger =Logger.getLogger(TwitterDownloader.class.getName());
	
	public static List<Coordinates> loadCoords() throws IOException{
		BufferedReader inStream=null;
		List<Coordinates> coords=new ArrayList<Coordinates>();
		try{
			String line;
			inStream=new BufferedReader(new FileReader("fishnet_label.csv"));
			inStream.readLine();		//skip the header line
			while((line=inStream.readLine())!=null){
				String[] values=line.split(",");
				coords.add(new Coordinates(Integer.parseInt(values[0]), 
						Float.parseFloat(values[1]), Float.parseFloat(values[2])));
			}
		}finally {
			if (inStream != null) {
				inStream.close();
			}
		}
		return coords;
	}
	public static String findSinceId(){
		String sinceId="0";
		try{
			rs = stmt.executeQuery("SELECT MAX(ID) AS since_id FROM TWEET");
			rs.next();
		    if(rs.getString("since_id")!=null){
		    	sinceId=rs.getString("since_id");
		    }
		}catch (Exception e) {  
            e.printStackTrace();  
        } 
		//out.print(sinceId);
		return sinceId;
	}
	@SuppressWarnings("deprecation")
	public static void updateDB(Coordinates coord,JSONArray tweets){
        JSONObject tweet;  
        try {  
    		//construct string
    		String q="INSERT INTO TWEET VALUES('";
            for(int i=0;i<tweets.length();i++) {  
            	if(i>0){
            		q+=",('";
            	}
                tweet = tweets.getJSONObject(i);  
                q+=tweet.getString("id_str")+"','"
                		+tweet.getString("created_at")+"','"
                		+tweet.getString("from_user_id_str").replaceAll("'", "''")+"','"
                		+tweet.getString("from_user").replaceAll("'", "''")+"','"
                		+tweet.getString("from_user_name").replaceAll("'", "''")+"','"
                		+tweet.getString("to_user_id_str")+"','"
                		+tweet.getString("profile_image_url")+"','"
                		+tweet.getString("iso_language_code")+"','"
                		+tweet.getString("source")+"','"
                		+tweet.getString("text").replaceAll("'", "''")+"',"
                		+coord.getId()+")";
                /*q+=URLEncoder.encode(tweet.getString("id_str"))+"','"
                		+URLEncoder.encode(tweet.getString("created_at"))+"','"
                		+URLEncoder.encode(tweet.getString("from_user_id_str"))+"','"
                		+URLEncoder.encode(tweet.getString("from_user"))+"','"
                		+URLEncoder.encode(tweet.getString("from_user_name"))+"','"
                		+URLEncoder.encode(tweet.getString("to_user_id_str"))+"','"
                		+URLEncoder.encode(tweet.getString("profile_image_url"))+"','"
                		+URLEncoder.encode(tweet.getString("iso_language_code"))+"','"
                		+URLEncoder.encode(tweet.getString("source"))+"','"
                		+URLEncoder.encode(tweet.getString("text"))+"',"
                		+coord.getId()+")";*/
            }  
            //out.println(q);
            stmt.execute(q);  
            out.println("# of rows added:"+ stmt.getUpdateCount()+"\n");  
            
        } catch (Exception e) {  
            e.printStackTrace();  
        }
	}
	
	@SuppressWarnings("finally")
	public static JSONArray getTweetsFromTwitter(Coordinates coord) throws JSONException, IOException, InterruptedException{
		String sinceId=findSinceId();
		//BMW OR Mercedes OR Audi OR Chrysler OR Ford OR General Motors OR Honda OR Toyota OR Nissan
		//obama OR barackobama
		String qStr="" +
				"&geocode="+Float.toString(coord.getLat())+","+Float.toString(coord.getLon())+","+"30km"+
				"&since_id="+sinceId;
		String urlStr = "http://search.twitter.com/search.json?lang=en&rpp=100&result_type=mixed&q=" + qStr;
		//out.println(urlStr);
		JSONArray tweets=null;
		//System.exit(0);
		try{
			 	URL url = new URL(urlStr);  
		        BufferedReader br = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));  
		        int c;
		        StringBuffer sb = new StringBuffer();  
		        while((c=br.read())!=-1){  
		        	sb.append((char)c);  
		        }  
		        br.close();  
		        //out.print(sb.toString());
		        JSONObject js = new JSONObject(sb.toString());  
		        tweets = js.getJSONArray("results");  
		        //out.print(tweets.length());
		}catch (Exception e) {  
            e.printStackTrace();
            Thread.sleep(10000);
            out.println("Server rejected. Sleep for 10 seconds...");
        }finally{
        	return tweets;
        }
	}
	
	public static int getDbSize() throws SQLException{
		int size=0;
		rs=stmt.executeQuery("SELECT COUNT(*) AS COUNT FROM TWEET");
		rs.next();
	    if(rs.getString("COUNT")!=null){
	    	size= Integer.parseInt(rs.getString("COUNT"));
	    }
	    return size;
	}
	public static String getCurrentTime(){
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static void deleteOldest100Rows() throws SQLException{
		try {
			stmt.executeQuery("DELETE FROM tweet WHERE id in (SELECT TOP 100 id from tweet ORDER BY id)");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args) throws Exception {
		out.println("Updating H2 twitterdb..."+getCurrentTime());
		Class.forName("org.h2.Driver");	
		conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/twitterdb","", "");
		stmt=conn.createStatement();
		//get all 2503 coordinates form a file
		List<Coordinates> coords=loadCoords();
		out.println("Reading location data from fishnet_label.csv\n" +
				"Total number of sampling locations:"+coords.size());
		BufferedReader inStream = null;
		PrintWriter outStream = null;
		try {
			out.println("Initially load skipped sampling location from previous run");
			inStream = new BufferedReader(new FileReader("skippedGeoID.txt"));
			int id=0;
			inStream.readLine();		//skip the header line
			while ((id = inStream.read()) != -1) {
				skippedGeoIDList.add(id);
			}
			inStream.close();
			outStream = new PrintWriter(new BufferedWriter(new FileWriter(
					"skippedGeoID.txt")));
		}finally{
		}
		final int maxTableSize=10000;

		while (true) {
			if(getDbSize() > maxTableSize){
				deleteOldest100Rows();
			}
			for (Coordinates coord : coords) {
				out.println("Sampling tweets from location "+coord.getId()+"/2503");
				if (skippedGeoIDList.contains(Integer.valueOf(coord.getId()))) {
					continue;
				} else {
					JSONArray tweets = getTweetsFromTwitter(coord);
					if (tweets != null && tweets.length() > 0) {
						updateDB(coord, tweets);
					} else {
						//logger.log(Level.FINEST, String.valueOf(coord.getId()));
						out.println("No tweets currently are available from location "+Integer.valueOf(coord.getId())+"\n");
						outStream.println(coord.getId());
					}
				}
				Thread.sleep(2000);
			}
			out.println("Wait 60s to sample 2503 locations again.");
			Thread.sleep(60*1000);
		}
/*		if (conn != null) {
			conn.close();
		}
		if (stmt != null) {
			stmt.close();
		}*/

		//out.print("done!");
	}

}
