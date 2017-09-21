package cn.mx.app.repository;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cn.mx.app.entity.GoodsShipping;

@RepositoryRestResource(collectionResourceRel = "goods_shipping", path = "goods_shipping")
public interface GoodsShippingRepository extends PagingAndSortingRepository<GoodsShipping,Long> {

}

