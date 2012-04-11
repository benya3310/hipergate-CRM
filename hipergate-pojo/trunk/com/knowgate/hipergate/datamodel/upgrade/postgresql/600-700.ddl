ALTER TABLE k_academic_courses ADD gu_supplier CHAR(32) NULL
GO;
ALTER TABLE k_academic_courses ADD nu_booked INTEGER NULL
GO;
ALTER TABLE k_academic_courses ADD nu_confirmed INTEGER NULL
GO;
ALTER TABLE k_academic_courses ADD nu_alumni INTEGER NULL
GO;
ALTER TABLE  k_suppliers ADD id_sector VARCHAR(16) NULL
GO;
ALTER TABLE k_suppliers ADD nu_employees INTEGER NULL
GO;
ALTER TABLE k_addresses ADD tx_dept VARCHAR(70) NULL
GO;
ALTER TABLE k_oportunities ADD id_message VARCHAR(254) NULL
GO;
CREATE TABLE k_x_pageset_list (
  gu_list CHAR(32)    NOT NULL,
  gu_pageset CHAR(32) NOT NULL,
  CONSTRAINT pk_x_pageset_list PRIMARY KEY (gu_list,gu_pageset)
)
GO;
DROP FUNCTION k_sp_del_meeting (CHAR)
GO;
CREATE FUNCTION k_sp_del_meeting (CHAR) RETURNS INTEGER AS '
BEGIN
  UPDATE k_activities SET gu_meeting=NULL WHERE gu_meeting=$1;
  DELETE FROM k_x_meeting_contact WHERE gu_meeting=$1;
  DELETE FROM k_x_meeting_fellow WHERE gu_meeting=$1;
  DELETE FROM k_x_meeting_room WHERE gu_meeting=$1;
  DELETE FROM k_meetings WHERE gu_meeting=$1;
  RETURN 0;
END;
' LANGUAGE 'plpgsql';
GO;
DROP FUNCTION k_sp_del_adhoc_mailing (CHAR)
GO;
CREATE FUNCTION k_sp_del_adhoc_mailing (CHAR) RETURNS INTEGER AS '
BEGIN
  UPDATE k_activities SET gu_mailing=NULL WHERE gu_mailing=$1;
  DELETE FROM k_x_adhoc_mailing_list WHERE gu_mailing=$1;
  DELETE FROM k_adhoc_mailings WHERE gu_mailing=$1;
  RETURN 0;
END;
' LANGUAGE 'plpgsql';
GO;
DROP FUNCTION k_sp_del_list (CHAR)
GO;
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

    DELETE FROM k_x_adhoc_mailing_list WHERE gu_list=bk;

    DELETE FROM k_x_pageset_list WHERE gu_list=bk;

    DELETE FROM k_lists WHERE gu_list=bk;
  END IF;

  DELETE FROM k_list_members WHERE gu_member IN (SELECT gu_contact FROM k_x_list_members WHERE gu_list=$1) AND gu_member NOT IN (SELECT x.gu_contact FROM k_x_list_members x, k_lists l WHERE x.gu_list=l.gu_list AND l.gu_workarea=wa AND x.gu_list<>$1);

  DELETE FROM k_list_members WHERE gu_member IN (SELECT gu_company FROM k_x_list_members WHERE gu_list=$1) AND gu_member NOT IN (SELECT x.gu_company FROM k_x_list_members x, k_lists l WHERE x.gu_list=l.gu_list AND l.gu_workarea=wa AND x.gu_list<>$1);

  DELETE FROM k_x_list_members WHERE gu_list=$1;

  DELETE FROM k_x_campaign_lists WHERE gu_list=$1;

  DELETE FROM k_x_adhoc_mailing_list WHERE gu_list=$1;

  DELETE FROM k_x_pageset_list WHERE gu_list=$1;

  DELETE FROM k_x_cat_objs WHERE gu_object=$1;
  UPDATE k_activities SET gu_list=NULL WHERE gu_list=$1;
  UPDATE k_x_activity_audience SET gu_list=NULL WHERE gu_list=$1;

  DELETE FROM k_lists WHERE gu_list=$1;

  RETURN 0;
END;
' LANGUAGE 'plpgsql';
GO;

ALTER TABLE k_oportunities ADD nu_oportunities INTEGER DEFAULT 1
GO;

CREATE FUNCTION k_sp_del_oportunity (CHAR) RETURNS INTEGER AS '
DECLARE
  GuContact CHAR(32);
BEGIN
  SELECT gu_contact INTO GuContact FROM k_oportunities WHERE gu_oportunity=$1;
  UPDATE k_phone_calls SET gu_oportunity=NULL WHERE gu_oportunity=$1;
  DELETE FROM k_oportunities_attachs WHERE gu_oportunity=$1;
  DELETE FROM k_oportunities_changelog WHERE gu_oportunity=$1;
  DELETE FROM k_oportunities_attrs WHERE gu_object=$1;
  DELETE FROM k_oportunities WHERE gu_oportunity=$1;
  IF GuContact IS NOT NULL THEN
    UPDATE k_oportunities SET nu_oportunities=(SELECT COUNT(*) FROM k_oportunities WHERE gu_contact=GuContact) WHERE gu_contact=GuContact;
  END IF;
  RETURN 0;
END;
' LANGUAGE 'plpgsql';
GO;
