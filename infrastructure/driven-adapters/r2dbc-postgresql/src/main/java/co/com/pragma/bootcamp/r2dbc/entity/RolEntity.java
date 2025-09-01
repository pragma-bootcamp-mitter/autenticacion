package co.com.pragma.bootcamp.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//corregir nombre de tabla
@Table("roles")
public class RolEntity {

    @Id
    @Column("role_id")
    private Integer id;

    @Column("name")
    private String name;

    @Column("description")
    private String description;
}
