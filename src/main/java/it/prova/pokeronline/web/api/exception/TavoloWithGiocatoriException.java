package it.prova.pokeronline.web.api.exception;

public class TavoloWithGiocatoriException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	
	public TavoloWithGiocatoriException(String msg) {
		super(msg);
	}
}
