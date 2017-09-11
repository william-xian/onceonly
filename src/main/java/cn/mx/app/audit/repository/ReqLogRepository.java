package cn.mx.app.audit.repository;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import cn.mx.app.entity.ReqLog;

@RepositoryRestResource(collectionResourceRel = "req_log", path = "req_log")
public interface ReqLogRepository extends PagingAndSortingRepository<ReqLog,String> {
}
