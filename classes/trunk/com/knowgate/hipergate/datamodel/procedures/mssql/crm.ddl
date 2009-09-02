INSERT INTO k_sequences (nm_table,nu_initial,nu_maxval,nu_increase,nu_current) VALUES ('seq_k_contact_refs', 10000, 2147483647, 1, 10000)
GO;

INSERT INTO k_sequences (nm_table,nu_initial,nu_maxval,nu_increase,nu_current) VALUES ('seq_k_welcme_pak', 1, 2147483647, 1, 1)
GO;

CREATE PROCEDURE k_sp_del_contact @ContactId CHAR(32) AS

  DECLARE @GuWorkArea CHAR(32)

  DELETE k_x_activity_audience WHERE gu_contact=@ContactId

  DELETE k_contact_education WHERE gu_contact=@ContactId

  DELETE k_x_duty_resource WHERE nm_resource=@ContactId

  DELETE k_welcome_packs_changelog WHERE gu_pack IN (SELECT gu_pack FROM k_welcome_packs WHERE gu_contact=@ContactId)

  DELETE k_welcome_packs WHERE gu_contact=@ContactId

  DELETE k_x_list_members WHERE gu_contact=@ContactId
  
  DELETE k_member_address WHERE gu_contact=@ContactId
  
  DELETE k_contacts_recent WHERE gu_contact=@ContactId

  SELECT @GuWorkArea=gu_workarea FROM k_contacts WHERE gu_contact=@ContactId

  DELETE k_x_group_contact WHERE gu_contact=@ContactId

  /* Borrar primero las direcciones asociadas al contacto */
  SELECT gu_address INTO #k_tmp_del_addr FROM k_x_contact_addr WHERE gu_contact=@ContactId
  DELETE k_x_contact_addr WHERE gu_contact=@ContactId
  DELETE k_addresses WHERE gu_address IN (SELECT gu_address FROM #k_tmp_del_addr)
  DROP TABLE #k_tmp_del_addr

  /* Borrar primero las cuentas bancarias asociadas al contacto */
  SELECT nu_bank_acc INTO #k_tmp_del_bank FROM k_x_contact_bank WHERE gu_contact=@ContactId
  DELETE k_x_contact_bank WHERE gu_contact=@ContactId
  DELETE k_bank_accounts WHERE nu_bank_acc IN (SELECT nu_bank_acc FROM #k_tmp_del_bank) AND gu_workarea=@GuWorkArea
  DROP TABLE #k_tmp_del_bank

  /* Los productos que contienen la referencia a los ficheros adjuntos no se borran desde aqu�,
     hay que llamar al m�todo Java de borrado de Product para eliminar tambi�n los ficheros f�sicos,
     de este modo la foreign key de la base de datos actua como protecci�n para que no se queden ficheros basura */

  DELETE k_oportunities_changelog WHERE gu_oportunity IN (SELECT gu_oportunity FROM k_oportunities WHERE gu_contact=@ContactId)
  DELETE k_oportunities_attrs WHERE gu_object IN (SELECT gu_oportunity FROM k_oportunities WHERE gu_contact=@ContactId)
  DELETE k_oportunities WHERE gu_contact=@ContactId

  DELETE k_x_cat_objs WHERE gu_object=@ContactId AND id_class=90

  DELETE k_x_contact_prods WHERE gu_contact=@ContactId
  DELETE k_contacts_attrs WHERE gu_object=@ContactId
  DELETE k_contact_notes WHERE gu_contact=@ContactId
  DELETE k_contacts WHERE gu_contact=@ContactId
GO;

CREATE PROCEDURE k_sp_del_company @CompanyId CHAR(32) AS

  DECLARE @GuWorkArea CHAR(32)

  DELETE k_x_duty_resource WHERE nm_resource=@CompanyId

  DELETE k_welcome_packs_changelog WHERE gu_pack IN (SELECT gu_pack FROM k_welcome_packs WHERE gu_company=@CompanyId)

  DELETE k_welcome_packs WHERE gu_company=@CompanyId

  DELETE k_x_list_members WHERE gu_company=@CompanyId

  DELETE k_member_address WHERE gu_company=@CompanyId
  
  DELETE k_companies_recent WHERE gu_company=@CompanyId

  SELECT @GuWorkArea=gu_workarea FROM k_companies WHERE gu_company=@CompanyId

  DELETE k_x_group_company WHERE gu_company=@CompanyId

  /* Borrar las direcciones de la compa�ia */
  SELECT gu_address INTO #k_tmp_del_addr FROM k_x_company_addr WHERE gu_company=@CompanyId
  DELETE k_x_company_addr WHERE gu_company=@CompanyId
  DELETE k_addresses WHERE gu_address IN (SELECT gu_address FROM #k_tmp_del_addr)
  DROP TABLE #k_tmp_del_addr

  /* Borrar primero las cuentas bancarias asociadas a la compa��a */
  SELECT nu_bank_acc INTO #k_tmp_del_bank FROM k_x_company_bank WHERE gu_company=@CompanyId
  DELETE k_x_company_bank WHERE gu_company=@CompanyId
  DELETE k_bank_accounts WHERE nu_bank_acc IN (SELECT nu_bank_acc FROM #k_tmp_del_bank) AND gu_workarea=@GuWorkArea
  DROP TABLE #k_tmp_del_bank

  /* Borrar las oportunidades */
  DELETE k_oportunities_changelog WHERE gu_oportunity IN (SELECT gu_oportunity FROM k_oportunities WHERE gu_company=@CompanyId)
  DELETE k_oportunities_attrs WHERE gu_object IN (SELECT gu_oportunity FROM k_oportunities WHERE gu_company=@CompanyId)
  DELETE k_oportunities WHERE gu_company=@CompanyId

  /* Borrar el enlace con categor�as */
  DELETE k_x_cat_objs WHERE gu_object=@CompanyId AND id_class=91

  /* Borrar las referencias de PageSets */
  UPDATE k_pagesets SET gu_company=NULL WHERE gu_company=@CompanyId

  DELETE k_x_company_prods WHERE gu_company=@CompanyId
  DELETE k_companies_attrs WHERE gu_object=@CompanyId
  DELETE k_companies WHERE gu_company=@CompanyId
GO;

CREATE PROCEDURE k_sp_del_oportunity @OportunityId CHAR(32) AS
  DELETE k_oportunities_changelog WHERE gu_oportunity=@OportunityId
  DELETE k_oportunities_attrs WHERE gu_object=@OportunityId
  DELETE k_oportunities WHERE gu_oportunity=@OportunityId
GO;

CREATE PROCEDURE k_sp_del_sales_man @SalesManId CHAR(32) AS
  DELETE k_sales_objectives WHERE gu_sales_man=@SalesManId
  DELETE k_sales_men WHERE gu_sales_man=@SalesManId
GO;

CREATE PROCEDURE k_sp_del_supplier @SupplierId CHAR(32) AS
  DECLARE @GuAddress CHAR(32)
  SELECT @GuAddress=gu_address FROM k_suppliers WHERE gu_supplier=@SupplierId
  DELETE k_x_duty_resource WHERE nm_resource=@SupplierId
  DELETE k_suppliers WHERE gu_supplier=@SupplierId
  DELETE k_addresses WHERE gu_address=@GuAddress
GO;
