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
                Vigilante newVigilante;
                resultadoVigilante=conn.ejecutarConsultaSelect("select * from VIGILANTE where usuario = '"+id[1]+"'and contrasenia = '"+id[2]+"'");
                newVigilante = infoVigilante(resultadoVigilante);
                salidaDecorada.println(serializarVigilante(newVigilante));
                break;
            case "informacionConductorCedula":
                System.out.println("Entra consultar conductor cedula");
                Conductor newConductorCedula;
                ResultSet resultadoConductorCedula;
                resultadoConductorCedula=conn.ejecutarConsultaSelect("select FECHA_NACIMIENTO,GENERO,CEDULA,CODIGO,NOMBRE,APELLIDO,ROL from CONDUCTOR where CEDULA ="+id[1]);
                newConductorCedula = infoConductor(resultadoConductorCedula);
                salidaDecorada.println(serializarConductor(newConductorCedula));
                //conn.ejecutarConsultaSelect("select placa from vehiculo where id="+id[1]);
                break;
            case "informacionConductorCodigo":
                System.out.println("Entra consultar conductor codigo");
                Conductor newConductorCodigo;
                ResultSet resultadoConductorCodigo;
                resultadoConductorCodigo=conn.ejecutarConsultaSelect("select FECHA_NACIMIENTO,GENERO,CEDULA,CODIGO,NOMBRE,APELLIDO,ROL from CONDUCTOR where CODIGO ="+id[1]);
                newConductorCodigo = infoConductor(resultadoConductorCodigo);
                salidaDecorada.println(serializarConductor(newConductorCodigo));
                break;
            case "informacionAutomovilConductorCedula":
                System.out.println("Entra consultar vehiculo cedula");
                ArrayList<Automovil> newAutomovilCedula;
                //newAutomovilCedula = new ArrayList();
                ResultSet resultadoAutomovilCedula;
                resultadoAutomovilCedula=conn.ejecutarConsultaSelect("SELECT AUTOMOVIL.PLACA,AUTOMOVIL.MARCA,AUTOMOVIL.TIPO FROM AUTOMOVIL INNER JOIN AUTOMOVIL_CONDUCTOR ON AUTOMOVIL.PLACA = AUTOMOVIL_CONDUCTOR.PLACA WHERE CEDULA="+id[1]);
                newAutomovilCedula = infoAutomovil(resultadoAutomovilCedula);
                salidaDecorada.println(serializarAutomovil(newAutomovilCedula));
                break;
            case "informacionAutomovilConductorCodigo":
                System.out.println("Entra consultar vehiculo codigo");
                ArrayList<Automovil> newAutomovilCodigo;
                //newAutomovilCodigo = new ArrayList();
                ResultSet resultadoAutomovilCodigo;
                resultadoAutomovilCodigo=conn.ejecutarConsultaSelect("SELECT AUTOMOVIL.PLACA,AUTOMOVIL.MARCA,AUTOMOVIL.TIPO FROM AUTOMOVIL INNER JOIN AUTOMOVIL_CONDUCTOR ON AUTOMOVIL.PLACA = AUTOMOVIL_CONDUCTOR.PLACA WHERE CODIGO="+id[1]);
                newAutomovilCodigo = infoAutomovil(resultadoAutomovilCodigo);
                salidaDecorada.println(serializarAutomovil(newAutomovilCodigo));
                break;
            case "ingresarConductor":
                System.out.println("Entra ingresar conductor");
                conn.ejecutarConsultaDML("INSERT INTO CONDUCTOR VALUES ("+id[1]+","+id[2]+",'"+id[3]+"','"+id[4]+"','"+id[5]+"','"+id[6]+"',to_date('"+id[7]+"','YYYY,MM,DD'))");
                break;
            case "ingresarAutomovil":
                System.out.println("Entra ingresar vehiculo");
                conn.ejecutarConsultaDML("INSERT INTO AUTOMOVIL VALUES ('"+id[1]+"','"+id[2]+"','"+id[3]+"')");
                break;
            case "vicularAutomovilConductor":
                System.out.println("Entra vincular vehiculo-conductor");
                conn.ejecutarConsultaDML("INSERT INTO AUTOMOVIL_CONDUCTOR VALUES ('"+id[1]+"',"+id[2]+")");
        }
    }
    private Vigilante infoVigilante(ResultSet resultadoVigilante) throws SQLException{
        Vigilante newVigilante;
        newVigilante = new Vigilante();
        while(resultadoVigilante.next()){
           newVigilante.setId(resultadoVigilante.getString("IDENTIFICACION"));
           newVigilante.setNombre(resultadoVigilante.getString("NOMBRE"));
           newVigilante.setApellido(resultadoVigilante.getString("APELLIDO"));
           newVigilante.setEstado(resultadoVigilante.getString("ESTADO"));
           newVigilante.setRol(resultadoVigilante.getString("ROL"));
        }
        return newVigilante;
    }
    private Conductor infoConductor(ResultSet resultadoConductor) throws SQLException{
        Conductor newConductor;
        newConductor = new Conductor();
         while(resultadoConductor.next()){
                newConductor.setFecha_nacimiento(resultadoConductor.getString("FECHA_NACIMIENTO"));
                newConductor.setGenero(resultadoConductor.getString("GENERO"));
                newConductor.setCedula(resultadoConductor.getString("CEDULA"));
                newConductor.setCodigo(resultadoConductor.getString("CODIGO"));
                newConductor.setNombre(resultadoConductor.getString("NOMBRE"));
                newConductor.setApellido(resultadoConductor.getString("APELLIDO"));
                newConductor.setRol(resultadoConductor.getString("ROL"));
                    
        }
         return newConductor;
    }
    private ArrayList infoAutomovil(ResultSet resultadoAutomovil) throws SQLException{
        ArrayList<Automovil> listadoAutomoviles;
        Automovil newAutomovil;
        listadoAutomoviles = new ArrayList();
        while(resultadoAutomovil.next()){
           newAutomovil = new Automovil();
           newAutomovil.setTipo(resultadoAutomovil.getString("tipo"));
           newAutomovil.setMarca(resultadoAutomovil.getString("marca"));
           newAutomovil.setPlaca(resultadoAutomovil.getString("placa"));
           listadoAutomoviles.add(newAutomovil);
        }
        return listadoAutomoviles;
    }
    
    public String serializarAutomovil(ArrayList<Automovil> listadoAutomovil ){
           JsonArray array = new JsonArray();
           JsonObject gsonObj;
           for(Automovil auto : listadoAutomovil){
               gsonObj = new JsonObject();
               gsonObj.addProperty("tipo", auto.getTipo());
               gsonObj.addProperty("placa", auto.getPlaca());
               gsonObj.addProperty("marca", auto.getMarca());
               array.add(gsonObj);
           }       
        return array.toString();
    }
    public String serializarVigilante(Vigilante vigilante){
        JsonObject gsonObj;
        gsonObj =  new JsonObject();
        gsonObj.addProperty("id", vigilante.getId());
        gsonObj.addProperty("nombre", vigilante.getNombre());
        gsonObj.addProperty("apellido", vigilante.getApellido());
        gsonObj.addProperty("estado", vigilante.getEstado());
        gsonObj.addProperty("rol", vigilante.getRol());
        return gsonObj.toString();
        
    }
    
    public String serializarConductor(Conductor conductor){
        JsonObject gsonObj;
        gsonObj =  new JsonObject();
        gsonObj.addProperty("cedula", conductor.getCedula());
        gsonObj.addProperty("codigo",conductor.getCodigo());
        gsonObj.addProperty("nombre",conductor.getNombre());
        gsonObj.addProperty("apellido",conductor.getApellido());
        gsonObj.addProperty("rol",conductor.getRol());
        gsonObj.addProperty("fecha_nacimiento", conductor.getFecha_nacimiento());
        gsonObj.addProperty("genero", conductor.getGenero());
        return gsonObj.toString();
    }
    
    
    
}
