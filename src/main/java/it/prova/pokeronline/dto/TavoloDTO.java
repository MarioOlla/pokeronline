package it.prova.pokeronline.dto;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.prova.pokeronline.model.Tavolo;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TavoloDTO {

	private Long id;
	@NotNull(message = "{esperienzaMin.notNull}")
	@Min(value = 0, message = "{esperienzaMin.belowZero}")
	private Integer esperienzaMin;
	@NotNull(message = "{cifraMinima.notNull}")
	@Min(value = 0, message = "{cifraMinima.belowZero}")
	private Integer cifraMinima;
	@NotBlank(message = "{denominazione.notBlank}")
	private String denominazione;
	private LocalDate dataCreazione;
	private UtenteDTO utenteCreazione;
	private Set<UtenteDTO> giocatori = new HashSet<>();

	public TavoloDTO() {

	}

	public TavoloDTO(Integer esperienzaMin, Integer cifraMinima, String denominazione) {
		this.esperienzaMin = esperienzaMin;
		this.cifraMinima = cifraMinima;
		this.denominazione = denominazione;
	}

	public TavoloDTO(Long id, Integer esperienzaMin, Integer cifraMinima, String denominazione, LocalDate dataCreazione,
			UtenteDTO utenteCreazione, Set<UtenteDTO> giocatori) {
		this.id = id;
		this.esperienzaMin = esperienzaMin;
		this.cifraMinima = cifraMinima;
		this.denominazione = denominazione;
		this.dataCreazione = dataCreazione;
		this.utenteCreazione = utenteCreazione;
		this.giocatori = giocatori;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getEsperienzaMin() {
		return esperienzaMin;
	}

	public void setEsperienzaMin(Integer esperienzaMin) {
		this.esperienzaMin = esperienzaMin;
	}

	public Integer getCifraMinima() {
		return cifraMinima;
	}

	public void setCifraMinima(Integer cifraMinima) {
		this.cifraMinima = cifraMinima;
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public LocalDate getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(LocalDate dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public UtenteDTO getUtenteCreazione() {
		return utenteCreazione;
	}

	public void setUtenteCreazione(UtenteDTO utenteCreazione) {
		this.utenteCreazione = utenteCreazione;
	}

	public Set<UtenteDTO> getGiocatori() {
		return giocatori;
	}

	public void setGiocatori(Set<UtenteDTO> giocatori) {
		this.giocatori = giocatori;
	}

	public Tavolo buildTavoloModel() {
		Tavolo result = new Tavolo(this.id, this.esperienzaMin, this.cifraMinima, this.denominazione,
				this.dataCreazione, null);
		if (this.utenteCreazione != null)
			result.setUtenteCreazione(this.utenteCreazione.buildUtenteModel(false));
		if (!this.giocatori.isEmpty()) {
			result.getGiocatori().addAll(this.giocatori.stream().map(player -> {
				return player.buildUtenteModel(false);
			}).collect(Collectors.toSet()));
		}
		return result;
	}

	public static TavoloDTO buildTavoloDtoFromModel(Tavolo model) {
		TavoloDTO result = new TavoloDTO(model.getId(), model.getEsperienzaMin(), model.getCifraMinima(),
				model.getDenominazione(), model.getDataCreazione(),
				UtenteDTO.buildUtenteDTOFromModel(model.getUtenteCreazione()), model.getGiocatori().stream()
				.map(player -> {
					return UtenteDTO.buildUtenteDTOFromModel(player);
				})
				.collect(Collectors.toSet()));
		return result;
	}
}
