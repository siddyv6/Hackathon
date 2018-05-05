package it.reply.dynprice.dynamicpricing.persistence.dao;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

import it.reply.dynprice.dynamicpricing.persistence.model.PriceEntity;



@Transactional
public interface PriceDao extends CrudRepository<PriceEntity, Long> {



}

