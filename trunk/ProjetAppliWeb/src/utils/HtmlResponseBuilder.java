package utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import servlet.ForumHierarchyCrosser;

public class HtmlResponseBuilder {
	
	private static int idForum = (int)Math.random() * 1000000;
	private static int idTopic = (int)(Math.random() + 1000000) * 200;
	
	public static void returnResult(File xmlFile, PrintWriter out ) {
		SAXBuilder sxb = new SAXBuilder();
		Document doc = null;;
		try {
			doc = sxb.build(xmlFile);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	out.println("<style type=\"text/css\" media=\"all\">");
		out.println("body { text-align:center; color: black; background-repeat:no-repeat; background-position: top center  }");
		out.println(".listeForum {text-align:center; }");
		out.println(".nomForum{background-color:#CCCCCC; color:black; width:50%; margin:auto; }");
		out.println(".nomTopic{background-color:black; color:white; width:50%; margin:auto; }");
		out.println(".post{color:red; font-weight: bold;}");
		out.println("h1 {text-align:center;}");
		out.println(".rdf{text-align:center; color:red;}");
		out.println("table{border:4px outset black; margin:auto; width:50%;}");
		out.println("td {background-color: #CCCCCC; color:black; border:1px solid red; text-align:left; }");
		out.println("caption {font-size:1.2em; font-weight:bold; margin:auto auto 20px; color:red; }");
		out.println("</style>");
		
		out.println("<h1>Resultat de votre extraction</h1>");
		out.println("<script type=\"text/javascript\" src=\"ajaxExtract.js\"></script>");
		out.println("<div class=\"rdf\">Exporter votre resultat en RDF : </h3><input type=\"button\" value=\"Export XML/RDF\" onclick=\"exportXmlToRdf()\"/></div><br />");
		out.println("<div class=\"listeForum\">");
		Element root = doc.getRootElement();
		List forumElements = root.getChildren("forum");
		for(int i=0; i<forumElements.size(); i++){
			addForum((Element) forumElements.get(i), idForum, out);
			idForum++;
		}
		out.println("</div>");
		

	}
	
	private static void addForum(Element forum, int idForum, PrintWriter out) {
		String forumName = forum.getAttributeValue("nom");
		out.println("<div class=\"forum\">");
		out.println("<p class=\"nomForum\" id=\""+idForum+"\" onclick=\"$('#"+idForum+"').siblings().slideToggle('slow')\">Forum: "+forumName+"</p>");
		List topicElements = forum.getChildren("topic");
		for(int i=0; i<topicElements.size(); i++){
			addTopic((Element) topicElements.get(i),idTopic, out);
			idTopic++;
		}
		out.println("</div>");
	}
	
	private static void addTopic(Element topic, int idTopic,PrintWriter out) {
		String topicName = topic.getAttributeValue("nom");
		out.println("<div class=\"topic\" style=\"display:none\" >");
		out.println("<p class=\"nomTopic\" id=\""+idTopic+"\" onclick=\"$('#"+idTopic+"').siblings().slideToggle('slow')\">Topic: "+topicName+"</p>");
		
		List postElements = topic.getChildren("post");
		out.println("<div class=\"post\" style=\"display:none\" >");
		out.println("<TABLE BORDER=\"1\">");
		out.println("<caption>Tableau des posts</caption>");
    	HashMap<String, String[]> posts = new HashMap<String, String[]>();
		
		for(int i=0; i<postElements.size(); i++){
			String[] tab = addPost((Element) postElements.get(i), out, posts);
			String[] innerTab = {tab[1], tab[2]};
			posts.put(tab[0], innerTab);
		}
		out.println("</table>");
		out.println("</div>");
		out.println("</div>");
	}
	
	private static String[] addPost(Element post, PrintWriter out, HashMap<String, String[]> precPost) {
		String postId = post.getAttributeValue("id");
		String postAuthor = post.getChildTextTrim("auteur");
		String postDate = post.getChildTextTrim("date");
		String postMessage = post.getChildTextTrim("message");
		String postParent = post.getChildTextTrim("parent");
		
		out.println("<table border=\"1\">");
		out.println("<tr>");
		out.println("<tr>");
		out.println("<td>"+postAuthor+" le "+postDate);
		if(! postParent.equals("")){
			String[] infoParent = precPost.get(postParent);
			out.println("<br />");
			out.println("repond au post de " + infoParent[0] + " du " + infoParent[1]);
		}
		out.println("</td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>" + postMessage + "</td>");
		out.println("</tr>");
		out.println("</table>");
		
		
		String[] tab = {postId, postAuthor, postDate};
		return tab;
	}
}
