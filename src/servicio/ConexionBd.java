/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servicio;

/**
 *
 * @author Felipe
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
public class ConexionBd {
    private static Connection conn = null;
    public void ConexionBd() {
    }
    public Connection newConnection(){
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe","PARSFW","oracle");
            conn.setAutoCommit(false);
            if(conn != null){
               System.out.println("Conexion Exitosa");
               //JOptionPane.showMessageDialog(null,"Conexion Servidor Central Exitosa" );
            }else{
                //System.out.println("La conexion es erronea");
            }
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null,"Conexion Erronea " + e.getMessage());
        }
        
        return conn;
    }
    public ResultSet ejecutarConsultaSelect(String consulta){
        try
        {
            Statement stm = conn.createStatement();
            return stm.executeQuery(consulta);
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            return null;
        }    
    }
    public int ejecutarConsultaDML(String consulta){
        try
        {
            int n;

            Statement stm = conn.createStatement();
            n=stm.executeUpdate(consulta);      
            stm.execute("commit");
            JOptionPane.showMessageDialog(null, "Copia Exitosa");
            return n;
        }
        catch(SQLException ex)
        {
            JOptionPane.showMessageDialog(null, "Algo fallÃ³... Intente de nuevo"+ex.getMessage());
            return 0;
        }
    }
}
