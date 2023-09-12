-- *********************************
-- 錯誤代碼表
-- *********************************

--DROP TABLE CFG_ERRORCODE;

CREATE TABLE CFG_ERRORCODE (
 OID             VARCHAR(32) NOT NULL,
 CODE   VARCHAR(20) NOT NULL,
 LOCALE   VARCHAR(5) NOT NULL,
 SEVERITY  VARCHAR(5) NOT NULL,
 MESSAGE   VARCHAR(1024) NOT NULL,
 SUGGESTION  VARCHAR(1024) ,
 SYSID        VARCHAR(5),
 SENDMON         CHAR(1)     DEFAULT 'N',
 HELPURL        VARCHAR(128) ,
 LASTMODIFYBY CHARACTER(10) NOT NULL,
 LASTMODIFYTIME TIMESTAMP NOT NULL,
 CONSTRAINT P_CFG_ERRORCODE PRIMARY KEY (OID)
);

CREATE UNIQUE INDEX XERRORCODE01
    ON CFG_ERRORCODE (CODE ASC, LOCALE ASC);
    
COMMENT ON TABLE CFG_ERRORCODE IS '錯誤代碼表';    

COMMENT ON CFG_ERRORCODE (
 OID             IS 'OID',
 CODE   IS '狀況代碼',
 LOCALE   IS '語言別(zh_TW/zh_CN/en)',
 SEVERITY  IS '等級(INFO/ERROR/WARN/NA)',
 MESSAGE   IS '狀況說明',
 SUGGESTION  IS '建議處理方式',
 SYSID     IS '系統別',
 SENDMON     IS '是否送監控(Y/N)',
 HELPURL         IS 'HELP URL',
 LASTMODIFYBY IS '最後修改人',
 LASTMODIFYTIME IS '最後修改時間'
);