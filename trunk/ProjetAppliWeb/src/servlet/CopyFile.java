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
 * Servlet implementation class RenameFile
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
//		System.out.println("adresse:" + adresse);
		adresseFormat = formatHtmlURL(adresse);
//		System.out.println("adresse formaté :" + adresseFormat);
		String adresseFile = adresseFormat.substring(7, adresseFormat
				.lastIndexOf(""));
		String adresseNewFile = adresseFormat.substring(7, adresseFormat
				.lastIndexOf("?"));
//		System.out.println("adresse du file :" + adresseFile);
//		System.out.println("adresse du new File :" + adresseNewFile);
		File file = new File(adresseFile);
		File newFile = new File(adresseNewFile);
//		System.out.println("exsitance du fichier : " + file.exists());
		Boolean res1 = copyFile(adresseFile, adresseNewFile);
		System.out.println("copie reussi : " + res1);

	}

	private String formatHtmlURL(String url) {
		String urlRes = url;
		if (url.contains("%3F")) {
			urlRes = url.replaceAll("%3F", "\\?");
		}
		return urlRes;
	}

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
