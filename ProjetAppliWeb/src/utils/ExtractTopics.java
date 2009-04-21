package utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
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
import org.jdom.filter.ElementFilter;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMSource;

public class ExtractTopics {

	private static String nomForum = null;
	private static String topicName = null;
	
	public static void extractalltag(PrintWriter out, String adresse,String filePath, ArrayList<String> oldPostsId, boolean forumIsTopic) {
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
			Node tmp = list.elementAt(0);
			topicName = extractTextNode(tmp);
			
			boolean postListNull = (oldPostsId == null);
			for (int i = 0; i < list.size(); i++) {
				
				parser.reset();
				list = parser.parse(idFiltre);
				Node tmp5 = list.elementAt(i);
				String id = extractLinkTagAttribute(tmp5, "id");
				parser.reset();
				
				if(postListNull || (! oldPostsId.contains(id))) {

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

					list = parser.parse(divCommands);
					Node tmp6 = list.elementAt(i);
					String linkParent = extractLinkParent(tmp6.getFirstChild());
					String parent = extractIdParent(linkParent);
					parser.reset();
					
					String idPostPrec = null;
					if(i>0){
						list = parser.parse(idFiltre);
						Node tmp7 = list.elementAt(i-1);
						idPostPrec = extractLinkTagAttribute(tmp7, "id");
						parser.reset();
					}

					list = parser.parse(idFiltre);
					Post p = new Post(id, auteur, message, date, parent, idPostPrec);
					res.add(p);
				}
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
			if(res.size() > 0)
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

	private static void writeAllPostToXml(List<Post> newPost, String filePath) {
		File file = new File(filePath);
		Document document = null;
		Element racine = null;
		Element forum = null;
		List<Element> listChild = null;
		boolean forumExist = false;
		boolean topicExist = false;

		SAXBuilder sxb = new SAXBuilder();
		
		if (!file.exists()) {
			racine = new Element("moodle");
			document = new Document(racine);
			
		} else {
			try {
				document = sxb.build(new File(filePath));
				racine = document.getRootElement();
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		listChild = racine.getChildren("forum");
		for (int i = 0; i < listChild.size(); i++) {
			if (((Element) listChild.get(i)).getAttributeValue("nom").equals(nomForum)) {
				forumExist = true;
				forum = ((Element) listChild.get(i));
				break;
			}
		}
		Element topic = null;
		if (!forumExist) {
			forum = new Element("forum");
			Attribute nomFor = new Attribute("nom", nomForum);
			forum.setAttribute(nomFor);
			topic = new Element("topic");
			topic.setAttribute("nom", topicName);
			forum.addContent(topic);
			racine.addContent(forum);
		} else {
			//on récupère l'élement topic s'il existe déjà
			listChild = forum.getChildren("topic");
			for (int i = 0; i < listChild.size(); i++) {
				if (((Element) listChild.get(i)).getAttributeValue("nom").equals(topicName)) {
					topicExist = true;
					topic = ((Element) listChild.get(i));
					break;
				}
			}
			if (!topicExist) {
				topic = new Element("topic");
				topic.setAttribute("nom", topicName);
				forum.addContent(topic);
			}
		}
		for (Post p : (ArrayList<Post>)newPost) {
			Element post = createPostElement(p);
			if((!forumExist) || (!topicExist) || (p.getIdPostPrec() == null))
				topic.addContent(post);
			else {				
				topic.addContent(getPrecPostIndex(topic, p.getIdPostPrec())+1, post);
			}
		}
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
	
	private static Element createPostElement(Post p) {
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
		
		return post;
	}
	
	public static ArrayList<String> getOldXmlPosts(String xmlFilePath) {
		File file = new File(xmlFilePath);
		if(! file.exists())
			return null;
		SAXBuilder sxb = new SAXBuilder();
		Document doc = null;
		try {
			doc = sxb.build(file);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element racine = doc.getRootElement();
		ArrayList<String> oldPosts = new ArrayList<String>();
		Iterator<Element> it = racine.getDescendants(new ElementFilter("post"));
		if(! it.hasNext())
			return null;
		while(it.hasNext()){
			Element post = (Element) it.next();
			String postId = post.getAttributeValue("id");
			oldPosts.add(postId);
		}
		return oldPosts;
	}
	
	private static int getPrecPostIndex(Element topic, String idPrecPost) {
		List<Element> topicChildren = topic.getChildren("post");
		for(int i=0; i<topicChildren.size(); i++) {
			String currentId = topicChildren.get(i).getAttribute("id").getValue();
			//on cherche l'index du post precedent avec son ID
			if(currentId.equals(idPrecPost))
				return i;
		}
		return -1;
	}

}
