package utn.frc.backend.parcial.expenses.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "expense")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expid")
    private Integer id;

    @Column(name = "expname")
    private String name;

    public Expense() {
    }

    public Expense(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

// aca van los getter y setter, no me anda el alt insert
}
