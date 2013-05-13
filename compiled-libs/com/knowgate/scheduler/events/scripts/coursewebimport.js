  import java.io.FileInputStream;
  import java.util.Date;
  import java.util.Properties;
  import java.sql.PreparedStatement;
  import java.sql.ResultSet;
  import com.knowgate.jdc.JDCConnection;
  import com.knowgate.dataobjs.*;
  import com.knowgate.misc.Gadgets;
  import com.knowgate.hipergate.Address;
  import com.knowgate.hipergate.DBLanguages;
  import com.knowgate.hipermail.MailAccount;
  import com.knowgate.hipermail.SendMail;
  import com.knowgate.training.Course;
  import com.knowgate.training.AcademicCourse;
  import com.knowgate.debug.StackTraceUtil;

  final String PAGE_NAME = "coursewebimport";
  final String GU_FSE = "ac1263a412bc4fcb51e100016bcb56bf";  // GUID del área de trabajo de FSE

  ThisEvent.log("Start script coursewebimport "+(new Date().toString()));

  Integer returnValue = new Integer(0);
  Integer ErrorCode = new Integer(0);
  String  ErrorMessage = "";
  
  PreparedStatement oBuscarCurso = null;
  PreparedStatement oBuscarConvocatoria = null;
  StringBuffer aNuevosCursos = new StringBuffer();

  try {
    ThisEvent.log("Getting DBWeb DataSource");
  	DBWeb oDbw = new DBWeb();
    JDCConnection oCon = oDbw.getConnection(PAGE_NAME,true); // Conexion de solo lectura contra la BB.DD. MySQL de la Web
    DBSubset oCursosWebFSE = new DBSubset("curso c, localidad l",
                                          "c.idcurso,c.lugar_implantacion,c.localidad,l.localidad AS nombre_localidad,c.titulo,c.fecha,c.fecha_curso,c.descripcion",
    																			"c.tipo=18 AND c.estado_curso=3 AND "+ "c.localidad=l.id", 100);
    final int nCursosWebFSE = oCursosWebFSE.load(oCon);
    oCon.close(PAGE_NAME);
    oDbw.close();
    ThisEvent.log("DBWeb DataSource successfully closed");
    
    ThisEvent.log("Getting states");

    DBSubset oProvincias = new DBSubset("k_lu_states", "id_state,tr_state_es", "id_country='es' ORDER BY 1", 52);
    oProvincias.load(JDBCConnection);

    ThisEvent.log("Got states");

    JDBCConnection.setAutoCommit(false);

    oBuscarCurso = JDBCConnection.prepareStatement("SELECT gu_course FROM k_courses WHERE gu_workarea='"+GU_FSE+"' AND nm_course ILIKE ?");
    oBuscarConvocatoria = JDBCConnection.prepareStatement("SELECT gu_acourse FROM k_academic_courses WHERE gu_acourse=?");
    
		for (int c=0; c<nCursosWebFSE; c++) {
			int idCurso = oCursosWebFSE.getInt("idcurso", c);
			String sTitulo = oCursosWebFSE.getString("titulo", c);
			String sDescripcion = oCursosWebFSE.getString("descripcion", c);
			String sGuCourse = null;
			int iParentesis = sTitulo.indexOf('(');
			
		  oBuscarCurso.setString(1, iParentesis>0 ? sTitulo.substring(0,iParentesis) : sTitulo );
		  ResultSet oEncontrado = oBuscarCurso.executeQuery();
		  if (oEncontrado.next()) {
		    sGuCourse = oEncontrado.getString(1);
		  }
		  oEncontrado.close();

		  if (null==sGuCourse) {
		    Course oCour = new Course();
		    oCour.put("gu_workarea", GU_FSE);
		    oCour.put("bo_active", (short) 1);
		    oCour.put("nm_course", Gadgets.left((iParentesis>0 ? sTitulo.substring(0,iParentesis) : sTitulo).toUpperCase(),100));
		    oCour.put("de_course", Gadgets.left(sDescripcion, 2000));
		    oCour.store(JDBCConnection);
				sGuCourse = oCour.getString("gu_course");
		  
		    DBPersist oOprt = new DBPersist("k_oportunities_lookup", "OportunityObjetive");
		    oOprt.put("gu_owner", GU_FSE);
		    oOprt.put("bo_active", (short) 1);
		    oOprt.put("id_section", "id_objetive");
		    oOprt.put("tp_lookup", "FSE");
		    oOprt.put("pg_lookup", DBLanguages.nextLookuUpProgressive(JDBCConnection, "k_oportunities_lookup", GU_FSE, "id_objetive"));
		    oOprt.put("vl_lookup", Gadgets.left((iParentesis>0 ? sTitulo.substring(0,iParentesis) : sTitulo).toUpperCase(),50));
		    oOprt.put("tr_en", Gadgets.left((iParentesis>0 ? sTitulo.substring(0,iParentesis) : sTitulo).toUpperCase(),50));
		    oOprt.put("tr_es", Gadgets.left((iParentesis>0 ? sTitulo.substring(0,iParentesis) : sTitulo).toUpperCase(),50));
		    oOprt.put("tx_comments", Gadgets.left(sDescripcion, 255));
		    if (!DBCommand.queryExists(JDBCConnection, "k_oportunities_lookup", "gu_owner='"+GU_FSE+"' AND id_section='id_objetive' AND vl_lookup='"+Gadgets.left((iParentesis>0 ? sTitulo.substring(0,iParentesis) : sTitulo).toUpperCase(),50)+"'"))
		      oOprt.store(JDBCConnection);
		  }

		  final String sGuACourse = "FSE18"+Gadgets.leftPad(String.valueOf(idCurso),'0',27);
		  oBuscarConvocatoria.setString(1, sGuACourse);
		  ResultSet oEncontrada = oBuscarConvocatoria.executeQuery();
		  boolean bYaExiste = oEncontrada.next();
		  oEncontrada.close();
		  if (!bYaExiste) {
				String sCodProv = oCursosWebFSE.getString("lugar_implantacion",c);
				int iCodLoca = oCursosWebFSE.getInt("localidad",c);
				String sNomLoca = oCursosWebFSE.getString(3,c);

		    final String sGuAddr = "LOC18"+Gadgets.leftPad(String.valueOf(iCodLoca),'0',27);
				Address oAddr = new Address();
				if (!oAddr.load(JDBCConnection, sGuAddr)) {
					oAddr.put("gu_address", sGuAddr);
					oAddr.put("ix_address", 1);
					oAddr.put("gu_workarea", GU_FSE);
					oAddr.put("bo_active", (short) 1);
					oAddr.put("id_country", "es");
					oAddr.put("nm_country", "ESPAÑA");
					oAddr.put("id_state", sCodProv);
					int iProv = oProvincias.binaryFind(0, sCodProv);
					if (iProv>=0) oAddr.put("nm_state", oProvincias.getString(1,iProv));
					oAddr.put("mn_city", Gadgets.left(sNomLoca,50));
				  oAddr.store(JDBCConnection);
				}

				AcademicCourse oAcour = new AcademicCourse();
				oAcour.put("gu_acourse", sGuACourse);
				oAcour.put("gu_course", sGuCourse);
		    oAcour.put("bo_active", (short) 1);
				oAcour.put("tx_start", oCursosWebFSE.getString("fecha_curso", c));
				oAcour.put("tx_end", "...");
				oAcour.put("nm_course", Gadgets.left(sTitulo,100).toUpperCase());
				oAcour.put("gu_address", sGuAddr);
		    oAcour.put("de_course", Gadgets.left(sDescripcion, 2000));
				
				oAcour.put("nu_booked", 0);
				oAcour.put("nu_confirmed", 0);
				oAcour.put("nu_alumni", 0);
				oAcour.store(JDBCConnection);

  			ThisEvent.log("added course "+oAcour.getString("nm_course")+" ("+sGuACourse+")");
				
				aNuevosCursos.append(oAcour.getString("nm_course")+"\n");
		  }
		} // next

    oBuscarConvocatoria.close();
    oBuscarConvocatoria = null;
    oBuscarCurso.close();
    oBuscarCurso=null;
		            
    JDBCConnection.commit();

    Properties oProps = new Properties();
    FileInputStream oFlIn = new FileInputStream("/etc/hipergate.cnf");
		oProps.load(oFlIn);
		oFlIn.close();

    if (aNuevosCursos.toString().length()>0)
      SendMail.send(oProps,"Nuevos cursos dados de alta:\n"+aNuevosCursos.toString(),"Nuevos cursos dados de alta","info@eoi.es","Course Web Import","info@eoi.es", new String[]{"luisc@knowgate.es"});
  }
  catch (Exception e) {  
    if (null!=oBuscarCurso) oBuscarCurso.close();
    if (null!=oBuscarConvocatoria) oBuscarConvocatoria.close();    
    if (!JDBCConnection.isClosed()) if (!JDBCConnection.getAutoCommit()) JDBCConnection.rollback;
    returnValue = new Integer(-1);
    ErrorCode = new Integer(-1);
    ErrorMessage = e.getClass().getName()+" "+e.getMessage();
	  ThisEvent.log(StackTraceUtil.getStackTrace(e));
  }

  ThisEvent.log("End script coursewebimport "+(new Date().toString()));