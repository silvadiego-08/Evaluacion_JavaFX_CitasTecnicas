package ni.edu.uam.citas_tecnicas.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Cliente {

    private Integer id;
    private String nombres;
    private String apellidos;
    private String telefono;
    private String correo;
    private boolean activo;

    @Override
    public String toString() {
        return nombres + " " + apellidos;
    }
}
