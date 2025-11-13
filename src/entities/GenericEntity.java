
package entities;

/**
 *
 * @author Andres Meshler
 */
public abstract class GenericEntity {
    private Long id; // Clave primaria de la entidad
    private boolean eliminado; // Baja lógica
    
    // Constructor completo
    GenericEntity (Long id, boolean eliminado){
        this.id = id;
        this.eliminado = eliminado;
    }
    
    // Constructor por defecto
    GenericEntity (){
        this.eliminado = false;
    }
    
    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    // Verificar si la entidad está eliminada
    public boolean isEliminado() {
        return eliminado;
    }
    // Eliminado true para eliminar, eliminado false para reactivar
    public void setEliminado(boolean eliminado) {
        this.eliminado = eliminado;
    }  
}
