package servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CopyFile
 * Servlet permettant la copie d'un fichier en local
 * @author Benayoun Vincent, Checconi Maxime
 * @version 1.0
 */
public class CopyFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CopyFile() {
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
		String adresse = request.getParameter("adresse");
		String adresseFormat = null;
		adresseFormat = formatURL(adresse);
		String adresseFile = null;
		String adresseNewFile = null;
		
		if (adresseFormat.contains("file:///")) {
			adresseFile = adresseFormat.substring(7, adresseFormat
					.lastIndexOf(""));
			adresseNewFile = adresseFormat.substring(7, adresseFormat
					.lastIndexOf("?"));
		} else if (adresseFormat.contains("http://")) {
			if (adresseFormat.contains("www.")) {
				adresseFile = adresseFormat.substring(11, adresseFormat
						.lastIndexOf(""));
				adresseNewFile = adresseFormat.substring(11, adresseFormat
						.lastIndexOf("?"));
				adresseFile = "/" + adresseFile;
				adresseNewFile = "/" + adresseNewFile;

			} else {

				adresseFile = adresseFormat.substring(6, adresseFormat
						.lastIndexOf(""));
				adresseNewFile = adresseFormat.substring(6, adresseFormat
						.lastIndexOf("?"));
			}
		} else {
			if (adresseFormat.contains("www.")) {
				adresseFile = adresseFormat.substring(4, adresseFormat
						.lastIndexOf(""));
				adresseNewFile = adresseFormat.substring(4, adresseFormat
						.lastIndexOf("?"));
				adresseFile = "/" + adresseFile;
				adresseNewFile = "/" + adresseNewFile;
			}
		}

		File file = new File(adresseFile);
		File newFile = new File(adresseNewFile);
		System.out.println("exsitance du fichier : " + file.exists());
		Boolean res1 = copyFile(adresseFile, adresseNewFile);
		System.out.println("copie reussi : " + res1);

	}

	private String formatURL(String url) {
		String urlRes = url;
		if (url.contains("%3F")) {
			urlRes = url.replaceAll("%3F", "\\?");
		}
		return urlRes;
	}

	/**
	 * Copie un fichier.
	 * @param source Source de la copie
	 * @param destination Destination de la copie 
	 * @return vrai(true) si la copie reussi ou faux(false) si la copie echoue
	 */
	private Boolean copyFile(String source, String destination) {
		FileChannel in = null; // canal d'entrée
		FileChannel out = null; // canal de sortie

		try {
			// Init
			in = new FileInputStream(source).getChannel();
			out = new FileOutputStream(destination).getChannel();

			// Copie depuis le in vers le out
			in.transferTo(0, in.size(), out);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} finally { // finalement on ferme
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}