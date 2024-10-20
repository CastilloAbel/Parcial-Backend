package utn.frc.backend.parcial.expenses.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empid")
    private Integer id;

    @Column(name = "empname")
    private String name;

    @Column(name = "did")
    private Integer did;

    public Employee() {
    }

    public Employee(Integer id, String name, Integer did) {
        this.id = id;
        this.name = name;
        this.did = did;
    }

// aca van los getter y setter, no me anda el alt insert
}
