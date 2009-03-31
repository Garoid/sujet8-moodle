package utils;

public class Post {

	private String id;
	private String sujet;
	private String auteur;
	private String message;
	private String date;
	private String parent;

	public Post() {
	}

	public Post(String id, String sujet, String auteur, String message,
			String date, String parent) {
		super();
		this.id = id;
		this.sujet = sujet;
		this.auteur = auteur;
		this.message = message;
		this.date = date;
		this.parent = parent;
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

	public String getSujet() {
		return sujet;
	}

	public void setSujet(String sujet) {
		this.sujet = sujet;
	}

}
