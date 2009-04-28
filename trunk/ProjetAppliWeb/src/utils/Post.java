package utils;

/**
 * Classe modélisant un Post
 * @author Benayoun Vincent, Checconi Maxime
 * @version 1.0
 */
public class Post {

	private String id;
	private String auteur;
	private String message;
	private String date;
	private String parent;
	private String idPostPrec;
	
	public Post() {
	}

	public Post(String id, String auteur, String message,
			String date, String parent, String idPostPrec) {
		super();
		this.id = id;
		this.auteur = auteur;
		this.message = message;
		this.date = date;
		this.parent = parent;
		this.idPostPrec = idPostPrec;
	}

	public String getId() {
		return id;
	}

	public String getAuteur() {
		return auteur;
	}

	public String getMessage() {
		return message;
	}

	public String getDate() {
		return date;
	}

	public String getParent() {
		return parent;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setAutheur(String auteur) {
		this.auteur = auteur;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getIdPostPrec() {
		return idPostPrec;
	}

}