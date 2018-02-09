
insert into record(scene,directory,name,hd_level,score,score_feel,score_star,score_passion,score_cum,score_special,score_bareback,deprecated,special_desc,last_modify_date,type,record_detail_id)
select scene,directory,name,HDLevel,score,scoreFeel,scoreStar,scorePassion,scoreCum,scoreSpecial,scoreNoCond,deprecated,specialDesc,lastModifyDate,2,id
from record_3w

CREATE TABLE record (
    "_id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "scene" TEXT,
    "directory" TEXT,
    "name" TEXT,
    "hd_level" TEXT,
    "score" INTEGER,
    "score_feel" INTEGER,
    "score_star" INTEGER,
    "score_passion" INTEGER,
    "score_cum" INTEGER,
    "score_special" INTEGER,
    "score_bareback" INTEGER,
    "deprecated" INTEGER,
    "special_desc" TEXT,
    "last_modify_date" INTEGER,
    "type" INTEGER,
    "record_detail_id" INTEGER)
    
insert into record_type1
select id,sequence,scoreRhythm,scoreForePlay,scoreBJob,scoreFkType1,scoreFkType2,scoreFkType3,scoreFkType4,scoreFkType5,scoreFkType6,scoreScene,scoreStory,scoreCShow,scoreRim
from record_1v1

CREATE TABLE record_type1 (
    "_id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "sequence" INTEGER,
    "score_rhythm" INTEGER,
    "score_fore_play" INTEGER,
    "score_bjob" INTEGER,
    "score_fk_type1" INTEGER,
    "score_fk_type2" INTEGER,
    "score_fk_type3" INTEGER,
    "score_fk_type4" INTEGER,
    "score_fk_type5" INTEGER,
    "score_fk_type6" INTEGER,
    "score_scene" INTEGER,
    "score_story" INTEGER,
    "score_cshow" INTEGER,
    "score_rim" INTEGER)
    
insert into record_type3
select id,sequence,scoreRhythm,scoreForePlay,scoreBJob,scoreFkType1,scoreFkType2,scoreFkType3,scoreFkType4,scoreFkType5,scoreFkType6,scoreFkType7,scoreFkType8,scoreScene,scoreStory,scoreCShow,scoreRim
from record_3w

CREATE TABLE record_type3 (
    "_id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "sequence" INTEGER,
    "score_rhythm" INTEGER,
    "score_fore_play" INTEGER,
    "score_bjob" INTEGER,
    "score_fk_type1" INTEGER,
    "score_fk_type2" INTEGER,
    "score_fk_type3" INTEGER,
    "score_fk_type4" INTEGER,
    "score_fk_type5" INTEGER,
    "score_fk_type6" INTEGER,
    "score_fk_type7" INTEGER,
    "score_fk_type8" INTEGER,
    "score_scene" INTEGER,
    "score_story" INTEGER,
    "score_cshow" INTEGER,
    "score_rim" INTEGER)
 
insert into stars
select *
from star
 
 
 CREATE TABLE "stars" (
    "_id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "name" TEXT,
    "records" INTEGER,
    "betop" INTEGER,
    "bebottom" INTEGER,
    "average" REAL,
    "max" INTEGER,
    "min" INTEGER,
    "caverage" REAL,
    "cmax" INTEGER,
    "cmin" INTEGER
)

insert into record_star
select *, 80, 80
from record_star_relation
 
CREATE TABLE record_star (
    "_id" INTEGER PRIMARY KEY AUTOINCREMENT,
    "record_id" INTEGER,
    "star_id" INTEGER,
    "type" INTEGER,
    "score" INTEGER,
    "score_c" INTEGER)

insert into properties
select *
from conf
 
CREATE TABLE "properties" (
    "_id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "key" TEXT NOT NULL,
    "value" TEXT,
    "other" TEXT
)

CREATE TABLE "favor" (
    "_id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    "star_id" INTEGER,
    "favor" REAL
)