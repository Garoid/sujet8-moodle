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
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

/**
 * Servlet implementation class XmlToRdf
 */
public class XmlToRdf extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final String pathToLocalXmlFile = "/home/max06/forum.xml";
	private final String pathToLocalRdfFile = "/home/max06/forumRdf.xml";
	private final String pathToLocalXslFile = this.getClass().getResource(
			"/resources/forum.xsl").getPath();;

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
		exportXmlToRdf(pathToLocalXmlFile, pathToLocalXslFile,
				pathToLocalRdfFile, out);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private void exportXmlToRdf(String rootPath, String fichierXSL,
			String outputFilePath, PrintWriter out) {
		JDOMResult documentJDOMSortie = new JDOMResult();
		Document resultat = null;
		Document document = null;
		SAXBuilder sxb = new SAXBuilder();
		File file = new File(rootPath);
		if (!file.exists()) {
			out.println("La création du fichier RDF ne peut pas se faire car Le fichier XML n'existe pas.");
		} else {
			try {
				document = sxb.build(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer = factory
						.newTransformer(new StreamSource(fichierXSL));
				transformer.transform(new JDOMSource(document),
						documentJDOMSortie);
				resultat = documentJDOMSortie.getDocument();

				XMLOutputter outputter = new XMLOutputter(Format
						.getPrettyFormat());
				outputter
						.output(resultat, new FileOutputStream(outputFilePath));
				out.println("Votre fichier RDF a été créé.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
