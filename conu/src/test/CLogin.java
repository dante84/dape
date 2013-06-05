package test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class CLogin {
	 	
	   private static final long serialVersionUID = 1L;	   	  
	   
	   private String nombre;
	   private String contraseña;	    
	   
	   public String verificaUsuario(){		   		     
		    
		      ConexionBase cb;
		      Connection conexion;
		      Statement statement;
		       
		      try{
		    	   
		    	  cb = new ConexionBase();
		    	  conexion = cb.getC("172.16.50.14","replicasiipo","test","slipknot");		    	   
		    	  statement = conexion.createStatement();
		    	  
		    	  String select = "select * from usuarios where nombre = '" + getNombre() + "' and contraseña = '" + getContraseña() + "'";		    	 
		    	  
		    	  ResultSet rs = statement.executeQuery(select);		    	   		    	  		    	   		    	 
		    	  
		    	  if( !rs.isBeforeFirst() ){
		    		  FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Revisa tu nombre de usuario y/o password alguno es incorrecto"));                             
		    	  }else{
		    		    FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
		    		    FacesContext.getCurrentInstance().addMessage("", new FacesMessage("Bienvenido " + getNombre()));
	                    return "/Contenido/index.xhtml?faces-redirect=true";
		    	  }		    	   		    	 
		    	  
		      }catch(Exception e){ e.printStackTrace(); }
		   
		      return "";
		   
	   }
	   
	   public String logout() {
              FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
              return "./inicio.xhtml?faces-redirect=true";
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
	   		   	  
}
