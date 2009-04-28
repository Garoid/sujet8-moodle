package servlet;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.developpez.adiguba.shell.Shell;

/**
 * Cette servlet servait au départ à extraire les fichiers du forum vers un répertoire local
 * en utilisant une l'API Shell.jar mais le problème étant le même qu'avec le wget de base
 * elle a été abandonnée
 */
public class ExtractToLocal extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private final String localForumDirectory = "/home/vincent/wget"; 

    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExtractToLocal() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String adresse = request.getParameter("adresse");
		String cookiesPath = this.getClass().getResource("/resources/cookies.txt").getPath();
		
		Shell sh = new Shell();
		try {
			sh.setDirectory(new File(localForumDirectory));
			sh.command("wget -r -np --load-cookies " +cookiesPath +" " +adresse).consume();
			System.out.println(sh.getDirectory().getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
