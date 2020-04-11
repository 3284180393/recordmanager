create table platform_record_backup_result
(
    id INTEGER PRIMARY KEY AUTOINCREMENT ,
    platformId nvarchar(20),
    platformName nvarchar(20),
    backupDate nvarchar(20),
    startTime datetime ,
    endTime datetime,
    result boolean,
    comment nvarchar(1000),
    backupCount integer,
    failCount integer,
    notbackupCount integer
);

create table ent_record_check_result
     (
         id INTEGER PRIMARY KEY AUTOINCREMENT ,
         platformCheckId integer,
         enterpriseId nvarchar(20),
         enterpriseName nvarchar(20),
         checkTime datetime,
         beginTime datetime,
         endTime datetime,
         result boolean,
         comment nvarchar(1000),
         hasBak boolean,
         checkCount integer,
         successCount integer,
         notIndexCount integer,
         notFileCount integer,
         notBakIndexCount integer,
         notBakFileCount integer
     );

create table platform_record_check_result
(
    id INTEGER PRIMARY KEY AUTOINCREMENT ,
    platformId nvarchar(20),
    platformName nvarchar(20),
    checkTime datetime,
    timeUsage integer,
    beginTime datetime,
    endTime datetime,
    result boolean,
    comment nvarchar(1000)
);

create table fail_check_record_detail
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    platformCheckId integer,
    enterpriseId nvarchar(20),
    enterpriseName nvarchar(20),
    sessionId nvarchar(40),
    agentId nvarchar(40),
    startTime datetime,
    endTime datetime,
    talkDuration integer,
    callType integer,
    endType integer,
    recordIndex nvarchar(100),
    bakRecordIndex nvarchar(100),
    failReason nvarchar(20)
);

create table not_backup_record_detail
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    platformBackupId integer,
    enterpriseId nvarchar(20),
    enterpriseName nvarchar(20),
    sessionId nvarchar(40),
    agentId nvarchar(40),
    startTime datetime,
    endTime datetime,
    talkDuration integer,
    callType integer,
    endType integer,
    recordIndex nvarchar(100),
    bakRecordIndex nvarchar(100),
    failReason nvarchar(20)
);

create table fail_backup_record_file
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    platformBackupId integer,
    fileSavePath nvarchar(255),
    backupPath nvarchar(255),
    failReason nvarchar(20)
);

create table next_backup_date
(
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nextBackupDate nvarchar(20),
    updateTime datetime
);