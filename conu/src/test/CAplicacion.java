package test;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class CAplicacion implements Serializable{
	
       private static final long serialVersionUID = 1L;
	   
	   private String aplicacion;

	   public String getAplicacion() {
		      return aplicacion;
	   } 

	   public void setAplicacion(String aplicacion) {
		      this.aplicacion = aplicacion;
	   }
	   
	   public void procesarAplicacion(){
		      
		      FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Apliacion " + aplicacion ,""));
		   
	   }

}
