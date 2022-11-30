package it.prova.pokeronline.repository.tavolo;

import java.util.List;

import it.prova.pokeronline.model.Tavolo;

public interface CustomTavoloRepository {
	
	public List<Tavolo> findByExample(Tavolo example);
		
	
}
