
import static java.lang.System.out;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.jboss.resteasy.logging.impl.Log4jLogger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;

import examples.cse769.EJB.Entity.TweetWithGeo;
import examples.cse769.EJB.Service.SearchSessionBean;

/**
 * Class SearchServlet.
 */
@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {    
    @EJB
    private SearchSessionBean searchSessionBean;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<TweetWithGeo> result=null;
		String[] locationArray=null;
		String[] keywordArray=null;
		if(request.getParameter("locations")!=null){
			locationArray=(request.getParameter("locations")).toUpperCase().split(",");
		}
		if(request.getParameter("keywords")!=null){
			keywordArray=(request.getParameter("keywords")).toUpperCase().split(",");
		}
		//result is an array list
		result= searchSessionBean.getTweetFromH2(keywordArray, locationArray);
		
		PrintWriter writer = response.getWriter();
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("text/xml");
	    
		String xmlString="";
		try {
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = builderFactory.newDocumentBuilder();
			// creating a new instance of a DOM to build a DOM tree.
			Document doc = docBuilder.newDocument();
			xmlString= new SearchServlet().createXmlTree(doc,result);
		} catch (Exception e) {
			System.out.println(e);
		}
		writer.write(xmlString);
		writer.close();
	}
	
	/* <root>
	 * 		<state name="">
	 * 			<location geoId="" lat="" lon="">
	 * 				<tweet></tweet>
	 * 			</location>
	 * 		</state>
	 * </root>*/
	public String createXmlTree (Document doc, List<TweetWithGeo> result) throws Exception {
		//This method creates an element node; Element implements Node
		out.println("the size is "+result.size());
		Element root = doc.createElement("root");
		//adding a node after the last child node of the specified node.
		//out.println("1");
		doc.appendChild(root);
		if(result ==null){
			return getStringFromDoc(doc);
		}
		//out.println("2");
		XPath xpath = XPathFactory.newInstance().newXPath();

		XPathExpression exp;
		Object exprResult;
		NodeList nodeList;
		
		out.println(result.get(0).getClass());
		
		for(int i=0;i<result.size();i++){		//to here
			TweetWithGeo a=result.get(i);
			out.println(a.getStname());
			exp = xpath.compile("//state[@name=\""+a.getStname()+"\"]");
			exprResult = exp.evaluate(doc, XPathConstants.NODESET);
			nodeList = (NodeList) exprResult;
			if(nodeList.getLength()>0){			//if state exists
				Node curState=nodeList.item(0);
				exp=xpath.compile("//location[@geoId=\""+a.getGeo_id()+"\"]");
				exprResult = exp.evaluate(curState, XPathConstants.NODESET);
				nodeList = (NodeList) exprResult;
				if(nodeList.getLength()>0){		//if location exists
					Node curLocation=nodeList.item(0);
					Element newTweet=doc.createElement("tweet");
					newTweet.appendChild(doc.createTextNode(a.getText()));
					curLocation.appendChild(newTweet);
				}else{
					Element newTweet=doc.createElement("tweet");
					newTweet.appendChild(doc.createTextNode(a.getText()));
					
					Element newLocation=doc.createElement("location");
					newLocation.setAttribute("geoId", Integer.toString(a.getGeo_id()));
					newLocation.setAttribute("city", a.getCity());
					newLocation.setAttribute("lat", Double.toString(a.getLat()));
					newLocation.setAttribute("lon", Double.toString(a.getLon()));
					
					newLocation.appendChild(newTweet);
					curState.appendChild(newLocation);
				}
			}else{
				Element newState=doc.createElement("state");
				newState.setAttribute("name", a.getStname());
				
				Element newTweet=doc.createElement("tweet");
				newTweet.appendChild(doc.createTextNode(a.getText()));
				
				Element newLocation=doc.createElement("location");
				newLocation.setAttribute("geoId", Integer.toString(a.getGeo_id()));
				newLocation.setAttribute("city", a.getCity());
				newLocation.setAttribute("lat", Double.toString(a.getLat()));
				newLocation.setAttribute("lon", Double.toString(a.getLon()));
				
				newLocation.appendChild(newTweet);
				newState.appendChild(newLocation);
				root.appendChild(newState);
			}
		}
		return getStringFromDoc(doc);
	}
	
	public String getStringFromDoc(Document doc)    {
	    DOMImplementationLS domImplementation = (DOMImplementationLS) doc.getImplementation();
	    LSSerializer lsSerializer = domImplementation.createLSSerializer();
	    return lsSerializer.writeToString(doc);   
	}

}
