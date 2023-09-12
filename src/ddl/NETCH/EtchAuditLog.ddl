--DROP TABLE AP_ETCH_AUDIT_LOG
CREATE TABLE AP_ETCH_AUDIT_LOG (
    oid             VARCHAR(32) NOT NULL,
    user_id          VARCHAR(40),
    source_ip        VARCHAR(20),
    execute_date   DATETIME,
    target_ip        VARCHAR(20),
    access_object        VARCHAR(100),
    access_account        VARCHAR(40),
    function_id        VARCHAR(50),
    action_id        VARCHAR(10),
    sql_script        text,
    execute_status        VARCHAR(10),
    data_count        VARCHAR(10),
    execute_result        text,
    CONSTRAINT pk_AP_ETCH_AUDIT_LOG PRIMARY KEY (oid)
);
