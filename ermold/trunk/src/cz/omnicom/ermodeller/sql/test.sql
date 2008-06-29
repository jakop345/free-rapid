--Set Echo on
Purge Recyclebin ;
/* používate-li verzi nižší než Oracle 10g,
 zakomentujte p?íkaz PURGE */
Drop table Garaz_obj cascade constraints ;

Drop table Zastavka_obj cascade constraints ;

Drop table Osoba_obj cascade constraints ;

Drop table Ridic_obj cascade constraints ;

Drop table Zakaznik_obj cascade constraints ;

Drop table Linka_obj cascade constraints ;

Drop  table Dopr_prostredek_obj cascade constraints ;

Drop table ridi_obj cascade constraints ;

Drop table obsazen_obj cascade constraints ;

Drop table objednane_obj cascade constraints ;

Drop table jezdi_obj cascade constraints ;

Drop table obsahuje_obj cascade constraints ;

Drop table Spoj_obj cascade constraints ;

Drop type Garaz_t FORCE ;

Drop type Zastavka_t FORCE ;

Drop type Osoba_t FORCE ;

Drop type Ridic_t FORCE ;

Drop type Zakaznik_t FORCE ;

Drop type Linka_t FORCE ;

Drop type Dopr_prostredek_t FORCE ;

Drop type ridi_t FORCE ;

Drop type obsazen_t FORCE ;

Drop type objednane_t FORCE ;

Drop type jezdi_t FORCE ;

Drop type obsahuje_t FORCE ;

Drop type Spoj_t FORCE ;
CREATE OR REPLACE TYPE Garaz_t AS OBJECT (
      ID number ,
      volnych_mist number ,
      kapacita number ,
      adresa VarChar2(64)
 ) ;
/
CREATE OR REPLACE TYPE Zastavka_t AS OBJECT (
      poloha VarChar2(64) ,
      nazev VarChar2(64) ,
      ID number
 ) ;
/
CREATE OR REPLACE TYPE Osoba_t AS OBJECT (
      ID number ,
      prijmeni VarChar2(64) ,
      jmeno VarChar2(64) ,
      adresa VarChar2(64)
 ) ;
/
CREATE OR REPLACE TYPE Ridic_t AS OBJECT (
      skupina_RO VarChar2(10)
 ) ;
/
CREATE OR REPLACE TYPE Zakaznik_t AS OBJECT (
      ICO VarChar2(64) ,
      DIC VarChar2(64)
 ) ;
/
CREATE OR REPLACE TYPE Linka_t AS OBJECT (
      pravidelnost char(10) ,
      nazev VarChar2(64) ,
      ID number
 ) ;
/
CREATE OR REPLACE TYPE Dopr_prostredek_t AS OBJECT (
      ID number ,
      znacka VarChar2(64) ,
      nazev VarChar2(64) ,
      typ_auta VarChar2(64) ,
      mist_k_sezeni number
 ) ;
/
CREATE OR REPLACE TYPE ridi_t AS OBJECT (
      osoba_id REF Osoba_t ,
      dopr_prostredek_id REF Dopr_prostredek_t
) ;
/
CREATE OR REPLACE TYPE obsazen_t AS OBJECT (
      dopr_prostredek_id REF Dopr_prostredek_t ,
      garaz_id REF Garaz_t
) ;
/
CREATE OR REPLACE TYPE objednane_t AS OBJECT (
      osoba_id REF Osoba_t ,
      spoj_id REF Spoj_t
) ;
/
CREATE OR REPLACE TYPE jezdi_t AS OBJECT (
      spoj_id REF Spoj_t ,
      dopr_prostredek_id REF Dopr_prostredek_t
) ;
/
CREATE OR REPLACE TYPE obsahuje_t AS OBJECT (
      cas_odjezdu Date ,
      cas_prijezdu Date ,
      zastavka_id REF Zastavka_t ,
      linka_id REF Linka_t
) ;
/
CREATE OR REPLACE TYPE Spoj_t AS OBJECT (
      datum_vyjezdu date ,
      pocet_k_rezervaci number ,
      ID number
 ) ;
/
ALTER TYPE Ridic_t ADD attribute Osoba_ID REF Osoba_t CASCADE ;

ALTER TYPE Zakaznik_t ADD attribute Osoba_ID REF Osoba_t CASCADE ;

ALTER TYPE Spoj_t ADD attribute Linka_ID REF Linka_t CASCADE ;
CREATE TABLE Garaz_obj OF Garaz_t (
      ID NOT NULL UNIQUE ,
      volnych_mist NOT NULL ,
      kapacita NOT NULL ,
      adresa NOT NULL
) ;
CREATE TABLE Zastavka_obj OF Zastavka_t (
      nazev NOT NULL ,
      ID NOT NULL UNIQUE
) ;
CREATE TABLE Osoba_obj OF Osoba_t (
      ID NOT NULL UNIQUE ,
      prijmeni NOT NULL ,
      jmeno NOT NULL ,
      adresa NOT NULL
) ;
CREATE TABLE Ridic_obj OF Ridic_t (
      skupina_RO NOT NULL ,
      osoba_id NOT NULL
) ;
CREATE TABLE Zakaznik_obj OF Zakaznik_t (
      osoba_id NOT NULL
) ;
CREATE TABLE Linka_obj OF Linka_t (
      pravidelnost NOT NULL ,
      nazev NOT NULL ,
      ID NOT NULL UNIQUE
) ;
CREATE TABLE Dopr_prostredek_obj OF Dopr_prostredek_t (
      ID NOT NULL UNIQUE ,
      znacka NOT NULL ,
      typ_auta NOT NULL ,
      mist_k_sezeni NOT NULL
) ;
CREATE TABLE ridi_obj OF ridi_t (
      osoba_id NOT NULL ,
      dopr_prostredek_id NOT NULL
) ;
CREATE TABLE obsazen_obj OF obsazen_t (
      dopr_prostredek_id NOT NULL ,
      garaz_id NOT NULL
) ;
CREATE TABLE objednane_obj OF objednane_t (
      osoba_id NOT NULL ,
      spoj_id NOT NULL
) ;
CREATE TABLE jezdi_obj OF jezdi_t (
      spoj_id NOT NULL ,
      dopr_prostredek_id NOT NULL
) ;
CREATE TABLE obsahuje_obj OF obsahuje_t (
      cas_odjezdu NOT NULL ,
      cas_prijezdu NOT NULL ,
      zastavka_id NOT NULL ,
      linka_id NOT NULL
) ;
CREATE TABLE Spoj_obj OF Spoj_t (
      linka_id NOT NULL ,
      datum_vyjezdu NOT NULL ,
      pocet_k_rezervaci NOT NULL ,
      ID NOT NULL UNIQUE
) ;
ALTER TABLE Ridic_obj ADD FOREIGN KEY (Osoba_ID) REFERENCES Osoba_obj ;

ALTER TABLE Zakaznik_obj ADD FOREIGN KEY (Osoba_ID) REFERENCES Osoba_obj ;

ALTER TABLE ridi_obj ADD FOREIGN KEY (OSOBA_ID) REFERENCES Osoba_obj ;

ALTER TABLE ridi_obj ADD FOREIGN KEY (DOPR_PROSTREDEK_ID) REFERENCES Dopr_prostredek_obj ;

ALTER TABLE obsazen_obj ADD FOREIGN KEY (DOPR_PROSTREDEK_ID) REFERENCES Garaz_obj ;

ALTER TABLE obsazen_obj ADD FOREIGN KEY (GARAZ_ID) REFERENCES Dopr_prostredek_obj ;

ALTER TABLE objednane_obj ADD FOREIGN KEY (osoba_id) REFERENCES Osoba_obj ;

ALTER TABLE objednane_obj ADD FOREIGN KEY (spoj_id) REFERENCES Spoj_obj ;

ALTER TABLE jezdi_obj ADD FOREIGN KEY (spoj_id) REFERENCES Spoj_obj ;

ALTER TABLE jezdi_obj ADD FOREIGN KEY (DOPR_PROSTREDEK_ID) REFERENCES Dopr_prostredek_obj ;

ALTER TABLE obsahuje_obj ADD FOREIGN KEY (zastavka_id) REFERENCES Zastavka_obj ;

ALTER TABLE obsahuje_obj ADD FOREIGN KEY (linka_id) REFERENCES Linka_obj ;

ALTER TABLE Spoj_obj ADD FOREIGN KEY (Linka_ID) REFERENCES Linka_obj ;

commit ;