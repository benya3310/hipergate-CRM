CREATE FUNCTION k_sp_del_list (CHAR) RETURNS INTEGER AS '
DECLARE
  tp SMALLINT;
  wa CHAR(32);
  bk CHAR(32);
BEGIN

  SELECT tp_list,gu_workarea INTO tp,wa FROM k_lists WHERE gu_list=$1;

  SELECT gu_list INTO bk FROM k_lists WHERE gu_workarea=wa AND gu_query=$1 AND tp_list=4;

  IF FOUND THEN
    DELETE FROM k_list_members WHERE gu_member IN (SELECT gu_contact FROM k_x_list_members WHERE gu_list=bk) AND gu_member NOT IN (SELECT x.gu_contact FROM k_x_list_members x, k_lists l WHERE x.gu_list=l.gu_list AND l.gu_workarea=wa AND x.gu_list<>bk);
    DELETE FROM k_list_members WHERE gu_member IN (SELECT gu_company FROM k_x_list_members WHERE gu_list=bk) AND gu_member NOT IN (SELECT x.gu_company FROM k_x_list_members x, k_lists l WHERE x.gu_list=l.gu_list AND l.gu_workarea=wa AND x.gu_list<>bk);

    DELETE FROM k_x_list_members WHERE gu_list=bk;

    DELETE FROM k_x_campaign_lists WHERE gu_list=bk;

    DELETE FROM k_lists WHERE gu_list=bk;
  END IF;

  DELETE FROM k_list_members WHERE gu_member IN (SELECT gu_contact FROM k_x_list_members WHERE gu_list=$1) AND gu_member NOT IN (SELECT x.gu_contact FROM k_x_list_members x, k_lists l WHERE x.gu_list=l.gu_list AND l.gu_workarea=wa AND x.gu_list<>$1);

  DELETE FROM k_list_members WHERE gu_member IN (SELECT gu_company FROM k_x_list_members WHERE gu_list=$1) AND gu_member NOT IN (SELECT x.gu_company FROM k_x_list_members x, k_lists l WHERE x.gu_list=l.gu_list AND l.gu_workarea=wa AND x.gu_list<>$1);

  DELETE FROM k_x_list_members WHERE gu_list=$1;

  DELETE FROM k_x_campaign_lists WHERE gu_list=$1;

  DELETE FROM k_lists WHERE gu_list=$1;

  RETURN 0;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE FUNCTION k_sp_email_blocked (CHAR,VARCHAR) RETURNS SMALLINT AS '
DECLARE
  BlackList CHAR(32);
  BoBlocked SMALLINT;

BEGIN
  BoBlocked := 0;

  SELECT gu_list INTO BlackList FROM k_lists WHERE gu_query=$1 AND tp_list=4;

  IF FOUND THEN
    SELECT 1 INTO BoBlocked FROM k_x_list_members WHERE gu_list=BlackList AND tx_email=$2;
  END IF;

  RETURN BoBlocked;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE FUNCTION k_sp_contact_blocked (CHAR,CHAR) RETURNS SMALLINT AS '
DECLARE
  BlackList CHAR(32);
  BoBlocked SMALLINT;

BEGIN
  BoBlocked := 0;

  SELECT gu_list INTO BlackList FROM k_lists WHERE gu_query=$1 AND tp_list=4;

  IF FOUND THEN
    SELECT 1 INTO BoBlocked FROM k_x_list_members WHERE gu_list=BlackList AND gu_contact=$2 LIMIT 1;
  END IF;

  RETURN BoBlocked;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE FUNCTION k_sp_company_blocked (CHAR,CHAR) RETURNS SMALLINT AS '
DECLARE
  BlackList CHAR(32);
  BoBlocked SMALLINT;

BEGIN
  BoBlocked := 0;

  SELECT gu_list INTO BlackList FROM k_lists WHERE gu_query=$1 AND tp_list=4;

  IF FOUND THEN
    SELECT 1 INTO BoBlocked FROM k_x_list_members WHERE gu_list=BlackList AND gu_company=$2 LIMIT 1;
  END IF;

  RETURN BoBlocked;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE FUNCTION k_sp_del_company() RETURNS OPAQUE AS '
BEGIN
  UPDATE k_member_address SET gu_company=NULL WHERE gu_company=OLD.gu_company;
  RETURN OLD;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE TRIGGER k_tr_del_company BEFORE DELETE ON k_companies FOR EACH ROW EXECUTE PROCEDURE k_sp_del_company();
GO;

CREATE FUNCTION k_sp_del_contact() RETURNS OPAQUE AS '
BEGIN
  UPDATE k_member_address SET gu_contact=NULL WHERE gu_contact=OLD.gu_contact;
  RETURN OLD;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE TRIGGER k_tr_del_contact BEFORE DELETE ON k_contacts FOR EACH ROW EXECUTE PROCEDURE k_sp_del_contact();
GO;

CREATE FUNCTION k_sp_del_address() RETURNS OPAQUE AS '
BEGIN
  DELETE FROM k_member_address WHERE gu_address=OLD.gu_address;
  RETURN OLD;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE TRIGGER k_tr_del_address BEFORE DELETE ON k_addresses FOR EACH ROW EXECUTE PROCEDURE k_sp_del_address();
GO;

CREATE FUNCTION k_sp_ins_address() RETURNS OPAQUE AS '
DECLARE
  AddrId CHAR(32);
  NmLegal VARCHAR(70);

BEGIN
  IF NEW.bo_active=1 THEN
  
    NmLegal := NEW.nm_company;
    IF NmLegal IS NOT NULL AND char_length(NmLegal)=0 THEN
      NmLegal := NULL;
    END IF;
    
    SELECT gu_address INTO AddrId FROM k_member_address WHERE gu_address=NEW.gu_address;

    IF NOT FOUND THEN
      INSERT INTO k_member_address (gu_address,ix_address,gu_workarea,dt_created,dt_modified,gu_writer,nm_legal,
                                    tp_location,tp_street,nm_street,nu_street,tx_addr1,tx_addr2,full_addr,id_country,
                                    nm_country,id_state,nm_state,mn_city,zipcode,work_phone,direct_phone,home_phone,
                                    mov_phone,fax_phone,other_phone,po_box,tx_email,url_addr,contact_person,tx_salutation,tx_remarks) VALUES
                                   (NEW.gu_address,NEW.ix_address,NEW.gu_workarea,NEW.dt_created,NEW.dt_modified,NEW.gu_user,NmLegal,
                                    NEW.tp_location,NEW.tp_street,NEW.nm_street,NEW.nu_street,NEW.tx_addr1,
                                    NEW.tx_addr2,COALESCE(NEW.tx_addr1,'''')||CHR(10)||COALESCE(NEW.tx_addr2,''''),
                                    NEW.id_country,NEW.nm_country,NEW.id_state,NEW.nm_state,NEW.mn_city,NEW.zipcode,
                                    NEW.work_phone,NEW.direct_phone,NEW.home_phone,NEW.mov_phone,NEW.fax_phone,NEW.other_phone,
                                    NEW.po_box,NEW.tx_email,NEW.url_addr,NEW.contact_person,NEW.tx_salutation,NEW.tx_remarks);
    END IF;
  END IF;

  RETURN NEW;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE TRIGGER k_tr_ins_address AFTER INSERT ON k_addresses FOR EACH ROW EXECUTE PROCEDURE k_sp_ins_address();
GO;

CREATE FUNCTION k_sp_upd_address() RETURNS OPAQUE AS '
DECLARE
  AddrId CHAR(32);
  NmLegal VARCHAR(70);
  
BEGIN
  IF NEW.bo_active=1 THEN
    SELECT gu_address INTO AddrId FROM k_member_address WHERE gu_address=NEW.gu_address;

    NmLegal := NEW.nm_company;
    IF NmLegal IS NOT NULL AND char_length(NmLegal)=0 THEN
      NmLegal := NULL;
    END IF;

    IF NOT FOUND THEN
      INSERT INTO k_member_address (gu_address,ix_address,gu_workarea,dt_created,dt_modified,gu_writer,nm_legal,tp_location,tp_street,nm_street,nu_street,tx_addr1,tx_addr2,full_addr,id_country,nm_country,id_state,nm_state,mn_city,zipcode,work_phone,direct_phone,home_phone,mov_phone,fax_phone,other_phone,po_box,tx_email,url_addr,contact_person,tx_salutation,tx_remarks) VALUES
    				   (NEW.gu_address,NEW.ix_address,NEW.gu_workarea,NEW.dt_created,NEW.dt_modified,NEW.gu_user,NmLegal,NEW.tp_location,NEW.tp_street,NEW.nm_street,NEW.nu_street,NEW.tx_addr1,NEW.tx_addr2,COALESCE(NEW.tx_addr1,'''')||CHR(10)||COALESCE(NEW.tx_addr2,''''),NEW.id_country,NEW.nm_country,NEW.id_state,NEW.nm_state,NEW.mn_city,NEW.zipcode,NEW.work_phone,NEW.direct_phone,NEW.home_phone,NEW.mov_phone,NEW.fax_phone,NEW.other_phone,NEW.po_box,NEW.tx_email,NEW.url_addr,NEW.contact_person,NEW.tx_salutation,NEW.tx_remarks);
    ELSE
      UPDATE k_member_address SET ix_address=NEW.ix_address,gu_workarea=NEW.gu_workarea,dt_created=NEW.dt_created,dt_modified=NEW.dt_modified,gu_writer=NEW.gu_user,tp_location=NEW.tp_location,tp_street=NEW.tp_street,nm_street=NEW.nm_street,nu_street=NEW.nu_street,tx_addr1=NEW.tx_addr1,tx_addr2=NEW.tx_addr2,full_addr=COALESCE(NEW.tx_addr1,'''')||CHR(10)||COALESCE(NEW.tx_addr2,''''),id_country=NEW.id_country,nm_country=NEW.nm_country,id_state=NEW.id_state,nm_state=NEW.nm_state,mn_city=NEW.mn_city,zipcode=NEW.zipcode,work_phone=NEW.work_phone,direct_phone=NEW.direct_phone,home_phone=NEW.home_phone,mov_phone=NEW.mov_phone,fax_phone=NEW.fax_phone,other_phone=NEW.other_phone,po_box=NEW.po_box,tx_email=NEW.tx_email,url_addr=NEW.url_addr,contact_person=NEW.contact_person,tx_salutation=NEW.tx_salutation,tx_remarks=NEW.tx_remarks
      WHERE gu_address=NEW.gu_address;
    END IF;
  ELSE
    DELETE FROM k_member_address WHERE gu_address=NEW.gu_address;
  END IF;

  RETURN NEW;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE TRIGGER k_tr_upd_address AFTER UPDATE ON k_addresses FOR EACH ROW EXECUTE PROCEDURE k_sp_upd_address();
GO;

CREATE FUNCTION k_sp_ins_comp_addr() RETURNS OPAQUE AS '
DECLARE

  GuCompany     CHAR(32);
  NmLegal       VARCHAR(70);
  NmCommercial  VARCHAR(70);
  IdLegal       VARCHAR(16);
  IdSector      VARCHAR(16);
  IdStatus      VARCHAR(30);
  IdRef         VARCHAR(50);
  TpCompany     VARCHAR(30);
  NuEmployees  	INTEGER;
  ImRevenue     FLOAT;
  GuSalesMan    CHAR(32);
  TxFranchise   VARCHAR(100);
  GuGeoZone     CHAR(32);
  DeCompany	VARCHAR(254);

BEGIN
  SELECT gu_company,nm_legal,id_legal,nm_commercial,id_sector,id_status,id_ref,tp_company,nu_employees,im_revenue,gu_sales_man,tx_franchise,gu_geozone,de_company
  INTO GuCompany,NmLegal,IdLegal,NmCommercial,IdSector,IdStatus,IdRef,TpCompany,NuEmployees,ImRevenue,GuSalesMan,TxFranchise,GuGeoZone,DeCompany
  FROM k_companies WHERE gu_company=NEW.gu_company;

  UPDATE k_member_address SET gu_company=GuCompany,nm_legal=NmLegal,id_legal=IdLegal,nm_commercial=NmCommercial,id_sector=IdSector,id_ref=IdRef,id_status=IdStatus,tp_company=TpCompany,nu_employees=NuEmployees,im_revenue=ImRevenue,gu_sales_man=GuSalesMan,tx_franchise=TxFranchise,gu_geozone=GuGeoZone,tx_comments=DeCompany
  WHERE gu_address=NEW.gu_address;

  RETURN NEW;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE TRIGGER k_tr_ins_comp_addr AFTER INSERT ON k_x_company_addr FOR EACH ROW EXECUTE PROCEDURE k_sp_ins_comp_addr()
GO;

CREATE FUNCTION k_sp_ins_cont_addr() RETURNS OPAQUE AS '
DECLARE
  GuCompany     CHAR(32);
  GuContact     CHAR(32);
  GuWorkArea    CHAR(32);
  TxName        VARCHAR(100);
  TxSurname     VARCHAR(100);
  DeTitle       VARCHAR(50);
  TrTitle       VARCHAR(50);
  DtBirth       TIMESTAMP;
  SnPassport    VARCHAR(16);
  IdGender      CHAR(1);
  NyAge         SMALLINT;
  TxDept        VARCHAR(70);
  TxDivision    VARCHAR(70);
  TxComments    VARCHAR(254);

BEGIN
  SELECT gu_contact,gu_company,gu_workarea,
         CASE WHEN char_length(tx_name)=0 THEN NULL ELSE tx_name END,
         CASE WHEN char_length(tx_surname)=0 THEN NULL ELSE tx_surname END,
         de_title,dt_birth,sn_passport,id_gender,ny_age,tx_dept,tx_division,tx_comments
  INTO   GuContact,GuCompany,GuWorkArea,TxName,TxSurname,DeTitle,DtBirth,SnPassport,IdGender,NyAge,TxDept,TxDivision,TxComments
  FROM k_contacts WHERE gu_contact=NEW.gu_contact;
  
  IF DeTitle IS NOT NULL THEN
    SELECT tr_en INTO TrTitle FROM k_contacts_lookup WHERE gu_owner=GuWorkArea AND id_section=''de_title'' AND vl_lookup=DeTitle;
    IF NOT FOUND THEN
      UPDATE k_member_address SET gu_contact=GuContact,gu_company=GuCompany,tx_name=TxName,tx_surname=TxSurname,
                                  de_title=DeTitle,tr_title=NULL,dt_birth=DtBirth,sn_passport=SnPassport,id_gender=IdGender,
                                  ny_age=NyAge,tx_dept=TxDept,tx_division=TxDivision,tx_comments=TxComments
      WHERE gu_address=NEW.gu_address;
    ELSE
      UPDATE k_member_address SET gu_contact=GuContact,gu_company=GuCompany,tx_name=TxName,tx_surname=TxSurname,de_title=DeTitle,
                                  tr_title=TrTitle,dt_birth=DtBirth,sn_passport=SnPassport,id_gender=IdGender,ny_age=NyAge,
                                  tx_dept=TxDept,tx_division=TxDivision,tx_comments=TxComments
      WHERE gu_address=NEW.gu_address;
    END IF;
  ELSE
      UPDATE k_member_address SET gu_contact=GuContact,gu_company=GuCompany,tx_name=TxName,tx_surname=TxSurname,
                                  de_title=NULL,tr_title=NULL,dt_birth=DtBirth,sn_passport=SnPassport,id_gender=IdGender,
                                  ny_age=NyAge,tx_dept=TxDept,tx_division=TxDivision,tx_comments=TxComments
      WHERE gu_address=NEW.gu_address;
  END IF;

  RETURN NEW;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE TRIGGER k_tr_ins_cont_addr AFTER INSERT ON k_x_contact_addr FOR EACH ROW EXECUTE PROCEDURE k_sp_ins_cont_addr()
GO;

CREATE FUNCTION k_sp_upd_comp() RETURNS OPAQUE AS '
DECLARE

  NmLegal       VARCHAR(70);
  NmCommercial  VARCHAR(70);
  IdLegal       VARCHAR(16);
  IdSector      VARCHAR(16);
  IdStatus      VARCHAR(30);
  IdRef         VARCHAR(50);
  TpCompany     VARCHAR(30);
  NuEmployees  	INTEGER;
  ImRevenue     FLOAT;
  GuSalesMan    CHAR(32);
  TxFranchise   VARCHAR(100);
  GuGeoZone     CHAR(32);
  DeCompany	VARCHAR(254);

BEGIN
  SELECT nm_legal,id_legal,nm_commercial,id_sector,id_status,id_ref,tp_company,nu_employees,im_revenue,gu_sales_man,tx_franchise,gu_geozone,de_company
  INTO NmLegal,IdLegal,NmCommercial,IdSector,IdStatus,IdRef,TpCompany,NuEmployees,ImRevenue,GuSalesMan,TxFranchise,GuGeoZone,DeCompany
  FROM k_companies WHERE gu_company=NEW.gu_company;

  UPDATE k_member_address SET nm_legal=NmLegal,id_legal=IdLegal,nm_commercial=NmCommercial,id_sector=IdSector,id_ref=IdRef,id_status=IdStatus,tp_company=TpCompany,nu_employees=NuEmployees,im_revenue=ImRevenue,gu_sales_man=GuSalesMan,tx_franchise=TxFranchise,gu_geozone=GuGeoZone,tx_comments=DeCompany
  WHERE gu_company=NEW.gu_company;

  RETURN NEW;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE TRIGGER k_tr_upd_comp AFTER UPDATE ON k_companies FOR EACH ROW EXECUTE PROCEDURE k_sp_upd_comp()
GO;

CREATE FUNCTION k_sp_upd_cont() RETURNS OPAQUE AS '
DECLARE
  GuCompany     CHAR(32);
  GuWorkArea    CHAR(32);
  TxName        VARCHAR(100);
  TxSurname     VARCHAR(100);
  DeTitle       VARCHAR(50);
  TrTitle       VARCHAR(50);
  DtBirth       TIMESTAMP;
  SnPassport    VARCHAR(16);
  IdGender      CHAR(1);
  NyAge         SMALLINT;
  TxDept        VARCHAR(70);
  TxDivision    VARCHAR(70);
  TxComments    VARCHAR(254);

BEGIN
  SELECT gu_company,gu_workarea,
         CASE WHEN char_length(tx_name)=0 THEN NULL ELSE tx_name END,
         CASE WHEN char_length(tx_surname)=0 THEN NULL ELSE tx_surname END,
         de_title,dt_birth,sn_passport,id_gender,ny_age,tx_dept,tx_division,tx_comments
  INTO   GuCompany,GuWorkArea,TxName,TxSurname,DeTitle,DtBirth,SnPassport,IdGender,NyAge,TxDept,TxDivision,TxComments
  FROM k_contacts WHERE gu_contact=NEW.gu_contact;
  
  IF DeTitle IS NOT NULL THEN
    SELECT tr_en INTO TrTitle FROM k_contacts_lookup WHERE gu_owner=GuWorkArea AND id_section=''de_title'' AND vl_lookup=DeTitle;
    IF NOT FOUND THEN
      UPDATE k_member_address SET gu_company=GuCompany,tx_name=TxName,tx_surname=TxSurname,
                                  de_title=DeTitle,tr_title=NULL,dt_birth=DtBirth,sn_passport=SnPassport,id_gender=IdGender,
                                  ny_age=NyAge,tx_dept=TxDept,tx_division=TxDivision,tx_comments=TxComments
      WHERE gu_contact=NEW.gu_contact;
    ELSE
      UPDATE k_member_address SET gu_company=GuCompany,tx_name=TxName,tx_surname=TxSurname,de_title=DeTitle,
                                  tr_title=TrTitle,dt_birth=DtBirth,sn_passport=SnPassport,id_gender=IdGender,ny_age=NyAge,
                                  tx_dept=TxDept,tx_division=TxDivision,tx_comments=TxComments
      WHERE gu_contact=NEW.gu_contact;
    END IF;
  ELSE
      UPDATE k_member_address SET gu_company=GuCompany,tx_name=TxName,tx_surname=TxSurname,
                                  de_title=NULL,tr_title=NULL,dt_birth=DtBirth,sn_passport=SnPassport,id_gender=IdGender,
                                  ny_age=NyAge,tx_dept=TxDept,tx_division=TxDivision,tx_comments=TxComments
      WHERE gu_contact=NEW.gu_contact;
  END IF;

  RETURN NEW;
END;
' LANGUAGE 'plpgsql';
GO;

CREATE TRIGGER k_tr_upd_cont AFTER UPDATE ON k_contacts FOR EACH ROW EXECUTE PROCEDURE k_sp_upd_cont()
GO;
