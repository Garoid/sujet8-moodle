package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import utils.ExtractTopics;
import utils.HtmlResponseBuilder;

/**
 * Servlet implementation class SubDiscussionChecker
 */
public class ForumHierarchyCrosser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String pathToLocalForumFiles = "/home/max06/";
	private String pathToLocalXmlFile = null;
	
	/**
	 * 
	 */
	public ForumHierarchyCrosser() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String adresse = request.getParameter("adresse");
		pathToLocalXmlFile = this.getClass().getResource("/resources").getPath();
		pathToLocalXmlFile += "/forum.xml";
		File ficXml = new File(pathToLocalXmlFile);
		if(ficXml.exists()){
			ficXml.delete();
		}

//		URL forumURL = new URL(adresse);
//		Runtime run = Runtime.getRuntime();
//		String cookiesPath =
//		this.getClass().getResource("/resources/cookies.txt").getPath();
//		File wgetResultFile = new
//		File(this.getClass().getResource("/resources/wgetExtract").getPath());
//		File wgetResultFile = new File(pathToLocalForumFiles);
//		run.exec(" wget -r -np -l inf --load-cookies " +cookiesPath + " " +forumURL, null , wgetResultFile);
		
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		try {
			if(! verifySubForumsExistence(adresse, out)) {
				if(! verifySubDiscussionsExistence(adresse, out)) {
					ExtractTopics.extractalltag(out, adresse, pathToLocalXmlFile, false);
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		File xmlFile = new File(pathToLocalXmlFile);
		if(xmlFile.exists())
			HtmlResponseBuilder.returnResult(xmlFile, out);
	}

	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private boolean verifySubForumsExistence(String adresse, PrintWriter out) throws ParserException {
		Parser parser = new Parser(adresse);
		AndFilter subForums = new AndFilter(new TagNameFilter("td"), new HasAttributeFilter("class", "cell c0"));
		AndFilter subForumsBis = new AndFilter(new TagNameFilter("td"), new HasAttributeFilter("class", "cell c1"));
		NodeList nl = parser.parse(subForums);
		parser.reset();
		NodeList nlBis = parser.parse(subForumsBis);
		nl.add(nlBis);
		if (nl.size() > 0) {
			for (int i = 0; i < nl.size(); i++) {
				Node child = nl.elementAt(i).getFirstChild();
				if( child instanceof TagNode) {
					String subForumPageName = ((TagNode) child).getAttribute("href");
					//on remplace ce qui suit le dernier "/" de l'adresse par le nom de la nouvelle page
					int index = adresse.lastIndexOf("/");
					String newURL = adresse.substring(0, index+1);
					newURL += subForumPageName;
					if(! verifySubDiscussionsExistence(formatHtmlURL(newURL), out))
						/*cas particulier où le forum pointe directement sur une 
						 * file de discussion et non sur une liste de files de discussion */
						ExtractTopics.extractalltag(out, formatHtmlURL(newURL), pathToLocalXmlFile, true);
				}
			}
			return true;
		} 
		return false;
	}
	
	private boolean verifySubDiscussionsExistence(String adresse, PrintWriter out) throws ParserException {
		Parser parser = new Parser(adresse);
		//on récupère ce tag qui est unique et caractérise une page listant des files de discussion
		AndFilter forumHeaderList = new AndFilter(new TagNameFilter("table"), new HasAttributeFilter("class", "forumheaderlist"));
		NodeList nl = parser.parse(forumHeaderList);
		if (nl.size() > 0) {
			parser.reset();
			AndFilter subDiscussions = new AndFilter(new TagNameFilter("td"), new HasAttributeFilter("class", "topic starter"));
			NodeList topicStarters = parser.parse(subDiscussions);
			if(topicStarters.size() > 0) {
				for (int i = 0; i < topicStarters.size(); i++) {
					TagNode linkNode = (TagNode) topicStarters.elementAt(i).getChildren().elementAt(0);
					String hrefSubDiscussion = linkNode.getAttribute("href");
					ExtractTopics.extractalltag(out, formatHtmlURL(hrefSubDiscussion), pathToLocalXmlFile, false);
				}
			}
			return true;
		} 
		return false;
	}

	private String formatHtmlURL(String url) {
		String formatedURL = url.replaceFirst("http://", "file://" + pathToLocalForumFiles + "/");
		formatedURL = formatedURL.replaceAll("\\?", "%3F");
		return formatedURL;
	}
}
