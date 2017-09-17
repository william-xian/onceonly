package io.onceonly.beans;

import java.util.List;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
@CacheConfig(cacheNames = "i18ns")
public interface I18nRepository extends CrudRepository<OOI18n,String> {
	List<OOI18n> findByIdStartingWith(@Param("id")String id);
	OOI18n findOneByName(@Param("name")String name);
	List<OOI18n> findByVal(@Param("val")String val);
}