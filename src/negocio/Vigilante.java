/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

/**
 *
 * @author Felipe
 */
public class Vigilante {
    private String id;
    private String nombre;
    private String apellido;
    private String Rol;

    public Vigilante() {
    }

    public Vigilante(String id, String nombre, String apellido, String Rol) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.Rol = Rol;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getRol() {
        return Rol;
    }

    public void setRol(String Rol) {
        this.Rol = Rol;
    }
    
}
