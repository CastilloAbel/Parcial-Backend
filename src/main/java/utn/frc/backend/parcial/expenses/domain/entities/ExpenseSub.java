package utn.frc.backend.parcial.expenses.domain.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "expense_submission")
public class ExpenseSub {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sid")
    private Integer id;

    @Column(name = "empid")
    private Integer employee_id;

    @Column(name = "sdate")
    private Date date;

    public ExpenseSub() {
    }

    public ExpenseSub(Integer id, Integer employee_id, Date date) {
        this.id = id;
        this.employee_id = employee_id;
        this.date = date;
    }

// aca van los getter y setter, no me anda el alt insert
}
