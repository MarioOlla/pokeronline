package it.prova.pokeronline.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.pokeronline.model.StatoUtente;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.repository.utente.UtenteRepository;
import it.prova.pokeronline.web.api.exception.ElementNotFoundException;

@Service
@Transactional(readOnly = true)
public class UtenteServiceImpl implements UtenteService {
	
	@Value("${utente.password.reset.value}") 
	private String defaultPassword;
	
	@Autowired
	private UtenteRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Utente> listAllUtenti() {
		return (List<Utente>) repository.findAll();
	}

	public Utente caricaSingoloUtente(Long id) {
		return repository.findById(id).orElse(null);
	}

	public Utente caricaSingoloUtenteConRuoli(Long id) {
		return repository.findByIdConRuoli(id).orElse(null);
	}

	@Transactional
	public Utente aggiorna(Utente utenteInstance) {
		// deve aggiornare solo nome, cognome, username, ruoli
		Utente utenteReloaded = repository.findById(utenteInstance.getId()).orElse(null);
		if (utenteReloaded == null)
			throw new RuntimeException("Elemento non trovato");
		utenteReloaded.setNome(utenteInstance.getNome());
		utenteReloaded.setCognome(utenteInstance.getCognome());
		utenteReloaded.setUsername(utenteInstance.getUsername());
		utenteReloaded.setRuoli(utenteInstance.getRuoli());
		utenteReloaded.setEsperienzaAccumulata(utenteInstance.getEsperienzaAccumulata());
		utenteReloaded.setCreditoAccumulato(utenteInstance.getCreditoAccumulato());
		return repository.save(utenteReloaded);
	}

	@Transactional
	public Utente inserisciNuovo(Utente utenteInstance) {
		utenteInstance.setStato(StatoUtente.CREATO);
		utenteInstance.setPassword(passwordEncoder.encode(utenteInstance.getPassword()));
		utenteInstance.setCreditoAccumulato(0);
		utenteInstance.setEsperienzaAccumulata(0);
		utenteInstance.setDateCreated(LocalDate.now());
		return repository.save(utenteInstance);
	}

	@Transactional
	public void rimuovi(Long idToRemove) {
		repository.deleteById(idToRemove);
	}

	public Utente eseguiAccesso(String username, String password) {
		return repository.findByUsernameAndPasswordAndStato(username, password, StatoUtente.ATTIVO);
	}

	public Utente findByUsernameAndPassword(String username, String password) {
		return repository.findByUsernameAndPassword(username, password);
	}

	@Transactional
	public void changeUserAbilitation(Long utenteInstanceId) {
		Utente utenteInstance = caricaSingoloUtente(utenteInstanceId);
		if (utenteInstance == null)
			throw new ElementNotFoundException("Couldn't find Utente with id:"+utenteInstanceId);

		if (utenteInstance.getStato() == null || utenteInstance.getStato().equals(StatoUtente.CREATO))
			utenteInstance.setStato(StatoUtente.ATTIVO);
		else if (utenteInstance.getStato().equals(StatoUtente.ATTIVO))
			utenteInstance.setStato(StatoUtente.DISABILITATO);
		else if (utenteInstance.getStato().equals(StatoUtente.DISABILITATO))
			utenteInstance.setStato(StatoUtente.ATTIVO);
	}

	public Utente findByUsername(String username) {
		return repository.findByUsername(username).orElse(null);
	}

	@Override
	public Utente inserisciNuovo(Utente utenteInstance, StatoUtente stato) {
		utenteInstance.setStato(stato);
		utenteInstance.setPassword(passwordEncoder.encode(utenteInstance.getPassword())); 
		utenteInstance.setDateCreated(LocalDate.now());
		return repository.save(utenteInstance);
		
	}

	@Override
	public List<Utente> findByExample(Utente example) {
		return repository.findByExample(example);
	}

	@Override
	public void cambiaPassword(String confermaNuovaPassword, String name) {
		Utente utente = repository.findByUsername(name).orElse(null);
		utente.setPassword(passwordEncoder.encode(confermaNuovaPassword));
		repository.save(utente);
		
	}

	@Override
	public void cambiaPassword(Long idUtente) {

		Utente utente = repository.findById(idUtente).orElse(null);
		utente.setPassword(passwordEncoder.encode(defaultPassword));
		repository.save(utente);
		
	}

	@Override
	public Utente aggiungiCredito(Integer importo, String username) {
		Utente current = this.findByUsername(username);
		current.setCreditoAccumulato(current.getCreditoAccumulato()+importo);
		System.out.println(current.getCreditoAccumulato());
		return repository.save(current);
	}

	@Override
	public void incrementaXP(String username) {
		Utente current = this.findByUsername(username);
		current.setEsperienzaAccumulata(current.getEsperienzaAccumulata()+1);
		repository.save(current);
	}

}
