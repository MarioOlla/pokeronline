package it.prova.pokeronline.web.api.exception;

public class TavoloNotAllowedException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public TavoloNotAllowedException(String msg) {
		super(msg);
	}
}
