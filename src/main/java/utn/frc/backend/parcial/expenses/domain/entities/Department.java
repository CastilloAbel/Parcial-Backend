package utn.frc.backend.parcial.expenses.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "did")
    private Integer id;

    @Column(name = "dname")
    private String name;

    public Department() {
    }

    public Department(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
// aca van los getter y setter, no me anda el alt insert
}
