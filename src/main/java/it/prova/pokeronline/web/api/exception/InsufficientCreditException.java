package it.prova.pokeronline.web.api.exception;

public class InsufficientCreditException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public InsufficientCreditException(String msg) {
		super(msg);
	}
}
