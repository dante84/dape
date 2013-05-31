package test;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class CLogin implements Serializable {
	 	
	   private static final long serialVersionUID = 1L;	   	  
	   
	   private String nombre;
	   private String año;
	   private String mes;
	   private String instrumento;
	   private String contraseña;
	   private String aplicacion;
	   private String servidor;
	   private String subInstrumento;
	   
	   private Map<String,String> mapaServidores;
	   private Map<String,String> mapaMeses;
	   private Map<String,String> mapaInstrumentos;
	   private Map<String,String> mapaInstrumentosSubtipo;
	   
	   private ConexionBase conexionBase;
	   
	   private String[] servidores = {"\\\\172.16.50.204\\","\\\\172.16.50.1\\"};
	   
	   private String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};	   	  
	   
	   private final String[] instrumentos = {
                                              "AC286","ACRESEC","ACRETSU","ACUERDO","ALI","CEAACES","CONALEP","DGESPE","ECCYPEC","ECELE","ECODEMS",
                                              "EGAL","EGEL","EGETSU","EPROM","ESPECIALES","EUC","EUCCA","EXANI","EXTRA","IFE","LEPRE_LEPRI","MCEF",
                                              "Metropolitano","MINNESOTA","OLIMPIADA","PILOTO","PREESCOLAR_BACH","PREESCOLAR_LIC","SEISP","SSP",
                                              "TRIF","UPN"
                                             };
	   
	   public CLogin(){
		      
		      mapaServidores = new HashMap<String,String>();
		      mapaInstrumentosSubtipo = new HashMap<String,String>();
		      mapaMeses = new HashMap<String, String>();
		      mapaInstrumentos = new HashMap<String,String>();		      
		      
		      mapaServidores.put(servidores[0],servidores[0]);
		      mapaServidores.put(servidores[1],servidores[1]);
		      
		      for( int i = 0; i <= meses.length - 1; i++ ){
		    	  
		    	   String month = meses[i];
		    	   mapaMeses.put(month,month);
		    	    
		      }
		      
		      for( int h = 0; h <= instrumentos.length - 1; h++ ){
		    	   String instrument = instrumentos[h];
		    	   mapaInstrumentos.put(instrument, instrument);
		      }
		      		      
		      
		     
	   }
	   
	   public String verificaUsuario(){		   		     
		    
		      ConexionBase cb;
		      Connection conexion;
		      Statement statement;
		       
		      try{
		    	   
		    	  cb = new ConexionBase();
		    	  conexion = cb.getC("172.16.50.14","replicasiipo","test","slipknot");		    	   
		    	  statement = conexion.createStatement();
		    	  
		    	  String select = "select * from usuarios where nombre = '" + nombre + "' and contraseña = '" + contraseña + "'";		    	 
		    	  
		    	  ResultSet rs = statement.executeQuery(select);		    	   		    	  		    	   		    	 
		    	  
		    	  if( !rs.isBeforeFirst() ){
		    		  FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Revisa tu nombre de usuario y/o password alguno es incorrecto"));                             
		    	  }else{
		    		    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		    		    FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Bienvenido " + nombre));
	                    return "/Contenido/index.xhtml?faces-redirect=true";
		    	  }		    	   		    	 
		    	  
		      }catch(Exception e){ e.printStackTrace(); }
		   
		      return "";
		   
	   }
	   
	   public void llenarSubIns(){    	    	         	       	         
 	      
		      mapaInstrumentosSubtipo = new HashMap<String,String>();
		     
 	          try{
 	    	  
 	    	      ConexionBase conexionBase = new ConexionBase();
 	    	      Connection con = conexionBase.getC("172.16.50.14","replicasiipo","PO","C0Xuqv5Fu3");
 	    	      Statement sta = con.createStatement();
 	    	      
 	    	      if( instrumento.equals("")){
 	    	    	  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Selecciona un examen",""));  
 	    	      }else{
 	    	      
 	    	            String select = "select nom_corto from datos_examenes where tipo_instr = '" + instrumento + "'"; 
 	    	            System.out.println(select);
 	    	            ResultSet rs = sta.executeQuery(select);
 	    	  
    	    	        mapaInstrumentosSubtipo.put("","");
 	    	      
 	    	            while( rs.next() ){
 	    		               String nomCorto = rs.getString(1);
 	    		               mapaInstrumentosSubtipo.put(nomCorto,nomCorto);
 	    	            }
 	    	  
 	    	            rs.close();
 	    	            sta.close();
 	    	            con.close();
 	    	      }
 	    	  
 	         }catch(Exception e){ e.printStackTrace(); }
 	      
       }
	   
	   public void procesarAplicacion(){
		      
		      if( aplicacion.equals("") || año.equals("") || servidor.equals("") || mes.equals("") || instrumento.equals("")  || 
		    	  subInstrumento.equals("")){
		    	  
		    	  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Ningun campo puede estar vacio",""));
  		    	  
		      }else{
  		    	    FacesContext.getCurrentInstance().addMessage(null,
  		    	    		                                     new FacesMessage("Aplicacion " + aplicacion + " servidor " + servidor + " año " + año + 
                                                                                  " mes " + mes + " examen " + instrumento + " subIns " + subInstrumento ,
                                                                                  ""));
  		    	    
  		    	    String subChange = subInstrumento.replace('_','-');
  		    	    
  		    	    String ruta = servidor + año + "\\" + instrumento + "\\" + subChange + "\\" + aplicacion ;
  		    	    
  		    	    File archivoRutaAplicacion = new File(ruta); 
  		    	    boolean existe = archivoRutaAplicacion.exists();
    		    	FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Ruta " + ruta + " existe " + existe,""));  		    	    
		    	  
		      }
		      
		   
	   }
	   
	   private void obtenDatos(String remoto) {
           
               Connection con = null;                                      
               System.out.println("En obten datos antes de obtener la conexion");
               
               ServletContext sContext = (ServletContext)((FacesContext) FacesContext.getCurrentInstance()).getExternalContext().getContext();        	          	          	     
     	       InputStream excelIs = sContext.getResourceAsStream("/WEB-INF/copiamcontrl.xlsx");
     	      
           
               try{
            	   
                   //con = conexionBase.getC(localhost,"replicasiipo","test","slipknot");
                   con = conexionBase.getC(remoto,"replicasiipo","test","slipknot");
                   Workbook wb = WorkbookFactory.create(excelIs); 
                                         
                   System.out.println("En obten datos despues de obtener la conexion");
                   Sheet hoja = wb.getSheetAt(0);      
                                         
                   Iterator<Row> rowIt = hoja.rowIterator();                                    
                   rowIt.next();

                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);                    
                                                                                                               
                   Statement statement = con.createStatement();//conectaBase();                        
                   String select = "select cve_instr from datos_examenes";                                      
               
                   select += " where nom_corto = '" + subInstrumento + "'" ;
                
         	       System.out.println("select de obtenDatos " + select);
                   ResultSet rs = statement.executeQuery(select);
               
                   int h = 0;
                   
                   while ( rs.next() ){
                   
                           int datoCve_instr = rs.getInt(1);
                    
                           for( Iterator<Row> it = rowIt; it.hasNext(); ){

                                Row r = (Row) it.next();

                                Cell cFechaInicio        = r.getCell(1); 
                                Cell cTipoAplicacion     = r.getCell(11);
                                Cell cSTipoAplcacion     = r.getCell(13);
                                Cell cInstitucion        = r.getCell(32);
                                Cell noRegistradosCell   = r.getCell(21);
                                Cell noRespuestaCell     = r.getCell(22);
                                Cell cClave_instr        = r.getCell(12);
 
                                String scTipoAplicacion  = cTipoAplicacion.getStringCellValue().trim();                                                  
                                String scSTipoAplicacion = cSTipoAplcacion.getStringCellValue().trim();
                                String scInstitucion     = cInstitucion.getStringCellValue().trim();
                                int scClave_instr        = Integer.parseInt( cClave_instr.getStringCellValue());
                                double noRegistrados     = noRegistradosCell.getNumericCellValue();
                                double noRespuesta       = noRespuestaCell.getNumericCellValue();                                                                                  

                                String valor = cFechaInicio.getStringCellValue().trim();                                                       
   
                                if( valor.length() < 7 ){ continue; }
                            
                                String mes = valor.substring(3,6);
                                String month = "";
                            
                                for( int i = 0; i <= meses.length - 1; i++ ){
                                     if( meses[i].equals(mes) ){
                                         month = String.valueOf(i + 1);
                                         if( month.length() - 1 == 0){
                                             month = "0" + month;
                                         }
                                     }
                                }
                            
                            String fecha = "20" + valor.substring(7,9) + "-" +  month  + "-" + valor.subSequence(0,2);                                                     
                            
                            Date fechaExcel = sdf.parse(fecha);
                            Calendar c = Calendar.getInstance();
                            c.setTime(fechaExcel);
                            int fem = c.get(Calendar.MONTH);
                            int cmi = -1;
                            
                            for( int i = 0; i <= meses.length - 1; i++ ){
                                 if( meses[i] == mes ){
                                	 cmi = i;
                                 }	
                            }                                                        
                                                                                                                                                   
                            if( fem == cmi && scClave_instr == datoCve_instr ){                                                                                                                                  
                    
                                Cell cApp   = r.getCell(0);
                                Object oapp = cApp.getStringCellValue();                                                                                                                                                                                            
                                                        
                                if( oapp != null ){                                 
                                    
                                    h++;
                    
                                    System.out.println( h + " " + oapp + " " + scClave_instr + " " + valor + " " + 
                                                        fecha + " " + fechaExcel);                                   
                        
                                }
                                                                                                                                                                                  
                            }                                                                                                                                     
       
                       }
                       
               }                                                                                    
                                                                                                   
       }catch(Exception e){ e.printStackTrace(); }                                                                                                                                                   
           
   }
	   	   
	   public String getNombre() {
		      return nombre;
	   }

	   public void setNombre(String nombre) {
		      this.nombre = nombre;
	   }

	   public String getContraseña() {
		      return contraseña;
	   }

	   public void setContraseña(String contraseña) {
		      this.contraseña = contraseña;
	   }

	   public String getAplicacion() {
		      return aplicacion;
	   }

	   public void setAplicacion(String aplicacion) {
		      this.aplicacion = aplicacion;
	   }

	   public String getAño() {
		      return año;
	   }

	   public void setAño(String año) {
		      this.año = año;
	   }

	   public String getMes() {
		      return mes;
	   }

	   public void setMes(String mes) {
		      this.mes = mes;
	   }

	   public Map<String,String> getMapaMeses() {
		      return mapaMeses;
	   }

	   public void setMapaMeses(Map<String,String> mapaMeses) {
		      this.mapaMeses = mapaMeses;
	   }

	   public String getInstrumento() {
		      return instrumento;
	   }

	   public void setInstrumento(String instrumento) {
		      this.instrumento = instrumento;
	   }

	   public Map<String,String> getMapaInstrumentos() {
		      return mapaInstrumentos;
	   }

	   public void setMapaInstrumentos(Map<String,String> mapaInstrumentos) {
		      this.mapaInstrumentos = mapaInstrumentos;
	   }

	   public String getSubInstrumento() {
		      return subInstrumento;
	   }

	   public void setSubInstrumento(String subInstrumento) {
		      this.subInstrumento = subInstrumento;
	   }

	   public Map<String,String> getMapaInstrumentosSubtipo() {
		      return mapaInstrumentosSubtipo;
	   }

	   public void setMapaInstrumentosSubtipo(Map<String,String> mapaInstrumentosSubtipo) {
		      this.mapaInstrumentosSubtipo = mapaInstrumentosSubtipo;
	   }
	   
	   public String getServidor() {
		   	  return servidor;
	   }

	   public void setServidor(String servidor) {
		   	  this.servidor = servidor;
	   }

	   public Map<String,String> getMapaServidores() {
		 	  return mapaServidores;
	   }

	   public void setMapaServidores(Map<String,String> mapaServidores) {
			  this.mapaServidores = mapaServidores;
	   }
	   
	   @Override
	   public boolean equals(Object obj) {		  
		      return super.equals(obj);
	   }
	   
	   @Override
	   public int hashCode() {		
		      return super.hashCode();
	   }
	   	   
}
