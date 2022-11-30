package it.prova.pokeronline.web.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.UtenteDTO;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.security.dto.UtenteInfoJWTResponseDTO;
import it.prova.pokeronline.service.UtenteService;
import it.prova.pokeronline.web.api.exception.ElementNotFoundException;
import it.prova.pokeronline.web.api.exception.IdNotNullForInsertException;

@RestController
@RequestMapping("/api/utente")
public class UtenteController {

	@Autowired
	private UtenteService utenteService;

	@PutMapping("/cambiaStato/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void cambiaStato(@PathVariable(name = "id",required = true) Long id) {
		utenteService.changeUserAbilitation(id);
	}

	@GetMapping(value = "/userInfo")
	public ResponseEntity<UtenteInfoJWTResponseDTO> getUserInfo() {

		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		Utente utenteLoggato = utenteService.findByUsername(username);
		List<String> ruoli = utenteLoggato.getRuoli().stream().map(item -> item.getCodice())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new UtenteInfoJWTResponseDTO(utenteLoggato.getNome(), utenteLoggato.getCognome(),
				utenteLoggato.getUsername(), utenteLoggato.getEmail(), ruoli));
	}
	
	@GetMapping
	public List<UtenteDTO> listAll() {
		return utenteService.listAllUtenti().stream()
				.map(utente -> UtenteDTO.buildUtenteDTOFromModel(utente))
				.collect(Collectors.toList());
	}
	
	@GetMapping("/{id}")
	public UtenteDTO caricaSingolo(@PathVariable(name = "id",required = true) Long id) {
		return UtenteDTO.buildUtenteDTOFromModel(utenteService.caricaSingoloUtente(id));
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public UtenteDTO inserisciNuovoUtente(@Valid@RequestBody UtenteDTO input) {
		Utente daInserire = input.buildUtenteModel(true);
		
		if(daInserire.getId()!=null)
			throw new IdNotNullForInsertException("Id must be null before insertion.");
		
		UtenteDTO inserito = UtenteDTO.buildUtenteDTOFromModel(utenteService.inserisciNuovo(daInserire));
		return inserito;
	}
	
	@PutMapping("/{id}")
	public UtenteDTO aggiornaUtente(@Valid@RequestBody UtenteDTO input,@PathVariable(name = "id",required = true) Long id) {
		if(utenteService.caricaSingoloUtente(id)==null)
			throw new ElementNotFoundException("Couldn't find Utente with id:"+id);
		Utente aggiornato = input.buildUtenteModel(true);
		return UtenteDTO.buildUtenteDTOFromModel(utenteService.aggiorna(aggiornato));
	}
	
	@GetMapping("/search")
	public List<UtenteDTO> ricerca(@Valid@RequestBody UtenteDTO input){
		return utenteService.findByExample(input.buildUtenteModel(true)).stream()
				.map(utente -> {
					return UtenteDTO.buildUtenteDTOFromModel(utente);
				})
				.collect(Collectors.toList());
	}
}
