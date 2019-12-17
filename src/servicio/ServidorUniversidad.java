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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import negocio.*;

public class ServidorUniversidad implements Runnable {
    private static ServerSocket ssock;
    private static Socket socket;
    private Scanner entradaDecorada;
    private PrintStream salidaDecorada;
    private static final int PUERTO = 5000;
    private static ConexionBd conn;

    public ServidorUniversidad() {
    }
    
    public void iniciar() {
        crearConexion();
        abrirPuerto();
        while (true) {
            esperarAlCliente();
            lanzarHilo();        
        }
    }
    private static void lanzarHilo() {
        new Thread(new ServidorUniversidad()).start();
    }
    private static void crearConexion(){
         conn = new ConexionBd();
         if(conn.newConnection()==null){
             System.out.print("jaja");
             System.exit(0);
         }       
    }

    @Override
    public void run() {
        System.out.println("entra ");
        try {
            crearFlujos();
            leerFlujos();
            cerrarFlujos();
        } catch (IOException e) {
        System.out.println(e);
        } catch (SQLException ex) {
            Logger.getLogger(ServidorUniversidad.class.getName()).log(Level.SEVERE, null, ex);
        }  

    }
    
    private static void abrirPuerto() {
    try {
        ssock = new ServerSocket(PUERTO);
         System.out.println("Escuchando por el puerto " + PUERTO);
        } catch (IOException ex) {
            Logger.getLogger(ServidorUniversidad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private static void esperarAlCliente() {
    try {
            socket = ssock.accept();
            System.out.println("Cliente conectado");
        } catch (IOException ex) {
            Logger.getLogger(ServidorUniversidad.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void crearFlujos() throws IOException {
        salidaDecorada = new PrintStream(socket.getOutputStream());
        entradaDecorada = new Scanner(socket.getInputStream());
    }


    /**
     * Lee los flujos del socket
     */
    private void leerFlujos() throws SQLException {
        if (entradaDecorada.hasNextLine()) {
     // Extrae el flujo que envÃ­a el cliente
        String peticion = entradaDecorada.nextLine();
        decodificarPeticion(peticion);

        } else {
        salidaDecorada.flush();
        salidaDecorada.println("NO_ENCONTRADO");
        }   
    }
    private void decodificarPeticion(String peticion) throws SQLException {
        StringTokenizer tokens = new StringTokenizer(peticion, ",");
        String parametros[] = new String[100];
        int i = 0;
        while (tokens.hasMoreTokens()) {
            parametros[i++] = tokens.nextToken();
        }
        String accion = parametros[0];
        procesarAccion(accion, parametros);
    }

    private void cerrarFlujos() throws IOException {
        salidaDecorada.close();
        entradaDecorada.close();
        socket.close();
    }

    private void procesarAccion(String accion, String id[]) throws SQLException {
        switch (accion) {
            case "informacionVigilante":
                System.out.println("Entra confirmar vigilante");
                ResultSet resultadoVigilante;
                resultadoVigilante=conn.ejecutarConsultaSelect("select usuario from VIGILANTE where usuario = '"+Integer.parseInt(id[1])+"'and contrasenia = '"+id[2]+"'");
                String us =" ";
                while(resultadoVigilante.next()){
                    us = resultadoVigilante.getString("USUARIO");
                }
                break;
            case "informacionConductorCedula":
                System.out.println("Entra consultar conductor cedula");
                Conductor newConductorCedula;
                ResultSet resultadoConductorCedula;
                resultadoConductorCedula=conn.ejecutarConsultaSelect("select CEDULA,CODIGO,NOMBRE,APELLIDO,ROL,ESTADO from CONDUCTOR where CEDULA ="+id[1]);
                newConductorCedula = infoConductor(resultadoConductorCedula);
                salidaDecorada.println(serializarConductor(newConductorCedula));
                //conn.ejecutarConsultaSelect("select placa from vehiculo where id="+id[1]);
                break;
            case "informacionConductorCodigo":
                System.out.println("Entra consultar conductor codigo");
                Conductor newConductorCodigo;
                ResultSet resultadoConductorCodigo;
                resultadoConductorCodigo=conn.ejecutarConsultaSelect("select CEDULA,CODIGO,NOMBRE,APELLIDO,ROL,ESTADO from CONDUCTOR where CODIGO ="+id[1]);
                newConductorCodigo = infoConductor(resultadoConductorCodigo);
                salidaDecorada.println(serializarConductor(newConductorCodigo));
                //conn.ejecutarConsultaSelect("select placa from vehiculo where id="+id[1]);
                break;
        }
    }
    private Conductor infoConductor(ResultSet resultadoConductor) throws SQLException{
        Conductor newConductor;
        newConductor = new Conductor();
         while(resultadoConductor.next()){
                newConductor.setCedula(resultadoConductor.getString("CEDULA"));
                newConductor.setCodigo(resultadoConductor.getString("CODIGO"));
                newConductor.setNombre(resultadoConductor.getString("NOMBRE"));
                newConductor.setApellido(resultadoConductor.getString("APELLIDO"));
                newConductor.setRol(resultadoConductor.getString("ROL"));
                newConductor.setEstado(resultadoConductor.getString("ESTADO"));
                    
        }
         return newConductor;
    }
    public String serializarConductor(Conductor conductor){
        JsonObject gsonObj;
        gsonObj =  new JsonObject();
        gsonObj.addProperty("cedula", conductor.getCedula());
        gsonObj.addProperty("codigo",conductor.getCodigo());
        gsonObj.addProperty("nombre",conductor.getNombre());
        gsonObj.addProperty("apellido",conductor.getApellido());
        gsonObj.addProperty("rol",conductor.getRol());
        gsonObj.addProperty("estado",conductor.getEstado()); 
        return gsonObj.toString();
    }
}
