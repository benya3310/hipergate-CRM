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