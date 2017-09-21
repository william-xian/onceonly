package cn.mx.app.repository;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cn.mx.app.entity.GoodsOrder;



@RepositoryRestResource(collectionResourceRel = "goods_order", path = "goods_order")
public interface GoodsOrderRepository extends PagingAndSortingRepository<GoodsOrder,Long> {
}

