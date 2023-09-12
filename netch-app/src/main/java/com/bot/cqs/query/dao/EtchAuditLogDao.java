package com.bot.cqs.query.dao;

import com.bot.cqs.query.persistence.EtchAuditLog;
import com.iisigroup.cap.db.dao.GenericDao;

public interface EtchAuditLogDao extends GenericDao<EtchAuditLog> {

    public void saveEtchAuditLog(EtchAuditLog etchAuditLog);

}
