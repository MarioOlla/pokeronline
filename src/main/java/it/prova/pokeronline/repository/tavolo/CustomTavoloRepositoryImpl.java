package it.prova.pokeronline.repository.tavolo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import it.prova.pokeronline.model.Tavolo;

public class CustomTavoloRepositoryImpl implements CustomTavoloRepository{

	@Autowired
	private EntityManager entityManager;
	
	@Override
	public List<Tavolo> findByExample(Tavolo example) {
		Map<String, Object> paramaterMap = new HashMap<String, Object>();
		List<String> whereClauses = new ArrayList<String>();

		StringBuilder queryBuilder = new StringBuilder("select t from Tavolo t where t.id = t.id ");
		
		if(StringUtils.isNotBlank(example.getDenominazione())) {
			whereClauses.add(" t.denominazione like :denominazione");
			paramaterMap.put("denominazione", "%"+example.getDenominazione()+"%");
		}
		
		if(example.getEsperienzaMin()!=null) {
			whereClauses.add(" t.esperienzaMin < :esperienzaMin");
			paramaterMap.put("esperienzaMin", example.getEsperienzaMin());
		}
		
		if(example.getCifraMinima()!=null) {
			whereClauses.add(" t.cifraMinima < :cifraMinima");
			paramaterMap.put("cifraMinima", example.getCifraMinima());
		}
		
		if(example.getDataCreazione()!=null) {
			whereClauses.add(" t.dataCreazione > :dataCreazione");
			paramaterMap.put("dataCreazione", example.getDenominazione());
		}
		
		queryBuilder.append(!whereClauses.isEmpty()?" and ":"");
		queryBuilder.append(StringUtils.join(whereClauses, " and "));
		TypedQuery<Tavolo> typedQuery = entityManager.createQuery(queryBuilder.toString(), Tavolo.class);

		for (String key : paramaterMap.keySet()) {
			typedQuery.setParameter(key, paramaterMap.get(key));
		}

		return typedQuery.getResultList();
	}
	
}
