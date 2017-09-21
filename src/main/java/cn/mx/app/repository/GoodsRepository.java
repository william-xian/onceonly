package cn.mx.app.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cn.mx.app.entity.Goods;



@RepositoryRestResource(collectionResourceRel = "goods", path = "goods")
public interface GoodsRepository extends PagingAndSortingRepository<Goods,Long> {
	Goods findByName(@Param("name")String name);
	Page<Goods> findByName(Pageable pageable,@Param("name")String name);
}

