package cn.mx.app.repository;


import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cn.mx.app.entity.UserChief;


@CacheConfig(cacheNames = "user")
@RepositoryRestResource(collectionResourceRel = "user_chief", path = "user_chief")
public interface UserRepository extends PagingAndSortingRepository<UserChief,String> {
	@Cacheable
	UserChief findByName(@Param("name")String name);
	Page<UserChief> findByName(Pageable pageable,@Param("name")String name);
}

