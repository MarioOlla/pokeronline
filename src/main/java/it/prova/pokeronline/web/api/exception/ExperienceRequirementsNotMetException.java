package it.prova.pokeronline.web.api.exception;

public class ExperienceRequirementsNotMetException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public ExperienceRequirementsNotMetException(String msg) {
		super(msg);
	}
}
