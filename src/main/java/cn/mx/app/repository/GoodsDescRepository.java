package cn.mx.app.repository;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cn.mx.app.entity.GoodsDesc;

@RepositoryRestResource(collectionResourceRel = "goods_desc", path = "goods_desc")
public interface GoodsDescRepository extends PagingAndSortingRepository<GoodsDesc,Long> {

}

