package it.prova.pokeronline.service;

import java.util.List;

import it.prova.pokeronline.model.Tavolo;

public interface TavoloService {

	public List<Tavolo> listAll(boolean eager);

	public List<Tavolo> listMine(boolean eager, String username);

	public Tavolo findById(Long id, boolean eager);

	public Tavolo inserisciNuovo(Tavolo input, String username);

	public Tavolo aggiorna(Tavolo input, String username);

	public void rimuovi(Long id);

	public List<Tavolo> findByExample(Tavolo example);

	public Tavolo getLastTableOf(String name);

	public void abbandonaPartita(String username);

}
