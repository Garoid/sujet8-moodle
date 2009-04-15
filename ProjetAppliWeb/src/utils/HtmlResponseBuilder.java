package utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

public class HtmlResponseBuilder {
	
	public static void returnResult(File xmlFile, PrintWriter out) {
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
		Element root = doc.getRootElement();
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\">");
		out.println("<html mlns=\"http://www.w3.org/1999/xhtml\" dir=\"ltr\" lang=\"fr-FR\">");
		out.println("<head>");
		out.println("<script src=\"http://code.jquery.com/jquery-latest.js\"></script>");
		
		out.println("<script>");
		out.println("$(document).ready(function(){");
		out.println("$(\"div\").click(function () {");
		out.println("var $div=$(this);");
		out.println("$(\"$div>div\")).is(\":hidden\")) {");
		out.println("$(\"$div>div\")).slideDown(\"slow\");");
		out.println("} else {");
		out.println("$(\"$div>div\")).hide();");
		out.println("}");
		out.println("});");
		out.println("}); </script>");
		
		out.println("</head>");
		out.println("<body>");
		
		List forumElements = root.getChildren("forum");
		for(int i=0; i<forumElements.size(); i++){
			addForum((Element) forumElements.get(i), out);
		}
		
		out.println("</body>");
		out.println("</html>");
	}
	
	private static void addForum(Element forum, PrintWriter out) {
		String forumName = forum.getAttributeValue("name");
		out.println("<div class=\"forum\">");
		out.println(forumName);
		
		List topicElements = forum.getChildren("topic");
		for(int i=0; i<topicElements.size(); i++){
			addTopic((Element) topicElements.get(i), out);
		}
		out.println("</div>");
	}
	
	private static void addTopic(Element topic, PrintWriter out) {
		String topicName = topic.getAttributeValue("nom");
		out.println("<div class=\"topic\" style=\"display:none\">");
		out.println(topicName);
		
		List postElements = topic.getChildren("post");
		for(int i=0; i<postElements.size(); i++){
			addPost((Element) postElements.get(i), out);
		}
		out.println("</div>");
	}
	
	private static void addPost(Element post, PrintWriter out) {
		String postId = post.getAttributeValue("id");
		
		String postAuthor = post.getChild("auteur").getTextTrim();
		String postDate = post.getChild("date").getTextTrim();
		String postMessage = post.getChild("message").getTextTrim();
		
		out.println("<div class=\"post\" id=\"" +postId +"\" style=\"display:none\">");
		out.println("<table border=\"1\">");
		out.println("<tr>");
		out.println(postAuthor +", le " +postDate);
		out.println("</tr>");
		out.println("<tr>");
		out.println(postMessage);
		out.println("</tr>");
		out.println("</table>");
		out.println("</div>");
		
	}
}
