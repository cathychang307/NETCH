--BAH_JOB_EXECUTION.STATUS JOB執行狀態
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobStatus00000000000000000000001','zh_TW','jobStatus','STARTING','STARTING',1,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobStatus00000000000000000000002','zh_TW','jobStatus','STARTED','STARTED',2,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobStatus00000000000000000000003','zh_TW','jobStatus','COMPLETED','COMPLETED',3,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobStatus00000000000000000000004','zh_TW','jobStatus','FAILED','FAILED',4,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobStatus00000000000000000000005','zh_TW','jobStatus','STOPPING','STOPPING',5,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobStatus00000000000000000000006','zh_TW','jobStatus','STOPPED','STOPPED',6,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobStatus00000000000000000000007','zh_TW','jobStatus','ABANDONED','ABANDONED',7,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobStatus00000000000000000000008','zh_TW','jobStatus','UNKNOWN','UNKNOWN',8,'System',current timestamp);
--BAH_JOB_EXECUTION.EXIT_CODE JOB執行結果
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobExitCode000000000000000000001','zh_TW','jobExitCode','EXECUTING','EXECUTING',1,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobExitCode000000000000000000002','zh_TW','jobExitCode','COMPLETED','COMPLETED',2,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobExitCode000000000000000000003','zh_TW','jobExitCode','NOOP','NOOP',3,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobExitCode000000000000000000004','zh_TW','jobExitCode','STOPPED','STOPPED',4,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobExitCode000000000000000000005','zh_TW','jobExitCode','FAILED','FAILED',5,'System',current timestamp);
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('jobExitCode000000000000000000006','zh_TW','jobExitCode','UNKNOWN','UNKNOWN',6,'System',current timestamp);
--BAH_SCHEDULE.timeZoneId 時區代碼
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('timeZoneId0000000000000000000001','zh_TW','timeZoneId','CTT','中原標準時間GMT+8:0',1,'System',current timestamp);
--insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('timeZoneId0000000000000000000002','zh_TW','timeZoneId','JST','日本標準時間GMT+9:0',2,'System',current timestamp);
--BAH_SCHEDULE.exeHost 執行主機
insert into CFG_CODETYPE(OID,LOCALE,CodeType,CODEVALUE,CODEDESC,CODEORDER,UPDATER,UPDATETIME) values('schExeHost0000000000000000000001','zh_TW','schExeHost','localhost','本機',1,'System',current timestamp);


