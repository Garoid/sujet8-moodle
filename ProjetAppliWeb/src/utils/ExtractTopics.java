package utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;

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
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;

public class ExtractTopics {

	private static String nomForum = null;

	public static void extractalltag(PrintWriter out, String adresse,
			String filePath, boolean forumIsTopic ) {
		Parser parser = null;
		AndFilter sujetFiltre = new AndFilter(new TagNameFilter("div"),
				new HasAttributeFilter("class", "subject"));
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
		AndFilter nomForumFiltre = new AndFilter(new TagNameFilter("li"),
				new HasAttributeFilter("class", "first"));

		try {
			parser = new Parser(adresse);
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

				list = parser.parse(divCommands);
				Node tmp6 = list.elementAt(i);
				String linkParent = extractLinkParent(tmp6.getFirstChild());
				String parent = extractIdParent(linkParent);
				parser.reset();

				list = parser.parse(sujetFiltre);
				Post p = new Post(id, sujet, auteur, message, date, parent);
				res.add(p);
			}
			parser.reset();
			list = parser.parse(nomForumFiltre);
			if(forumIsTopic){
				nomForum = extractNomForum(
						list.elementAt(list.size() - 1).toPlainTextString()).trim();
			}
			else{
				nomForum = extractNomForum(
						list.elementAt(list.size() - 2).toPlainTextString()).trim();
			}
			// showAllPost(res, out);
			writeAllPostToXml(res, filePath);
		} catch (ParserException e) {
			e.printStackTrace();
		}
	}

	private static String extractLinkParent(Node node) throws ParserException {
		if (node instanceof TagNode) {
			TagNode tag = (TagNode) node;
			if (tag instanceof LinkTag) {
				LinkTag link = (LinkTag) tag;
				Node firstChild = link.getFirstChild();
				if (firstChild.toPlainTextString().equals("Show parent")) {
					String res = link.getLink();
					return res;
				}
				return null;
			}
		}
		return null;
	}

	private static String extractLinkTagAttribute(Node node, String attribute)
			throws ParserException {
		if (node instanceof TagNode) {

			TagNode tag = (TagNode) node;
			if (tag instanceof LinkTag) {
				LinkTag link = (LinkTag) tag;
				return link.getAttribute("id");
			}
			return null;
		}
		return null;
	}

	private static String extractLinkTagText(Node node) throws ParserException {
		if (node instanceof TagNode) {

			TagNode tag = (TagNode) node;
			if (tag instanceof LinkTag) {
				LinkTag link = (LinkTag) tag;
				return link.getLinkText();
			}
			return null;
		}
		return null;
	}

	private static String extractNodeToHtml(Node node) throws ParserException {
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

	private static String extractTextNode(Node node) throws ParserException {
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

	private static void showAllPost(List<Post> l, PrintWriter out) {
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

	private static void writeAllPostToXml(List<Post> l, String filePath) {
		File file = new File(filePath);
		Document document = null;
		Element racine = null;
		Element forum = null;
		List<Element> listChild = null;
		Boolean nomForumExist = false;

		SAXBuilder sxb = new SAXBuilder();

		if (!file.exists()) {
			racine = new Element("moodle");
			document = new Document(racine);

		} else {
			try {
				document = sxb.build(new File(filePath));
				racine = document.getRootElement();
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Element topic = new Element("topic");
		listChild = racine.getChildren("forum");
		for (int i = 0; i < listChild.size(); i++) {
			if (((Element) listChild.get(i)).getAttributeValue("name").equals(
					nomForum)) {
				nomForumExist = true;
				forum = ((Element) listChild.get(i));
				forum.addContent(topic);
				break;
			}
		}
		if (!nomForumExist) {
			forum = new Element("forum");
			Attribute nomFor = new Attribute("name", nomForum);
			forum.setAttribute(nomFor);
			forum.addContent(topic);
			racine.addContent(forum);
		}
		for (Post p : (ArrayList<Post>) l) {
			Element post = new Element("post");
			Attribute id = new Attribute("id", p.getId());
			post.setAttribute(id);

			Element auteur = new Element("auteur");
			auteur.setText(p.getAuteur());
			post.addContent(auteur);

			Element date = new Element("date");
			date.setText(p.getDate());
			post.addContent(date);

			Element message = new Element("message");
			message.setText(p.getMessage());
			post.addContent(message);

			Element parent = new Element("parent");
			parent.setText(p.getParent());
			post.addContent(parent);

			topic.addContent(post);

		}
		Attribute nomTopic = new Attribute("nom", ((Post) l.get(0)).getSujet());
		topic.setAttribute(nomTopic);
		// racine.addContent(forum);
		JDOMSource source = new JDOMSource(document);
		StreamResult result = new StreamResult(filePath);
		Transformer transformer = null;
		try {
			transformer = TransformerFactory.newInstance().newTransformer();
			transformer.transform(source, result);
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private static String extractDate(Node auteur) {
		String date = null;
		String ligne = auteur.toPlainTextString();
		String[] res = ligne.split("-");
		date = res[1];
		return date;
	}

	private static String extractIdParent(String linkParent) {
		if (linkParent != null) {
			String idParent = null;
			String[] res = linkParent.split("#");
			idParent = res[1];
			return idParent;
		} else
			return null;
	}

	private static String extractNomForum(String link) {
		if (link != null) {
			String nomForum = null;
			String[] res = link.split(";");
			nomForum = res[2];
			return nomForum;
		} else {
			return null;
		}
	}

}
