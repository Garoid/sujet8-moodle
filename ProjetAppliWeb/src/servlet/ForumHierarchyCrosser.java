package servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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
 * C'est ici que la hierarchie du forum va être parcourue afin d'extraire les files de discussion correspondant à
 * l'URL donnée en paramètre
 * @author vincent
 *
 */
public class ForumHierarchyCrosser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String pathToLocalForumFiles		= null;
	private String pathToLocalXmlFile 			= null;
	private ArrayList<String> oldPostsId 		= new ArrayList<String>();
	
	/**
	 * 
	 */
	public ForumHierarchyCrosser() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String adresse = request.getParameter("adresse");
		pathToLocalForumFiles = "/home/vincent/wget";
		pathToLocalXmlFile = this.getClass().getResource("/resources").getPath();
		pathToLocalXmlFile += "/"+getSiteWebName(adresse)+".xml";
		

//		URL forumURL = new URL(adresse);
//		Runtime run = Runtime.getRuntime();
//		String cookiesPath = this.getClass().getResource("/resources/cookies.txt").getPath();
//		File wgetResultFile = new File(pathToLocalForumFiles);
//		run.exec("wget -r -np --load-cookies " +cookiesPath + " " +forumURL, null , wgetResultFile);
//		
//		System.out.println("Wget terminé");
		
		//on récupère la liste des post (leur ID) déjà existant dans le fichier XML s'il existe
		oldPostsId = ExtractTopics.getOldXmlPosts(pathToLocalXmlFile);
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		
		
		try {
			//on vérifie si l'adresse donnée correspond à une liste de forums
			if(! verifySubForumsExistence(adresse)) {
				//si l'adresse pointe sur une liste de discussions
				if(! verifySubDiscussionsExistence(adresse)) {
					//ou finalement sur une discussion
					ExtractTopics.extractalltag(adresse, pathToLocalXmlFile, oldPostsId, false);
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
		
		//si le fichier xml a été créé alors on peut renvoyer les résultats collectés
		File xmlFile = new File(pathToLocalXmlFile);
		if(xmlFile.exists())
			HtmlResponseBuilder.returnResult(xmlFile, out);
	}

	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	/**
	 * Permet de vérifier si l'URL pointe sur un liste de forums et descend dans les sous catégories
	 * si c'est le cas
	 * @param adresse l'URL du forum (ou sous catégorie)
	 * @param out le flux de sortie
	 * @return true si l'adresse pointait bien vers une liste de forums, faux sinon
	 * @throws ParserException
	 */
	private boolean verifySubForumsExistence(String adresse) throws ParserException {
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
					//pour chaque href de forum trouvé on appelle la méthode permettant de descendre jusqu'à la discussion
					if(! verifySubDiscussionsExistence(formatHtmlURL(newURL)))
						/*cas particulier où le forum pointe directement sur une 
						 * file de discussion et non sur une liste de discussions */
						ExtractTopics.extractalltag(formatHtmlURL(newURL), pathToLocalXmlFile, oldPostsId, true);
				}
			}
			return true;
		} 
		return false;
	}
	
	/**
	 * Permet de vérifier si l'URL pointe sur un liste de discussions et descend dans les sous catégories
	 * si c'est le cas
	 * @param adresse l'URL du forum (ou sous catégorie)
	 * @param out le flux de sortie
	 * @return true si l'adresse pointait bien vers une liste de discussions, faux sinon
	 * @throws ParserException
	 */
	private boolean verifySubDiscussionsExistence(String adresse) throws ParserException {
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
					ExtractTopics.extractalltag(formatHtmlURL(hrefSubDiscussion), pathToLocalXmlFile, oldPostsId, false);
				}
			}
			return true;
		} 
		return false;
	}

	/**
	 * Remplace le ? précédent les headers en fin d'adresse par %3f (code HTML)
	 * @param url l'adresse à modifier
	 * @return l'adresse formatée
	 */
	private String formatHtmlURL(String url) {
		String formatedURL = url.replaceFirst("http://", "file://" + pathToLocalForumFiles + "/");
		formatedURL = formatedURL.replaceAll("\\?", "%3F");
		return formatedURL;
	}
	
	/**
	 * Récupère le nom du site web afin de pouvoir créer des fichiers XML au nom unique
	 * et facilement retrouvable
	 * @param adresse l'URL du forum
	 * @return le nom du site web visitée débarassé des www. ou http:// ou file://
	 */
	private String getSiteWebName(String adresse) {
		String res = adresse;
		if(adresse.contains("http://"))
			res = adresse.substring("http://".length());
		if(adresse.contains("file://"+pathToLocalForumFiles+"/"))
			res = adresse.substring(("file://"+pathToLocalForumFiles +"/").length());
		int indexSlash = res.indexOf("/");
		int indexWWW = 0;
		if(res.contains("www."))				
			indexWWW = res.indexOf("www.")+4;
		res = res.substring(indexWWW, indexSlash);
		return res;
	}
}
