package test;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.primefaces.component.datatable.DataTable;
import org.springframework.web.context.ContextLoader;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.VerticalPositionMark;

public class CAplicacion implements Serializable{
	                   
	   private static final long serialVersionUID = 1L;
	   private boolean registro = false,respuesta = false,datsErraticos = false,mControlNoDat = false;
       private boolean bGuardarDisabled,tieneCero;       
       
       private int imagenesExistenR = 0,imagenesExistenS = 0,valorMControlR = 0,valorMControlS = 0,numeroPosiciones = 1,posicionesExcel = 0;
       private int posicionesRegistro = 0,posicionesRespuesta = 0,posicionesRegistroBPM = 0,posicionesRespuestaBPM = 0;
       private int posicionesRegistroMc = 0,posicionesRespuestaMc = 0;
	          
	   private String año;
	   private String mes;
	   private String instrumento;	   
	   private String aplicacion;
	   private String servidor;
	   private String subInstrumento;
	         
       private String scTipoAplicacion,scSTipoAplicacion,scInstitucion,fecha,rutaApp,estado,fechaAlterna;
       private int scClave_instr;
       private double noRegistrados,noRespuesta;
       private Date fechaExcel;
       private Aplicacion app;
       
       private List<Aplicacion> valores;
	   private Map<String,String> mapaServidores;
	   private Map<String,String> mapaMeses;
	   private Map<String,String> mapaInstrumentos;
	   private Map<String,String> mapaInstrumentosSubtipo;
	   private List<String> valoresLista;
	   
	   private SimpleDateFormat sdf;
	   
	   private DataTable tablaValores;
	   
	   private Workbook wb;
	   
	   private Map<Object,Object> aplicacionesInexistentes = new HashMap<>(),mapaAplicacionesPosicionesDesfazadas = new HashMap<>();

	   @ManagedProperty(value="#{cl}")
	   private CLogin loginBean; 
	   
	   private ConexionBase conexionBase;
	   
	   private String[] servidores = {"\\\\172.16.50.204\\","\\\\172.16.50.1\\"};
	   
	   private String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};	   	  
	   
	   private final String[] instrumentos = {
                                              "AC286","ACRESEC","ACRETSU","ACUERDO","ALI","CEAACES","CONALEP","DGESPE","ECCYPEC","ECELE","ECODEMS",
                                              "EGAL","EGEL","EGETSU","EPROM","ESPECIALES","EUC","EUCCA","EXANI","EXTRA","IFE","LEPRE_LEPRI","MCEF",
                                              "Metropolitano","MINNESOTA","OLIMPIADA","PILOTO","PREESCOLAR_BACH","PREESCOLAR_LIC","SEISP","SSP",
                                              "TRIF","UPN"
                                             };
	   	   
	   public CAplicacion(){
		   		      		 		      
		      valores = new ArrayList<Aplicacion>();
		      tablaValores = new DataTable();
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
		      
		      bGuardarDisabled = true;
		      			    
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
		    	  
		    	  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Ningun campo puede estar vacio.Verifica por favor",""));
		    	  
		      }else{
		    	  		    	     
		    	    String subChange = subInstrumento.replace('_','-');
		    	    String ruta = "";
		    	    
		    	    boolean esEgel = false;
		    	    if( esEgel = instrumento.equals("EGEL") ){ ruta = servidor + año + "\\" + instrumento + "\\"; }
		    	    else{ ruta = servidor + año + "\\" + instrumento + "\\" + subChange + "\\" ; }
		    	    
		    	    File archivoRutaAplicacion = new File(ruta); 
		    	    boolean existe = archivoRutaAplicacion.exists();
 		    	  
		    	    System.out.println("ruta " + ruta + " existe " + existe);
		    	    
		    	    if( existe ){
		    	    	 
		    	    	rutaApp = "";
		    	    	
		    	    	for( int i = 0; i <= ruta.length() - 1; i++ ){
		    	    		 char car = ruta.charAt(i);
		    	    		 if( car == '\\'){ rutaApp += "\\\\"; }
		    	    		 else{ rutaApp += car; }
		    	    	}
		    	    	
		    	    	System.out.println("rutaApp " + rutaApp);
		    	    	
 		    	        obtenDatos();
 		    	        cuentaPosiciones(ruta,esEgel);
 		    	        
 		    	        if( cuentaImagenes(ruta,instrumento,subInstrumento) ){
 		    	    	 		    	    	    		    	    	
 		    	    	    String state = "";
 		    	    	    if( datsErraticos || mControlNoDat ){ state = "Verificar";	}
 		    	    	    else{ state = "Correcto"; }
 		    	    	
 		    	    	    estado = state;
 		    	    	    
 		    	    	    if( (instrumento.equals("EXANI") || instrumento.equals("EGEL")) ){
 	                        	 posicionesRegistro -= 1;
 	                        	 posicionesRespuesta -= 1;
 	                        	 
 	                        	 if( posicionesRegistro < 0 ){ posicionesRegistro = 0;}
 	                        	 if( posicionesRespuesta < 0 ){ posicionesRespuesta = 0;}
 	                        }
 		    	    	    
 		    	    	    System.out.println(aplicacion + " " + imagenesExistenR + " " + imagenesExistenS + " " + posicionesRegistro + " " + 
  	    	                                   posicionesRegistroBPM + " " + posicionesRegistroMc + " " + + posicionesRespuesta + " " + 
 		    	    			               posicionesRespuestaBPM + " " +  posicionesRespuestaMc + " " + state);
  	    	
 		    	    	
 		    	    	    
 		    	    	    
 		    	    	    app = new Aplicacion(aplicacion,
 		    	    		  	                            String.valueOf(imagenesExistenR),
 		    	    			                            String.valueOf(imagenesExistenS),
 		    	    			                            String.valueOf(posicionesRegistro),
 		    	    			                            String.valueOf(posicionesRegistroBPM),
 		    	    			                            String.valueOf(posicionesRegistroMc),
 		    	    			                            String.valueOf(posicionesRespuesta),
 		    	    			                            String.valueOf(posicionesRespuestaBPM),
 		    	    			                            String.valueOf(posicionesRespuestaMc),
 		    	    			                            state);
 		    	    	    
 		    	    	    valoresLista = new ArrayList<String>(); 		    	    	   		    	    	   
 		    	    	    valoresLista.add(String.valueOf(imagenesExistenR));
 		    	    	    valoresLista.add(String.valueOf(imagenesExistenS));
 		    	    	    valoresLista.add(String.valueOf(posicionesRegistro)); 		    	    	    
 		    	    	    valoresLista.add(String.valueOf(posicionesRegistroMc));
 		    	    	    valoresLista.add(String.valueOf(posicionesRespuesta)); 		    	    	    
 		    	    	    valoresLista.add(String.valueOf(posicionesRespuestaMc));
 		    	    	    valoresLista.add(state);
 		    	    	
 		    	    	    getValores().add(app); 	 		    	    	      	    	  
 		    	    	
 		    	        }else{
    		    	          FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("El numero de aplicacion " + aplicacion + 
    		    	    	   	                                                                 " no existe.Verifica por favor",""));
 		    	        }
 		    			    	  
		            }	
		    	    
		      }
		   
	   }
	   
	   private void obtenDatos() {
        		      
               Connection con = null;              
               System.out.println("En obten datos antes de obtener la conexion");
            
//             ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();        	          	          	     
//  	       InputStream excelIs = ec.getResourceAsStream("/WEB-INF/copiamcontrl.xlsx");
  	       
  	           ServletContext sContext = (ServletContext)((FacesContext) FacesContext.getCurrentInstance()).getExternalContext().getContext();        	          	          	     
 	           InputStream excelIs = sContext.getResourceAsStream("/WEB-INF/copiamcontrl.xlsx");
  	                      	       
  	           String remoto = "172.16.50.14";
  	       
               try{
           	   
             	   conexionBase = new ConexionBase();
                   //con = conexionBase.getC(localhost,"replicasiipo","test","slipknot");
                   con = conexionBase.getC(remoto,"replicasiipo","test","slipknot");
                   wb = WorkbookFactory.create(excelIs); 
                                      
                   System.out.println("En obten datos despues de obtener la conexion");
                   Sheet hoja = wb.getSheetAt(0);      
                                      
                   Iterator<Row> rowIt = hoja.rowIterator();                                    
                   rowIt.next();

                   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);                    
                                                                                                            
                   Statement statement = con.createStatement();                        
                   String select = "select cve_instr from datos_examenes";                                      
            
                   select += " where nom_corto = '" + subInstrumento + "'" ;
             
      	           System.out.println("select de obtenDatos " + select);
                   ResultSet rs = statement.executeQuery(select);
            
                   int h = 0;
                
                   while ( rs.next() ){
                
                           int datoCve_instr = rs.getInt(1);
                 
                           for( Iterator<Row> it = rowIt; it.hasNext(); ){

                                Row r = (Row)it.next();
  
                                Cell cFechaInicio        = r.getCell(1); 
                                Cell cTipoAplicacion     = r.getCell(11);
                                Cell cSTipoAplcacion     = r.getCell(13);
                                Cell cInstitucion        = r.getCell(32);
                                Cell noRegistradosCell   = r.getCell(21);
                                Cell noRespuestaCell     = r.getCell(22);
                                Cell cClave_instr        = r.getCell(12);

                                String scTipoAplicacion  = cTipoAplicacion.getStringCellValue().trim();                                                  
                                String scSTipoAplicacion = cSTipoAplcacion.getStringCellValue().trim();
                                scInstitucion            = cInstitucion.getStringCellValue().trim();
                                int scClave_instr        = Integer.parseInt( cClave_instr.getStringCellValue());
                                double noRegistrados     = noRegistradosCell.getNumericCellValue();
                                double noRespuesta       = noRespuestaCell.getNumericCellValue();                                                                                  

                                String valor = cFechaInicio.getStringCellValue().trim();                                                       

                                if( valor.length() < 7 ){ continue; }                         
                               
                                String month = "";                                                            
                                int cmi = -1;
                                
                                for( int i = 0; i <= meses.length - 1; i++ ){                             	         
                                     if( meses[i].equals(mes) ){
                                    	 cmi = i;
                                         month = String.valueOf(i + 1);
                                         if( month.length() - 1 == 0){
                                             month = "0" + month;
                                         }
                                     }
                                }
                         
                                fecha = "20" + valor.substring(7,9) + "-" +  month  + "-" + valor.subSequence(0,2);                                                     
                         
                                fechaExcel = sdf.parse(fecha);
                                Calendar c = Calendar.getInstance();
                                c.setTime(fechaExcel);
                                int fem = c.get(Calendar.MONTH);
                                                                                                                               
                                if( fem == cmi && scClave_instr == datoCve_instr ){                                                                                                                                  
                 
                                    Cell cApp   = r.getCell(0);
                                    Object oapp = cApp.getStringCellValue();                                                                                                                                                                                            
                                                     
                                    if( oapp != null && oapp.equals(aplicacion) ){                                 
                                  
                                        h++;
                 
                                        System.out.println( h + " " + oapp + " " + scClave_instr + " " + valor + " " + 
                                                            fecha + " " + fechaExcel + " " + scInstitucion);         
                                                          
                                        fechaAlterna = fecha;
                                        registro = true;                                        
                                        respuesta = true;                                                                                                                                                                                                                              
                     
                                    }
                                                                                                                                                                               
                                }                                                                                                                                     
    
                           }
                    
                   }                                                                                    
                                                                                                
               }catch(Exception e){ e.printStackTrace(); }                                                                                                                                                   
        
       }
	   
	   private boolean cuentaImagenes(String ruta,String instrumento,String subInstrumento){
                                                                                                                                                                                                       
               try{
                          
            	    System.out.println(" ruta " + ruta + "\\" + aplicacion );
            	    
                    File appDir = new File(ruta + "\\" + aplicacion);
                    boolean existe = appDir.exists();                                                                                                  
                      
                    if( !existe ){ 
                    	return false; 
                    }else{
                          boolean esDir = appDir.isDirectory();                       	  
                          if( esDir ){                                                                                                                                  
                              File[] archivos = appDir.listFiles();                                                                                                                          
                              for( File f : archivos ){                                                                                                      
                                   String nombreArchivo = f.getName();  
                                
                                   if( instrumento.equals("EGEL") ){
                                	   if( nombreArchivo.matches("\\d{6}\\_[Rr][Xx]\\d{2}\\.[t][i][f]") ){ imagenesExistenR++; }                                                                                                                                                                                                                                                                                                                    
                                       if( nombreArchivo.matches("\\d{6}\\_[Ss][Xx]\\d{2}\\.[t][i][f]") ){ imagenesExistenS++; }
                                   }else{
                                         if( nombreArchivo.matches("\\d{6}\\_[Rr]\\d{3}\\.[t][i][f]") ){ imagenesExistenR++; }                                                                                                                                                                                                                                                                                                                    
                                         if( nombreArchivo.matches("\\d{6}\\_[Ss]\\d{3}\\.[t][i][f]") ){ imagenesExistenS++; }
                                   }
                                                                     
                              }
                                                                                                                                                  
                          }                                           
                                                           
                     }
                    
                     conexionBase = new ConexionBase();
                     Connection con = conexionBase.getC("172.16.50.14","replicasiipo","PO","C0Xuqv5Fu3");                     
                     Statement sta = con.createStatement();
                     String select = "select canImaReg,canImaRes from datos_examenes where nom_corto = '" + subInstrumento + "'";
                     System.out.println(select);
                     
                     ResultSet rs = sta.executeQuery(select);
                     
                     int regDebenExistir = 0;
                     int resDebenExistir = 0;
                     
                     if( rs.first() ){
                    	                            
                    	 int pregRs = Integer.valueOf(rs.getString(1));
                    	 int presRs = Integer.valueOf(rs.getString(2));
                    	                     	 
                         if( (instrumento.equals("EXANI") || instrumento.equals("EGEL")) ){
                        	 regDebenExistir = (posicionesRegistro  - 1) * pregRs;
                        	 resDebenExistir = (posicionesRespuesta - 1) * presRs;
                         }else{
                        	   regDebenExistir = posicionesRegistro  * pregRs;
                        	   resDebenExistir = posicionesRespuesta * presRs;                                                           
                         }
                         
                         System.out.println("preg " + posicionesRegistro + " pres " + posicionesRespuesta + " posicionesRegistro " + posicionesRegistro + 
                        		            " posicionesRespuesta " + posicionesRespuesta);                                                  
                         System.out.println("ImagReg del query " + rs.getString(1) + " existen " + imagenesExistenR + " debenExistir " + regDebenExistir);
                         System.out.println("ImagRes del query " + rs.getString(2) + " existen " + imagenesExistenS + " debenExistir " + resDebenExistir);
                                                  
                         if( (regDebenExistir != imagenesExistenR) || (resDebenExistir != imagenesExistenS) ){
                        	  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Las imagenes no empatan.Verifica por favor",""));
                        	  return false;
                         }
                         
                     }
               
               }catch(Exception e){ e.printStackTrace(); }    
               
               return true;
   
       }
	   
	   private void cuentaPosiciones(String rutaDatif,boolean esEgel){
                                                                                                                                                                                                                                                            
               rutaDatif += "\\" + aplicacion + "\\DATIF";
               File datif = new File(rutaDatif);                                                                                          
                
               boolean existeDatif = datif.exists();                                                                                     
                  
               if( existeDatif ){
                   datif.getAbsolutePath();                     
                   File[] archivos = datif.listFiles(                            		   
	 	                  new FileFilter() {
		                     @Override
                             public boolean accept(File pathname) {                                     
                                    if( pathname.getName().endsWith(".dat") ){ return true; }                                          
                                        return false;                                        
                             }

                         }
                           
                   );                                                                                            
                                                                 
                   int r = -1;
                   int S = -1;
                   int la = (archivos.length - 1);                          
                                                                                         
                   if( la == -1 && 
                       ( ( registro  && Double.valueOf( noRegistrados  ) > 0 ) || 
                       ( ( respuesta && Double.valueOf( noRespuesta    ) > 0 ) ) ) ) {      
                          System.out.println("No hay dats " + aplicacion);                          
                   }
                   
                   for( int m = 0; m <= la; m++ ){
   
                        String nombreArchivo = archivos[m].getName();                                                                                                            
                        String subNombreArchivo = "";
    
                        for( int i = 0; i <= nombreArchivo.length() - 5; i++ ){
                             subNombreArchivo += nombreArchivo.charAt(i);
                        }                                                                                                        
                                   
                        char ci = nombreArchivo.charAt(0);
                                                       
                        if( subNombreArchivo.matches("[Rr]\\d{9}[Xx][_\\d]") || subNombreArchivo.matches("[Ss]\\d{9}[Xx][_\\d]") ){ 
        
                            String c = "";
                            c += ci;
           
                            if( c.matches("[RrSs]") ){                                                       
                                if( "r".equals(c) || "R".equals(c) ){                                                                 
                                     r++;                                 
                                }   
                                if( "s".equals(c) || "S".equals(c) ){                                                               
                                     S++;
                                }
                            }                                                                                          
        
                        }else{                                     
                              datsErraticos = true;
                              continue;
                        }   
                                                                                                          
                   }
                                      
                   System.out.println("Es " + respuesta +" S " + S + " " + noRespuesta );
                   if( respuesta && S == -1 && noRespuesta > 0 ){
                       System.out.println("No hay dats de respuestas en " + aplicacion);
                       mControlNoDat = true;
                   }
                                                                       
                   System.out.println("Es " + registro +" r " + r + ( Double.valueOf( noRegistrados ) >= 0 ) );
                   if( registro && r == -1 && Double.valueOf(noRegistrados) > 0){
                       System.out.println("No hay dats de registros en " + aplicacion);
                       mControlNoDat = true;
                   }
                      
                   if( la == -1 ){
                    	  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("La aplicacion " + aplicacion + " archivos dat." +
                                                                                             "Verifica por favor",""));
                   }else{    
                     	 
                         int i = 0;
                           
                         while( i <=  r ){                               
                                String nombreArchivo = archivos[i].getName();                                                                                                       
                                valorMControlR = leeArchivo(nombreArchivo,rutaDatif,aplicacion,i,r,"R");
                                i++;
                         }                                                            
   
                         while( i <= la ){                               
                                String nombreArchivo = archivos[i].getName();                                                                                                                                                                                    
                                valorMControlS = leeArchivo(nombreArchivo,rutaDatif,aplicacion,i,la,"S");
                                i++;
                         }
   
                   }
                   
               }else{
                 	 FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("La aplicacion " + aplicacion + " no tienen folder DATIF." +
                                                                                        "Verifica por favor",""));	  
               }                                                                                     
                                                                                                                                                                                                
       }
          
       @SuppressWarnings("resource")
	   public int leeArchivo(String nombreArchivo,String rutaDatif,Object f,int i,int noArchivos,String tipo){               

              String linea = "";                                                 
              int temp;                                                                                                                                                                                                         

              try{         
                           
                  File f1 = new File(rutaDatif + "\\" + nombreArchivo);                                    
				  FileInputStream fis = new FileInputStream(f1);                       

                  while(true){

                        temp = fis.read();                                                                   
                    
                        if( temp == -1 ){                              
                            break;
                        }

                        int digitoSub;
                        linea += (char)temp;                                                                                                          

                        if( temp == '\n' ){ 
                             
                            String sub = linea.substring(3,9);                            
                            digitoSub  = Integer.parseInt(sub);                               

                            if( digitoSub == 0 ){
//                            	original                            	
//                            	numeroPosiciones -= 2;
//                            	tieneCero = true;
//                            	
//                            	Nuevo continue
                            	linea = "";
                            	continue;
                            }

                            numeroPosiciones++;                                                                                
                              
                            if( digitoSub != numeroPosiciones ){ 
 	                            mapaAplicacionesPosicionesDesfazadas.put(f,f);
                            }
                                                              
                            linea = "";

                        }                                                        

                  }                                                                                                         
              
                  posicionesExcel = mcExcelPosiciones((String)f, "2012",tipo);                                                                                          
           
                  if( i == noArchivos ){ 

                      int posiciones = revisaBpmPosiciones((String)f, "2012",tipo);
                                                                                                                                                
                      if( tipo.equals("R") ){                                   	 
                          posicionesRegistro = numeroPosiciones;
                          posicionesRegistroBPM = posiciones;
                          posicionesRegistroMc = posicionesExcel;
                      }else{
                            posicionesRespuesta = numeroPosiciones;
                            posicionesRespuestaBPM = posiciones;
                            posicionesRespuestaMc = posicionesExcel;
                      }  

                      numeroPosiciones = 1;
                         
                  }                                   
                                                                                                                                                                                                                                                                                                                                                                                                                                                                               
              }catch(IOException | NumberFormatException e){ e.printStackTrace(); }                              
                       
              return posicionesExcel;

       }
   
       public int mcExcelPosiciones(String app,String año,String tipo){ 

              int posiciones = 0;                                                                               

              try{
                               
                  Sheet hoja = wb.getSheetAt(0);                  
                  Iterator<Row> rowIt = hoja.rowIterator();                                                 
              
                  rowIt.next();
                  for(Iterator<Row> it = rowIt; it.hasNext(); ){
                      Row r = it.next();
                      Cell cAplicacion = r.getCell(0);
                      String cvc = cAplicacion.getStringCellValue().trim();                                             
                      if( cvc.matches("^[0-9]+$") ){
                          if( Integer.parseInt(cvc) == Integer.parseInt(app) ){                                                     
                              Cell cPosiciones;
                              if( tipo.equals("R") ){ cPosiciones = r.getCell(21); }
                              else{ cPosiciones = r.getCell(22);}
                              posiciones += cPosiciones.getNumericCellValue();                     
                          }
                      }
                  
                  }

              }catch(Exception e){ e.printStackTrace(); }                                          
          
              return posiciones;

       }    
   
       public int revisaBpmPosiciones(String aplicacion,String año,String tipo){

              Connection c;
              Statement s;
              ResultSet rs;                                                        

              int posiciones = 0;

              try{

                  Class.forName("oracle.jdbc.OracleDriver");                   
                  c = DriverManager.getConnection("jdbc:oracle:thin:@10.10.2.10:1521:ceneval","dpoc","bpm_DPOC");

                  s = c.createStatement();

                  String select = "";

                  if( tipo.equals("R") ){ 
                      select += "select \"Registrado_desglose\",\"Registrado\" from dpoc where NUM_APLIC = '" + 
                      aplicacion + "' and extract(year from \"fecha_de_inicio\") ='" + año + "'";
                  }else{
                        select += "select \"Aplicados_desglose\",\"Aplicados\" from dpoc where NUM_APLIC = '" + aplicacion + "' and " + 
                                  " to_char(\"fecha_de_inicio\",'YYYY') = '" + año + "'";
                  }

                  rs = s.executeQuery(select);
          
                  int i = 0;                      

                  while( rs.next() ){
                         i++;
                         if( i > 1 ){                             
                             posiciones =  rs.getInt(1);                             
                             break;
                         }else{ posiciones = rs.getInt(2); }                             

                  }         

              }catch(ClassNotFoundException | SQLException e){ e.printStackTrace(); }

              return posiciones;

       }
       
       public void guardarDatos(){
    	        	      
    	      sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.ENGLISH);
    	      String fActual = sdf.format(new Date());
    	      
    	      try{
    	    	  
    	    	  conexionBase = new ConexionBase();
    	    	  Connection con = conexionBase.getC("172.16.50.14","ceneval","PO","C0Xuqv5Fu3");
    	    	  Statement sta = con.createStatement();
    	    	  
    	    	  String select =  "select no_aplicacion from viimagenes where no_aplicacion = '";
                                                                                                                                        
                  select += aplicacion + "'";
                  System.out.println(select);                      
                  ResultSet rs = sta.executeQuery(select);                                                     
                       
                  if( !rs.isBeforeFirst() ){
                           
                      String insert = "insert into viimagenes(no_aplicacion,instrumento,nombre,fecha_alta," +
                                      "fecha_registro,imag_reg,imag_res,pregistro,pregistrobpm,pregistromcontrol," + 
                                      "prespuesta,prespuestabpm,prespuestamcontrol,ruta,institucion,estado) values('";
                                                              
                            
                      insert += aplicacion + "','" + instrumento + "','" + subInstrumento + "','" + fecha + "','" + fActual + "','" + 
                                imagenesExistenR + "','" + imagenesExistenS + "','" + posicionesRegistro + "','" + posicionesRegistroBPM + "','" + 
                                posicionesRegistroMc + "','" + posicionesRespuesta + "','" + posicionesRespuestaBPM + "','" + posicionesRespuestaMc + "')";
                      
                      System.out.println(insert);
                      int resultado = sta.executeUpdate(insert);    
                      
                      if( resultado > 0 ){
                    	  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Datos insertados correctamente",""));
                      }else{ FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Hubo un error al insertar los datos.Contacta al" +
                    	                                                                        " Administrador del sistema","")); }
                                                                                                                                                                                                                                                                               
                  }else{
                               
                	    String update = "update viimagenes set ";                	                                                    	
                        System.out.println(aplicacion + " existe");                              
                               
                        update += "instrumento = '" + instrumento + "',nombre = '" + subInstrumento + "',fecha_alta = '" + fecha + 
                                  "',fecha_registro = '" + fActual +"', imag_reg = '" + imagenesExistenR + "', imag_res = '" + imagenesExistenS + 
                            	  "', pregistro = '" + posicionesRegistro + "', pregistrobpm = '" + posicionesRegistroBPM + 
                                  "',pregistromcontrol = '" + posicionesRegistroMc + "',prespuesta = '" + posicionesRespuesta +
                            	  "',prespuestabpm = '" + posicionesRespuestaBPM + "',prespuestamcontrol = '" + posicionesRespuestaMc +
                            	  "',ruta = '" + rutaApp + "', institucion = '" + scInstitucion + "', estado = '" + estado + "'";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
                               
                        System.out.println(update);
                        int resultado = sta.executeUpdate(update);                                                                                                 
                        
                        if( resultado > 0 ){
                      	    FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Datos actualizados correctamente",""));
                        }else{ 
                        	  FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Hubo un error al actualizar los datos.Contacta al" +
                      	                                                                         " Administrador del sistema",""));
                        }
                                                                                                                                                                                                                                                                                                                                      
                  }                                                                                                          
                                                                                              
                  rs.close();
                                        	    	  
    	      }catch(Exception e){ e.printStackTrace(); }
    	   
       }
       
       public void generarReporte(){
    	   
    	      HttpSession session = (HttpSession)FacesContext.getCurrentInstance().getExternalContext().getSession(true);		      		      		      
		      CLogin cl = (CLogin)session.getAttribute("cl");
		      		      		        	    
    	      FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("En generarReporte " + aplicacion,""));
    	      FacesContext context = FacesContext.getCurrentInstance(); 
    	       
    	      HttpServletResponse response = (HttpServletResponse)context.getExternalContext().getResponse();  
    	      response.setContentType("application/pdf");  
    	      response.setHeader("Content-disposition","filename=Reporte.pdf");
    	      
              try{
            	  
            	  ByteArrayOutputStream baos = new ByteArrayOutputStream();
            	  
    	    	  SimpleDateFormat sdff = new SimpleDateFormat("dd/MM/yyyy");
    	    	  SimpleDateFormat sdfh = new SimpleDateFormat("HH:mm:ss");
    	    	  
    	  		  String fCadena = sdff.format(new Date());
    	  		  String hCadena = sdfh.format(new Date());
    	  		  
    	    	  Document pdf = new Document();
  		          pdf.setPageSize(PageSize.A4);
  		          
  		          PdfWriter.getInstance(pdf,baos);
  		             	
  		          Chunk espacio1 = new Chunk(new VerticalPositionMark(),120,false);  		            		          
  		          Chunk espacio2 = new Chunk(new VerticalPositionMark(),200,true);  		          	
  		            		            		          
  		          Phrase fraseEncabezado = new Phrase();
  		          
  		          FontFactory.register("C:\\\\Windows\\\\Fonts\\\\AGENCYB.TTF","agencyb");
  		          FontFactory.register("C:\\\\Windows\\\\Fonts\\\\AGENCYR.TTF","agencyr");  
  		          Font agencyFb = FontFactory.getFont("agencyb");
  		          agencyFb.setSize(16f);
  		          
  		          fraseEncabezado.add(new Chunk(espacio1));  		          
  		          fraseEncabezado.add(new Chunk("DIRECCION DE PROCESOS ÓPTICOS Y CALIFICACIÓN",agencyFb));
  		            		          
		          Font agencySubh = FontFactory.getFont("agencyb");
		          agencySubh.setSize(12f);
		          
  		          fraseEncabezado.add(new Chunk(espacio2));  		         
  		          fraseEncabezado.add(new Chunk("   - PROCESOS ÓPTICOS -",agencySubh));
  		            		         
  		          HeaderFooter encabezado = new HeaderFooter(fraseEncabezado,false);  		            		                              		           		          
  		           
  		          pdf.setHeader(encabezado);
  		            		           
  		          pdf.open();  		          
  		            		            		            		          
  		          Font agencyDatos= FontFactory.getFont("agencyb");
		          agencySubh.setSize(10f);		            
		          
		          Paragraph parrafoInformacion = new Paragraph();
		          Phrase fraseInformacion = new Phrase();
		          
		          fraseInformacion.setFont(agencyDatos);		          		          
		          fraseInformacion.add("                                                                    Validación de imagenes de lectura óptica");
		          
		          parrafoInformacion.add(fraseInformacion);
		          
		          pdf.add(parrafoInformacion);
		          pdf.add(Chunk.NEWLINE);		          
		          
		          Paragraph parrafoTablaEncabezado = new Paragraph();
		          PdfPTable tablaEncabezado = new PdfPTable(3);
		          tablaEncabezado.setWidthPercentage(100);
		          
		          Phrase fraseAplicacion = new Phrase("Número de aplicación: " + aplicacion,agencyDatos);    	                	          
 	              PdfPCell celdaApp = new PdfPCell(fraseAplicacion);
   	              tablaEncabezado.addCell(celdaApp);
   	              
   	              System.out.println("La fecha en reporte " + fechaAlterna);
    	          Phrase fraseFecha = new Phrase("Fecha de aplicación: " + fechaAlterna,agencyDatos);    	                	          
	              PdfPCell celdaFecha = new PdfPCell(fraseFecha);
	              tablaEncabezado.addCell(celdaFecha);
	              
	              Phrase fraseExamen = new Phrase("Tipo de examen: " + subInstrumento,agencyDatos);    	                	          
	              PdfPCell celdaExamen = new PdfPCell(fraseExamen);
	              tablaEncabezado.addCell(celdaExamen);
	              
   	              parrafoTablaEncabezado.add(tablaEncabezado);
   	        
   	              pdf.add(parrafoTablaEncabezado);
   	              pdf.add(Chunk.NEWLINE);
		          		          
		          Paragraph parrafoTabla = new Paragraph();
		          PdfPTable tablaPdf = new PdfPTable(2);
		          
		          tablaPdf.setWidthPercentage(100);
		          //tablaPdf.setTotalWidth((PageSize.A4.getWidth() - pdf.leftMargin() - pdf.rightMargin()) * tablaPdf.getWidthPercentage() / 100);
		          
		          String[] encabezados = {"Nombre dato","Numero"};
    
                  Font fuenteEncabezados = FontFactory.getFont("agencyb");               
                  fuenteEncabezados.setSize(10f);
                  
                  for( int i = 0; i <= (encabezados.length - 1); i++ ){
                       Phrase fraseEncabezados = new Phrase();
                       fraseEncabezados.setFont(fuenteEncabezados);
          	           fraseEncabezados.add(encabezados[i]);
          	           PdfPCell celda = new PdfPCell(fraseEncabezados);
                       celda.setFixedHeight(20);  	    	      	            	    	
          	           tablaPdf.addCell(celda);                	   
                  }
		                 
                  String[] datosNombres = {"Imagenes registro","Imagenes respuesta","Cantidades registro",
                		                   "Cantidades registro MControl","Cantidades respuesta","Cantidades respuesta MControl","Estado"};
                  
                  Font agencyr = FontFactory.getFont("agencyr");
                  agencyr.setSize(12f);
                  
                  int i = 0;
                  for( String valor : valoresLista ){
                	  
                	   Phrase frase = new Phrase(datosNombres[i],agencyr);    	                	          
      	               PdfPCell celda = new PdfPCell(frase);
        	           tablaPdf.addCell(celda);
        	           
        	           frase = new Phrase(valor,agencyr);    	                	          
      	               celda = new PdfPCell(frase);
        	           tablaPdf.addCell(celda);
        	           
        	           i++;
        	                           	   
                  }
                                  	               	                            
      	          pdf.add(Chunk.NEWLINE);
      	          parrafoTabla.add(tablaPdf);
                  pdf.add(parrafoTabla);
                             
                  for( i = 0; i <= 23; i++ ){
                       pdf.add(Chunk.NEWLINE);
                  }                 
                                    
                  Paragraph parrafoTablaFoot = new Paragraph();
		          PdfPTable tablaFoot = new PdfPTable(2);
		          tablaFoot.setWidthPercentage(100);
		          
		          conexionBase = new ConexionBase();
		          Connection con = conexionBase.getC("172.16.50.14","replicasiipo","PO","C0Xuqv5Fu3");
		          String select = "select nombre_completo from usuarios where nombre = '" + cl.getNombre().trim() + "'";
		          System.out.println(select);
		          String name = "";
		          
		          try {
					   Statement sta = con.createStatement();
					   ResultSet rs = sta.executeQuery(select);
					   if( rs.first() ){
					       name = rs.getString(1);
					   }
				  }catch (SQLException e){ e.printStackTrace();	}
		          
		          Phrase fraseFoot = new Phrase("Nombre del usuario: " + name,agencyr);    	                	          
 	              PdfPCell celdaFoot = new PdfPCell(fraseFoot);
 	              celdaFoot.setFixedHeight(25f);
   	              tablaFoot.addCell(celdaFoot);
   	          		   	              	              
	              fraseFoot = new Phrase("Vo.Bo",agencyr);    	                	          
	              celdaFoot = new PdfPCell(fraseFoot);
	              celdaFoot.setFixedHeight(25f);
	              celdaFoot.setBorderColorBottom(new Color(255));
	              tablaFoot.addCell(celdaFoot);
	              
	              fraseFoot = new Phrase("Fecha del diagnóstico: " + fCadena,agencyr);    	                	          
	              celdaFoot = new PdfPCell(fraseFoot);
	              celdaFoot.setFixedHeight(25f);
	              tablaFoot.addCell(celdaFoot);
	              
	              fraseFoot = new Phrase("Nombre:",agencyr);    	                	          
	              celdaFoot = new PdfPCell(fraseFoot);
	              celdaFoot.setBorderColorTop(new Color(255));
	              celdaFoot.setFixedHeight(25f);
	              tablaFoot.addCell(celdaFoot);
	              
	              fraseFoot = new Phrase("Hora del diagnóstico: " + hCadena,agencyr);    	                	          
	              celdaFoot = new PdfPCell(fraseFoot);
	              celdaFoot.setFixedHeight(25f);
	              tablaFoot.addCell(celdaFoot);
	              
	              fraseFoot = new Phrase("Firma:",agencyr);    	                	          
	              celdaFoot = new PdfPCell(fraseFoot);
	              celdaFoot.setFixedHeight(25f);
	              tablaFoot.addCell(celdaFoot);
		          
                  parrafoTablaFoot.add(tablaFoot);
                  pdf.add(parrafoTablaFoot);                                    
                  
  		          pdf.close();  
  		            		          
  		          OutputStream os = response.getOutputStream();
  		          baos.writeTo(os);
  		          os.flush();
  		          os.close();
       		            		         
    	      }catch(DocumentException | IOException e){ e.printStackTrace(); }
    	    
              context.responseComplete();
               
       }
              
	   public Object getBean(String nombre){
   		      Object bean = ContextLoader.getCurrentWebApplicationContext().getBean(nombre);
   		      return bean;
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
	   
	   public DataTable getTablaValores() {
		      return tablaValores;
	   }

	   public void setTablaValores(DataTable tablaValores) {
		      this.tablaValores = tablaValores;
	   }
	   
	   public List<Aplicacion> getValores() {
			  return valores;
	   }		
	   	   
	   public boolean isbGuardarDisabled() {
		      return bGuardarDisabled;
	   }

	   public void setbGuardarDisabled(boolean bGuardarDisabled) {
		      this.bGuardarDisabled = bGuardarDisabled;
	   }
	   
	   @Override
	   public boolean equals(Object obj) {
		      // TODO Auto-generated method stub
		      return super.equals(obj);
	   }
	   
	   @Override
	   public int hashCode() {
		      // TODO Auto-generated method stub
		      return super.hashCode();
	   }	  

	   public void setLoginBean(CLogin loginBean) {
		      this.loginBean = loginBean;
	   }
	  	
}

