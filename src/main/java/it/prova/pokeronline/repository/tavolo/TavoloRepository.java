package it.prova.pokeronline.repository.tavolo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import it.prova.pokeronline.model.Tavolo;

public interface TavoloRepository extends CrudRepository<Tavolo, Long>,CustomTavoloRepository {
	
	@Query("select t from Tavolo t left join fetch t.utenteCreazione left join fetch t.giocatori")
	public List<Tavolo> listAllEager();
	
	@Query("select t from Tavolo t left join t.utenteCreazione uc left join t.giocatori where uc.username=:username")
	public List<Tavolo> listMine(String username);
	
	@Query("select t from Tavolo t left join fetch t.utenteCreazione uc left join fetch t.giocatori where uc.username=:username")
	public List<Tavolo> listMineEager(String username);
	
	@Query("select t from Tavolo t left join fetch t.utenteCreazione left join fetch t.giocatori where t.id=:id")
	public Tavolo findByIdEager(Long id);

	@Query(nativeQuery = true,value = "select t from tavolo t inner join utente u where t.id = u.tavolo_id and u.username=:name")
	public Tavolo getLastTable(String name);
}
