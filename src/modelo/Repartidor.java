package modelo;

public class Repartidor {

    private int id;
    private String nombre;

    // Para repartidores que ya vienen desde MySQL
    public Repartidor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Para crear un repartidor nuevo
    public Repartidor(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}