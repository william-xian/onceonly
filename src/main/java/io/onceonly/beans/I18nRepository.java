package io.onceonly.beans;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Component;

import io.onceonly.db.dao.impl.DaoImpl;
import io.onceonly.db.tbl.OOI18n;

@CacheConfig(cacheNames = "i18ns")
@Component
public class I18nRepository extends DaoImpl<OOI18n,String> {
	List<OOI18n> findByIdStartingWith(String id) {
		return new ArrayList<>();
	}
	OOI18n findOneByName(String name) {
		return new OOI18n();
	}
	List<OOI18n> findByVal(String val) {
		return new ArrayList<>();
	}
}