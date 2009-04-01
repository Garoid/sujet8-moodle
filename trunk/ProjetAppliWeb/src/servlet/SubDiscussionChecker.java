package servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import utils.ExtractTopics;

/**
 * Servlet implementation class SubDiscussionChecker
 */
public class SubDiscussionChecker extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String pathToLocalForumFiles = "/home/max06";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubDiscussionChecker() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String adresse = request.getParameter("adresse"); 
		try {
			Parser parser = new Parser(adresse);
			AndFilter discussionsFilter = new AndFilter(new TagNameFilter("table"), new HasAttributeFilter("class", "forumheaderlist"));
			NodeList list = parser.parse(discussionsFilter);
			if(list.size() > 0) {
				getAndExtractSubDiscussions(adresse, response.getWriter());
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	
	private void getAndExtractSubDiscussions(String adresse, PrintWriter out) throws ParserException, IOException {
		Parser parser = new Parser(adresse);
		AndFilter topicFilter = new AndFilter(new TagNameFilter("td"), new HasAttributeFilter("class", "topic starter"));
		NodeList topicsList = parser.parse(topicFilter);
		if(topicsList.size() > 0) {
			for (int i = 0; i < topicsList.size(); i++) {
				TagNode hrefChild = (TagNode) topicsList.elementAt(i).getChildren().elementAt(0);
				String href = hrefChild.getAttribute("href");
				href = href.replaceFirst("http://", "file://" + pathToLocalForumFiles + "/");
				href = href.replaceAll("\\?","%3F");
				System.out.println(href);
				ExtractTopics.extractalltag(out, href);
				out.println("-----------------------------------------");
			}
		}
	}
}
