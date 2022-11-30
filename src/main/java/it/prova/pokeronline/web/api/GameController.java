package it.prova.pokeronline.web.api;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.pokeronline.dto.TavoloDTO;
import it.prova.pokeronline.dto.UtenteDTO;
import it.prova.pokeronline.model.Tavolo;
import it.prova.pokeronline.service.TavoloService;
import it.prova.pokeronline.service.UtenteService;
import it.prova.pokeronline.web.api.exception.ElementNotFoundException;
import it.prova.pokeronline.web.api.exception.ExperienceRequirementsNotMetException;
import it.prova.pokeronline.web.api.exception.InsufficientCreditException;

@RestController
@RequestMapping("/api/gioco")
public class GameController {
	
	@Autowired
	private UtenteService utenteService;
	
	@Autowired
	private TavoloService tavoloService;
	
	@PutMapping("/compraCredito/{importo}")
	public UtenteDTO compraCredito(@PathVariable(name = "importo",required = true) Integer importo ,Principal principal) {
		return UtenteDTO.buildUtenteDTOFromModel(utenteService.aggiungiCredito(importo, principal.getName()));		
	}
	
	@GetMapping("/lastGame")
	public TavoloDTO lastGame(Principal principal) {
		return TavoloDTO.buildTavoloDtoFromModel(tavoloService.getLastTableOf(principal.getName()));
	}
	
	@PostMapping("/abbandonaPartita")
	@ResponseStatus(HttpStatus.OK)
	public void abbandona(Principal principal) {
		if(this.lastGame(principal) != null) {
			utenteService.incrementaXP(principal.getName());
			tavoloService.abbandonaPartita(principal.getName());
		}
	}
	
	@GetMapping("/listAllJoinable")//FFO
	public List<TavoloDTO> listAllJoinable(Principal principal){
		Tavolo esempio = new Tavolo();
		esempio.setEsperienzaMin(utenteService.findByUsername(principal.getName()).getEsperienzaAccumulata());
		return tavoloService.findByExample(esempio).stream()
				.map(tavolo -> {
					return TavoloDTO.buildTavoloDtoFromModel(tavolo);
				}).collect(Collectors.toList());
	}
	
	@PutMapping("/gioca/{id}")
	public void gioca(@PathVariable(name = "id",required = true) Long id, Principal principal) {
		Tavolo tavolo = tavoloService.findById(id, true);
		if(tavolo==null)
			throw new ElementNotFoundException("Couldn't find Tavolo with id:"+id);
		if(utenteService.findByUsername(principal.getName()).getEsperienzaAccumulata() < tavolo.getEsperienzaMin())
			throw new ExperienceRequirementsNotMetException("This table is out of your league");
		Long timeSeed = new Date().getTime();

		if(timeSeed%37 < 38 ) {
			utenteService.aggiungiCredito(tavolo.getGiocatori().size()*tavolo.getCifraMinima()/2, principal.getName());
			System.out.println(utenteService.findByUsername(principal.getName()).getCreditoAccumulato());
		}else {
			if(utenteService.findByUsername(principal.getName()).getCreditoAccumulato() >= tavolo.getCifraMinima())
				utenteService.aggiungiCredito(-tavolo.getCifraMinima(), principal.getName());
			else {
				throw new InsufficientCreditException("Your credit doesn't cover the match cost");
			}
		}
	}
	
	
}
