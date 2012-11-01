package examples.cse769.EJB.Service;
import java.util.List;
import java.math.BigInteger;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import static java.lang.System.out;
//import examples.cse769.EJB.Entity.Geo;
//import examples.cse769.EJB.Entity.Tweet;
import examples.cse769.EJB.Entity.TweetWithGeo;

/**
 * This bean has two methods to implement searching functions
 * @author Yu Qiao, Wei Chen and Igor
 *
 */

@Stateless
public class SearchSessionBean {
	//(Optional) The name of the persistence unit as defined in the persistence.xml file.
	@PersistenceContext(unitName="examples-769-EJB")
	EntityManager em;
	
	public Integer getTweetFromH2(String [] keyword, String [] location, int option){
		String query=constructQuery(keyword,location,1);
		return ((BigInteger)em.createNativeQuery(query).getSingleResult()).intValue();
	}
	/**
	 * @param keyword
	 * @param location
	 * @return
	 */
	public List<TweetWithGeo> getTweetFromH2(String [] keyword, String [] location){
		//List<TweetWithGeo> result = null;
		String query=constructQuery(keyword,location,2);
		return em.createNativeQuery(query,examples.cse769.EJB.Entity.TweetWithGeo.class).getResultList();

		//out.println("12345");
		//out.println(result.get(0).getCity());

	}
	
	private static String constructQuery(String [] keyword, String [] location, int option){
		String query=null;
		String inClauseLocation = null;
		String regExpClause = null;
		if(option==1){
			query="SELECT count(*) FROM TWEET_GEO_VIEW ";
		}else if(option==2){
			query="SELECT * FROM TWEET_GEO_VIEW ";
		}
		if (location != null){
			inClauseLocation = " ('"+location[0]+"'";
			if(location.length>0){
				int i;
				for(i=1;i<location.length;i++){
					inClauseLocation += "," + "'" + location[i] + "'";
				}
			}			
			inClauseLocation += ") ";			
			
			
		}

		if(keyword != null)
		{
			regExpClause = "UPPER(text) "+sqlRegexp(keyword[0]);
			if(keyword.length>0){
				int i;
				for(i=1;i<keyword.length;i++){
					regExpClause += " OR UPPER(text) " + sqlRegexp(keyword[i]);
				}
			}					
		}
		//only use location to search
		if(regExpClause == null){
			query += "where UPPER(stusps) in" + inClauseLocation + "or UPPER(stname) in" + inClauseLocation;
		}
		//only use keyword to search 
		else if (inClauseLocation == null){
			query += "where " + regExpClause;
		}
		//use both keyword and location to search
		else{
			query += "where (UPPER(stusps) in" + inClauseLocation + "or UPPER(stname) in" + inClauseLocation
					+ ") and (" + regExpClause + ")"; 
		}
		return query;
	}
	
	public static String sqlRegexp(String s){
		return "REGEXP '[^a-z]"+s+"[^a-z]'";
	}

}
