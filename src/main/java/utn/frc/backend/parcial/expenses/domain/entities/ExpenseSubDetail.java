package utn.frc.backend.parcial.expenses.domain.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "expense_submission_details")
public class ExpenseSubDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "sid")
    private Integer sid;

    @Column(name = "expid")
    private Integer employeeId;

    @Column(name = "amount")
    private Double amount;

    public ExpenseSubDetail() {
    }

    public ExpenseSubDetail(Integer id, Integer sid, Integer employeeId, Double amount) {
        this.id = id;
        this.sid = sid;
        this.employeeId = employeeId;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employee_id) {
        this.employeeId = employee_id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
