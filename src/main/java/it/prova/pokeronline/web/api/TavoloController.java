package it.prova.pokeronline.web.api;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.model.Utente;
import it.prova.pokeronline.service.TavoloService;
import it.prova.pokeronline.service.UtenteService;
import it.prova.pokeronline.web.api.exception.ExperienceRequirementsNotMetException;
import it.prova.pokeronline.web.api.exception.IdNotNullForInsertException;
import it.prova.pokeronline.web.api.exception.NotTavoloCreatorException;
import it.prova.pokeronline.web.api.exception.TavoloNotAllowedException;

@RestController
@RequestMapping("/api/tavolo")
public class TavoloController {
	
	@Autowired
	private UtenteService utenteService;
	
	@Autowired
	private TavoloService tavoloService;
	
	@GetMapping("/listAll")//permesso solo agli admin
	public List<TavoloDTO> listAll(){	
		return tavoloService.listAll(false).stream()
				.map(tavolo -> {
					return TavoloDTO.buildTavoloDtoFromModel(tavolo);
				}).collect(Collectors.toList());
	}
	
	@GetMapping("/listMine")//permesso ad admin e special players
	public List<TavoloDTO> listMine(Principal principal){
		return tavoloService.listMine(false, principal.getName()).stream()
				.map(tavolo -> {
					return TavoloDTO.buildTavoloDtoFromModel(tavolo);
				}).collect(Collectors.toList());
	}
	
	@GetMapping("/{id}")//FFO
	public TavoloDTO caricaTavolo(@PathVariable(name = "id",required = true) Long id,Principal principal) {
		Utente current = utenteService.findByUsername(principal.getName());
		if(current.isAdmin()) {
			return TavoloDTO.buildTavoloDtoFromModel(tavoloService.findById(id, false));
		}
		Tavolo tavolo = tavoloService.findById(id, false);
		if(tavolo.getEsperienzaMin() <= current .getEsperienzaAccumulata()) {
			return TavoloDTO.buildTavoloDtoFromModel(tavolo);
		}
		throw new ExperienceRequirementsNotMetException("Your xp level is too low for this tavolo");
		
	}
	
	@GetMapping("/search")//FFO
	public List<TavoloDTO> ricercaTavolo(@RequestBody TavoloDTO input, Principal principal){
		Utente current = utenteService.findByUsername(principal.getName());
		Tavolo example = input.buildTavoloModel();
		if(current.isAdmin()) {
			return tavoloService.findByExample(example).stream()
					.map(tavolo -> {
						return TavoloDTO.buildTavoloDtoFromModel(tavolo);
					}).collect(Collectors.toList());
		}else {
			if(example.getEsperienzaMin() <= current.getEsperienzaAccumulata() || example.getUtenteCreazione().getUsername().equals(principal.getName()))
				return tavoloService.findByExample(example).stream()
						.map(tavolo -> {
							return TavoloDTO.buildTavoloDtoFromModel(tavolo);
						}).collect(Collectors.toList());
			throw new TavoloNotAllowedException("you do not have access to this tavolo");
		}
	}
	
	@PostMapping//permesso ad admin e special players
	@ResponseStatus(HttpStatus.CREATED)
	public TavoloDTO inserisciTavolo(@Valid @RequestBody TavoloDTO input,Principal principal) {
		Tavolo daInserire = input.buildTavoloModel();
		
		if(daInserire.getId()!=null)
			throw new IdNotNullForInsertException("Id must be null before insertion.");
		
		TavoloDTO inserito = TavoloDTO.buildTavoloDtoFromModel(tavoloService.inserisciNuovo(daInserire,principal.getName()));
		return inserito;
	}
	
	@PutMapping("/{id}")//permesso ad admin e special players
	public TavoloDTO aggiornaTavolo(@PathVariable(name = "id",required = true) Long id,@Valid@RequestBody TavoloDTO input, Principal principal) {
		Utente current = utenteService.findByUsername(principal.getName());
		input.setId(id);
		if(current.isAdmin())
			return TavoloDTO.buildTavoloDtoFromModel(tavoloService.aggiorna(input.buildTavoloModel(),principal.getName()));
		else {
			if(current.getId() == input.buildTavoloModel().getId())
				return TavoloDTO.buildTavoloDtoFromModel(tavoloService.aggiorna(input.buildTavoloModel(),principal.getName()));
			throw new NotTavoloCreatorException("Cannot update a table that is not yours.");
		}
	}
	
	@DeleteMapping("/{id}")//permesso ad admin e special players
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void rimuoviTavolo(@PathVariable(name = "id",required = true) Long id) {
		tavoloService.rimuovi(id);
 	}
}
