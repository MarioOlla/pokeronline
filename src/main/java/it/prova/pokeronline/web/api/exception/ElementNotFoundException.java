package it.prova.pokeronline.web.api.exception;

public class ElementNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ElementNotFoundException(String msg) {
		super(msg);
	}
}
