package test;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;

// @author daniel

public class ConexionBase implements Serializable {
         
	    private static final long serialVersionUID = 1L;
		private Connection c;        
   
        public Connection getC(String host,String base,String usuario,String pass) {
            
               try{
                   
                   Class.forName("com.mysql.jdbc.Driver");                                     
                   c = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + base,usuario,pass);                                     
                   
               }catch(Exception e){ e.printStackTrace(); }                                          
               
               return c;
               
        }
                           
}
