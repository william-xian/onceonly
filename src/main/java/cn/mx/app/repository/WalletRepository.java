package cn.mx.app.repository;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cn.mx.app.entity.Wallet;



@RepositoryRestResource(collectionResourceRel = "wallet", path = "wallet")
public interface WalletRepository extends PagingAndSortingRepository<Wallet,Long> {
}

