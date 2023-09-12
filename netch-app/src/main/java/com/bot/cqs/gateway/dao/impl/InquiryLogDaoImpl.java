package com.bot.cqs.gateway.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.bot.cqs.gateway.dao.InquiryLogDao;
import com.bot.cqs.gateway.persistence.InquiryLog;
import com.bot.cqs.query.command.QueryInquiryLogCommand;
import com.bot.cqs.query.dto.InquiryLogDto;
import com.bot.cqs.query.util.DateUtil;
import com.bot.cqs.query.util.factory.ApplicationParameterFactory;
import com.iisigroup.cap.db.constants.SearchMode;
import com.iisigroup.cap.db.dao.SearchSetting;
import com.iisigroup.cap.db.dao.impl.GenericDaoImpl;
import com.iisigroup.cap.jdbc.support.CapSqlStatement;

@Repository
public class InquiryLogDaoImpl extends GenericDaoImpl<InquiryLog> implements InquiryLogDao {
    private ApplicationParameterFactory applicationParameterFactory;
    @Resource
    private CapSqlStatement sqlp;

    public InquiryLogDaoImpl() {

        super();
        applicationParameterFactory = ApplicationParameterFactory.newInstance();

    }

    /**
     * 列出所有資料查詢記錄，並以查詢主鍵值排序。
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<InquiryLog> findAll() {
        SearchSetting search = createSearchTemplete();
        search.addOrderBy("inquiryLogKey");
        return find(search).subList(0, applicationParameterFactory.getQueryMaxRows());
    }

    /**
     * 依資料查詢主鍵值搜尋單筆資料查詢記錄。
     * 
     * @param inquiryLogKey
     * @return
     */
    public InquiryLog findByInquiryLogKey(String inquiryLogKey) {
        SearchSetting search = createSearchTemplete();
        search.addSearchModeParameters(SearchMode.EQUALS, "inquiryLogKey", inquiryLogKey);
        return findUniqueOrNone(search);
    }

    /**
     * 帳務報表檢視-查詢明細
     * 
     * @param command
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<InquiryLog> findQueryDetail(QueryInquiryLogCommand command) {
        return null;
        // TODO
        // String inChargeBankId = command.getInquiryChargeBankId();
        // String inAccount = command.getInquiryAccount();
        // String inTxCode = command.getInquiryTxCode();
        // String startDate = command.getStartDate();
        // String endDate = command.getEndDate();
        //
        // DetachedCriteria criteria = DetachedCriteria.forClass(InquiryLog.class);
        // criteria.add(Restrictions.isNull("inquiryErrorCode"));
        // if (StringUtils.hasText(inChargeBankId)) {
        // criteria
        // .add(Restrictions.eq("inquiryChargeBankId", inChargeBankId));
        // }
        // if (StringUtils.hasText(inAccount)) {
        // criteria.add(Restrictions.eq("inquiryAccount", inAccount));
        // }
        // if (StringUtils.hasText(inTxCode)) {
        // criteria.add(Restrictions.eq("inquiryTxCode", inTxCode));
        // }
        // if (StringUtils.hasText(startDate) && StringUtils.hasText(endDate)) {
        // criteria.add(Restrictions.ge("inquiryDate", startDate));
        // criteria.add(Restrictions.le("inquiryDate", endDate));
        // } else if (StringUtils.hasText(startDate)
        // && !StringUtils.hasText(endDate)) {
        // criteria.add(Restrictions.eq("inquiryDate", startDate));
        // } else if (!StringUtils.hasText(startDate)
        // && StringUtils.hasText(endDate)) {
        // criteria.add(Restrictions.eq("inquiryDate", endDate));
        // } else if (!StringUtils.hasText(startDate)
        // && !StringUtils.hasText(endDate)) {
        // criteria.add(Restrictions.eq("inquiryDate", DateUtil
        // .toADDate(new Date())));
        // }
        // criteria.addOrder(Order.asc("inquiryDate")).addOrder(Order.asc("inquiryChargeBankId")).addOrder(Order.asc("inquiryAccount")).addOrder(Order.asc("inquiryTxCode"));
        // return getHibernateTemplate().findByCriteria(criteria, 0,
        // applicationParameterFactory.getQueryMaxRows());
    }

    /**
     * 查詢月收費檔
     * 
     * @param inquiryChargeBankId
     *            付費分行
     * @param date[]
     *            時間 一個月的第一天與最後一天
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<InquiryLogDto> findChargeQuery(String inquiryChargeBankId, Date[] date) {
        Map<String, String> param = new HashMap<String, String>();
        param.put("startDate", DateUtil.toADDate(date[0]));
        param.put("endDate", DateUtil.toADDate(date[1]));
        param.put("inquiryChargeBankId", inquiryChargeBankId);
        return getNamedJdbcTemplate().query(sqlp.getValue("InquiryLogDto_findChargeQuery").toString(), param, new InquiryLogDtoRowMapper());
    }

    public class InquiryLogDtoRowMapper implements RowMapper<InquiryLogDto> {

        @Override
        public InquiryLogDto mapRow(ResultSet rs, int rowNum) throws SQLException {
            InquiryLogDto item = new InquiryLogDto();

            item.setInquiryChargeBankId(rs.getString("inquiryChargeBankId"));
            item.setInquiryDate(rs.getString("inquiryDate"));
            item.setInquiryTxCode(rs.getString("inquiryTxCode"));
            item.setRowSummary(rs.getInt("rowSummary"));
            item.setTotalRowSummary(rs.getInt("totalRowSummary"));
            item.setInquiryId(rs.getString("inquiryId"));
            item.setInquiryBizId(rs.getString("inquiryBizId"));
            item.setInquiryBankCode(rs.getString("inquiryBankCode"));
            item.setInquiryBankAccount(rs.getString("inquiryBankAccount"));
            item.setInquiryCacheFlag(rs.getInt("inquiryCacheFlag"));

            return item;
        }

    }

    /**
     * 新增 資料查詢記錄。
     * 
     * @param inquiryLog
     */
    public void insertInquiryLog(InquiryLog inquiryLog) {
        save(inquiryLog);
    }

    /**
     * 修改 資料查詢記錄。
     * 
     * @param inquiryLog
     */
    public void updateInquiryLog(InquiryLog inquiryLog) {
        save(inquiryLog);
    }

    /**
     * 選出所有的charge_bank_id。
     * 
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<InquiryLog> findInquiryChargeBankList() {
        Map<String, Object> param = new HashMap<String, Object>();
        List<Map<String, Object>> list = getNamedJdbcTemplate().query("InquiryLog_getChargeBankList", param);
        List<InquiryLog> resultList = new ArrayList<InquiryLog>();
        for (Map<String, Object> map : list) {
            InquiryLog inquiryLog = new InquiryLog();
            inquiryLog.setInquiryChargeBankId(MapUtils.getString(map, "inquiryChargeBankId"));
            resultList.add(inquiryLog);
        }
        return resultList;
    }

}
