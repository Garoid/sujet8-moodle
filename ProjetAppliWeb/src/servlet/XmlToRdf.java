package servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

/**
 * Servlet implementation class XmlToRdf
 * Servlet permettant de générer un fichier rdf à partir d'un fichier xml
 * @author Benayoun Vincent, Checconi Maxime
 * @version 1.0
 */
public class XmlToRdf extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String pathToLocalXmlFile = this.getClass().getResource(
			"/resources/localhost.xml").getPath();
	private final String pathToLocalRdfFile = this.getClass().getResource(
			"/resources").getPath()
			+ "localhostRdf.xml";
	private final String pathToLocalXslFile = this.getClass().getResource(
			"/resources/forum.xsl").getPath();

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public XmlToRdf() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		exportXmlToRdf(pathToLocalXmlFile, pathToLocalXslFile, pathToLocalRdfFile, out);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * Permet d'exporter un fichier xml en rdf en appliquant un fichier xsl sur le fichier xml
	 * @param rootPath Fichier xml que l'on souhaite exporter au format rdf
	 * @param fichierXSL Feuille de style xsl que l'on va appliquer sur le fichier xml
	 * @param outPutFilePath Fichier xml généré au format rdf 
	 * @param out PrintWriter permettant d'ecrire dans la response 
	 */
	private void exportXmlToRdf(String rootPath, String fichierXSL,
			String outputFilePath, PrintWriter out) {
		JDOMResult documentJDOMSortie = new JDOMResult();
		Document resultat = null;
		Document document = null;
		SAXBuilder sxb = new SAXBuilder();
		File file = new File(rootPath);
		if (!file.exists()) {
			out
					.println("La création du fichier RDF ne peut pas se faire car Le fichier XML n'existe pas.");
		} else {
			try {
				document = sxb.build(file);
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory.newTransformer(new StreamSource(fichierXSL));
				transformer.transform(new JDOMSource(document),documentJDOMSortie);
				resultat = documentJDOMSortie.getDocument();
				XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
				outputter.output(resultat, new FileOutputStream(outputFilePath));
				out.println("Votre fichier RDF a ete cree.");
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}

		}
	}

}