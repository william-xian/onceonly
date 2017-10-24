package cn.mx.app.audit.repository;


import org.springframework.stereotype.Component;

import cn.mx.app.entity.ReqLog;
import io.onceonly.db.dao.impl.DaoImpl;

@Component
public class ReqLogRepository extends DaoImpl<ReqLog,String> {
}
