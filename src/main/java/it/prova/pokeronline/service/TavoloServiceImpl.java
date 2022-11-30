package it.prova.pokeronline.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.repository.tavolo.TavoloRepository;
import it.prova.pokeronline.repository.utente.UtenteRepository;
import it.prova.pokeronline.web.api.exception.ElementNotFoundException;
import it.prova.pokeronline.web.api.exception.TavoloWithGiocatoriException;

@Service
@Transactional
public class TavoloServiceImpl implements TavoloService {
	
	@Autowired
	private TavoloRepository tavoloRepository;
	
	@Autowired
	private UtenteRepository utenteRepository;
	
	@Override
	public List<Tavolo> listAll(boolean eager) {
		if(eager)
			return tavoloRepository.listAllEager();
		return (List<Tavolo>) tavoloRepository.findAll();
	}

	@Override
	public List<Tavolo> listMine(boolean eager, String username) {
		if(eager) 
			return tavoloRepository.listMineEager(username);
		return tavoloRepository.listMine(username);
	}

	@Override
	public Tavolo findById( Long id, boolean eager) {
		if(eager)
			return tavoloRepository.findByIdEager(id);
		return tavoloRepository.findById(id).orElse(null);
	}

	@Override
	public Tavolo inserisciNuovo(Tavolo input, String username) {
		Utente utenteCreatore = utenteRepository.findByUsername(username).orElse(null);
		input.setDataCreazione(LocalDate.now());
		input.setUtenteCreazione(utenteCreatore);
		return tavoloRepository.save(input);
	}

	@Override
	public Tavolo aggiorna(Tavolo input, String username) {
		Tavolo daAggiornare = tavoloRepository.findById(input.getId()).orElse(null);
		if(daAggiornare==null) {
			throw new ElementNotFoundException("Couldn't find a tavolo with id:"+input.getId());
		}
		if(input.getUtenteCreazione()== null)
			input.setUtenteCreazione(daAggiornare.getUtenteCreazione());
		return tavoloRepository.save(input);
	}

	@Override
	public void rimuovi(Long id) {
		Tavolo toBeDeleted = tavoloRepository.findByIdEager(id);
		if(!toBeDeleted.getGiocatori().isEmpty()) 
			throw new TavoloWithGiocatoriException("There are still players for this table");
		tavoloRepository.deleteById(id);
		
	}

	@Override
	public List<Tavolo> findByExample(Tavolo example) {
		return tavoloRepository.findByExample(example);
	}

	@Override
	public Tavolo getLastTableOf(String name) {
		return tavoloRepository.getLastTable(name);
	}

	@Override
	public void abbandonaPartita(String username) {
		Set<Utente> giocatori = getLastTableOf(username).getGiocatori();
		Utente toBeRemoved = utenteRepository.findByUsername(username).orElse(null);
		giocatori.stream().forEach(utente -> {
			if(utente.getUsername().equals(username)) 
				giocatori.remove(toBeRemoved);
		});
	}

}
