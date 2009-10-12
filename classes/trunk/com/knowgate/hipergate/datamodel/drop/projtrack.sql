DROP VIEW v_duty_company;
DROP VIEW v_duty_project;
DROP VIEW v_duty_resource;
DROP VIEW v_project_company;

ALTER TABLE k_projects DROP CONSTRAINT f1_projects;
ALTER TABLE k_projects DROP CONSTRAINT f2_projects;
ALTER TABLE k_projects DROP CONSTRAINT f3_projects;

ALTER TABLE k_project_expand DROP CONSTRAINT f1_project_expand;
ALTER TABLE k_project_expand DROP CONSTRAINT f2_project_expand;

ALTER TABLE k_x_duty_resource DROP CONSTRAINT f1_x_duty_resource;

ALTER TABLE k_duties_attach DROP CONSTRAINT f1_duties_attach;

ALTER TABLE k_bugs DROP CONSTRAINT f1_bugs;

ALTER TABLE k_bugs_attach DROP CONSTRAINT f1_bugs_attach;

DROP INDEX k_projects.ix1_projects;
DROP INDEX k_projects.ix2_projects;
DROP INDEX k_projects.ix3_projects;
DROP INDEX k_projects.ix4_projects;

DROP INDEX k_project_expand.i1_project_expand;
DROP INDEX k_project_expand.i2_project_expand;
DROP INDEX k_project_expand.i3_project_expand;
DROP INDEX k_project_expand.i4_project_expand;

DROP INDEX k_duties.ix1_duties;
DROP INDEX k_duties.ix2_duties;

DROP INDEX k_bugs.i1_bugs;
DROP INDEX k_bugs.i2_bugs;
DROP INDEX k_bugs.i3_bugs;

DROP TABLE k_bugs_track;
DROP TABLE k_bugs_attach;
DROP TABLE k_bugs_lookup;
DROP TABLE k_bugs_changelog;
DROP TABLE k_bugs;
DROP TABLE k_duties_workreports;
DROP TABLE k_duties_dependencies;
DROP TABLE k_duties_attach;
DROP TABLE k_duties_lookup;
DROP TABLE k_x_duty_resource;
DROP TABLE k_duties;
DROP TABLE k_project_expand;
DROP TABLE k_projects_lookup;
DROP TABLE k_project_snapshots;
DROP TABLE k_project_costs;
DROP TABLE k_projects;
