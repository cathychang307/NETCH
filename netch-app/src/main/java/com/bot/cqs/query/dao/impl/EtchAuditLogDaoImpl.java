package com.bot.cqs.query.dao.impl;

import org.springframework.stereotype.Repository;

import com.bot.cqs.query.dao.EtchAuditLogDao;
import com.bot.cqs.query.persistence.EtchAuditLog;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;

@Repository
public class EtchAuditLogDaoImpl extends GenericDaoImpl<EtchAuditLog> implements EtchAuditLogDao {

    public void saveEtchAuditLog(EtchAuditLog etchAuditLog) {
        save(etchAuditLog);
    }

}
