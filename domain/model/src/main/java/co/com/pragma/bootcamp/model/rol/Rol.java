package co.com.pragma.bootcamp.model.rol;

import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Rol {
    private String id;
    private String nombre;
    private String descripcion;
}
