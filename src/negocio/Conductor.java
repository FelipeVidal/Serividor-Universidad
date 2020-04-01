/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

/**
 *
 * @author Felipe Vidal y Aldair Zemanate
 */
public class Conductor extends Persona{
    private String codigo;
    private String rol;
    public Conductor() {
    }
    public Conductor(String id, String nombre, String apellido,String fechaNacimiento,String genero,String codigo,String rol){
        super(id,nombre,apellido,fechaNacimiento,genero);
        this.codigo = codigo;
        this.rol = rol;
    }
    
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
    

}
