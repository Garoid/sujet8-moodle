package utils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.HasSiblingFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class ExtractTopics {
	
	public static void extractalltag(PrintWriter out, String adresse) {
		Parser parser = null;
		AndFilter sujetFiltre = new AndFilter(new TagNameFilter("div"),new HasAttributeFilter("class", "subject"));
		AndFilter idFrere = new AndFilter(new TagNameFilter("table"),
				new HasAttributeFilter("class", "forumpost"));
		AndFilter idFiltre = new AndFilter(new TagNameFilter("a"),
				new HasSiblingFilter(idFrere));
		AndFilter messageFiltre = new AndFilter(new TagNameFilter("td"),
				new HasAttributeFilter("class", "content"));
		AndFilter divAuteur = new AndFilter(new TagNameFilter("div"),
				new HasAttributeFilter("class", "author"));
		AndFilter auteurFiltre = new AndFilter(new TagNameFilter("a"),
				new HasParentFilter(divAuteur));
		AndFilter divCommands = new AndFilter(new TagNameFilter("div"),
				new HasAttributeFilter("class", "commands"));
		AndFilter parentFiltre = new AndFilter(new TagNameFilter("a"),
				new HasParentFilter(divCommands));

		try {
			parser = new Parser(adresse);

			// parser.setResource("file:///home/max06/localhost/moodle/mod/forum/discuss.php%3Fd=2");
		} catch (ParserException e1) {
			e1.printStackTrace();
		}
		try {
			List<Post> res = new ArrayList<Post>();
			NodeList list = parser.parse(sujetFiltre);
			for (int i = 0; i < list.size(); i++) {
				Node tmp = list.elementAt(i);
				String sujet = extractTextNode(tmp);
				parser.reset();

				list = parser.parse(messageFiltre);
				Node tmp2 = list.elementAt(i);
				String message = extractNodeToHtml(tmp2);
				parser.reset();

				list = parser.parse(auteurFiltre);
				Node tmp3 = list.elementAt(i);
				String auteur = extractLinkTagText(tmp3);
				parser.reset();

				list = parser.parse(divAuteur);
				Node tmp4 = list.elementAt(i);
				String date = extractDate(tmp4);
				parser.reset();

				list = parser.parse(idFiltre);
				Node tmp5 = list.elementAt(i);
				String id = extractLinkTagAttribute(tmp5, "id");
				parser.reset();
				
				String parent = null;
//				if (i != 0) {
//					list = parser.parse(parentFiltre);
//					Node tmp6 = list.elementAt(i);
//					String linkParent = extractLinkParent(tmp6, out);
////				parent = extractIdParent(linkParent);
//					parser.reset();
//				}

				list = parser.parse(sujetFiltre);
				Post p = new Post(id, sujet, auteur, message, date, parent);
				res.add(p);
			}
			 showAllPost(res, out);
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	public static String extractLinkParent(Node node, PrintWriter out)
			throws ParserException {
		if (node instanceof TagNode) {
			TagNode tag = (TagNode) node;
			if (tag instanceof LinkTag) {
				LinkTag link = (LinkTag) tag;
				NodeList nl = link.getChildren();
				if (nl != null) {
					for (NodeIterator i = nl.elements(); i.hasMoreNodes();) {
						Node tmp = i.nextNode();
						if (tmp.getText().equals("Show parent")) {
							String res = link.getLink();
							return res;
						}
					}
				}
			}
		}
		return null;
	}

	public static String extractLinkTagAttribute(Node node, String attribute)
			throws ParserException {
		if (node instanceof TagNode) {

			TagNode tag = (TagNode) node;
			String tagName = tag.getTagName();
			if (tag instanceof LinkTag) {
				LinkTag link = (LinkTag) tag;
				return link.getAttribute("id");
			}
			return null;
		}
		return null;
	}

	public static String extractLinkTagText(Node node) throws ParserException {
		if (node instanceof TagNode) {

			TagNode tag = (TagNode) node;
			String tagName = tag.getTagName();
			if (tag instanceof LinkTag) {
				LinkTag link = (LinkTag) tag;
				return link.getLinkText();
			}
			return null;
		}
		return null;
	}

	public static String extractNodeToHtml(Node node) throws ParserException {
		if (node instanceof TextNode) {

			TextNode text = (TextNode) node;
			String el = text.toHtml();
			return el;
		} else if (node instanceof TagNode) {

			TagNode tag = (TagNode) node;
			if (tag.getAttribute("class") != null
					&& tag.getAttribute("class").equals("commands")) {
				return null;
			} else {
				NodeList nl = tag.getChildren();
				String res = "";
				if (null != nl)
					for (NodeIterator i = nl.elements(); i.hasMoreNodes();) {
						Node n = i.nextNode();
						if (n instanceof TagNode) {
							TagNode tmp = (TagNode) n;
							if (tmp.getAttribute("class") != null
									&& tmp.getAttribute("class").equals(
											"commands")) {
								break;
							}
						}
						res += n.toHtml();
					}
				return res;
			}
		}
		return null;
	}

	public static String extractTextNode(Node node) throws ParserException {
		if (node instanceof TextNode) {

			TextNode text = (TextNode) node;
			return text.getText();
		} else if (node instanceof TagNode) {

			TagNode tag = (TagNode) node;
			NodeList nl = tag.getChildren();
			if (null != nl)
				for (NodeIterator i = nl.elements(); i.hasMoreNodes();) {
					if (!(tag.getAttribute("class")).equals("commands")) {
						return extractTextNode(i.nextNode());
					}
				}

		}
		return null;
	}

	public static void showAllPost(List l, PrintWriter out) {
		for (Post p : (ArrayList<Post>) l) {
			out.println("Id: " + p.getId());
			out.println("Auteur: " + p.getAuteur());
			out.println("Date: " + p.getDate());
			out.println("topic: " + p.getSujet());
			out.println("message: " + p.getMessage());
			out.println("parent: " + p.getParent());
			out.println();
		}
	}

	public static String extractDate(Node auteur) {
		String date = null;
		String ligne = auteur.toPlainTextString();
		String[] res = ligne.split("-");
		date = res[1];
		return date;
	}

	public static String extractIdParent(String linkParent) {
		String idParent = null;
		String[] res = linkParent.split("#");
		idParent = res[1];
		return idParent;
	}

}
