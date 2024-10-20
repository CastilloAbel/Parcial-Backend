package utn.frc.backend.parcial.expenses;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class Expenses {
    private static final int EMPLOYEE_ID_INDEX = 0, EMPLOYEE_NAME_INDEX = 1;
    private static final int DEPARTMENT_ID_INDEX = 2, DEPARTMENT_NAME_INDEX = 3;
    private static final int EXPENSE_SUBMISSION_ID_INDEX = 4, EXPENSE_SUBMISSION_DATE_INDEX = 5;
    private static final int EXPENSE_ID_INDEX = 7, EXPENSE_NAME_INDEX = 8;
    private static final int DET_ID_INDEX = 6, DET_AMOUNT_INDEX = 9;
    private static final String PERSISTENCE_UNIT_NAME = "h2ExpPU";
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static double total = 0.0;


    record CsvRow(
            Integer empId, String empName, Integer dptId, String dptName,
            Integer subId, Date subDate, Integer expId, String expName,
            Integer detId, Double detAmmount
    ) {}

    private static List<CsvRow> rowList;

    public record Expen(Integer id, String name){}
    public record Department(
            Integer id, String name
    ){}
    public record Employee(Integer id, String name, Department department){}
    public record ExpenSub(Integer id, Employee employee, Date subDate){}
    public record ExpenSubDetail(Integer id, Expen expen, ExpenSub expenSub, Double amount){}

    private static Map<Integer, Expen> expMapper = new HashMap<>();
    private static Map<Integer, Department> departmentMap = new HashMap<>();
    private static Map<Integer, Employee> employeeMap = new HashMap<>();
    private static Map<Integer, ExpenSub> expenSubMap = new HashMap<>();
    private static Map<Integer, ExpenSubDetail> expenSubDetailMap = new HashMap<>();


    private static Function<String[], CsvRow> csvRowMapper = (arr) -> {
        Date date = null;
        try {
            DateFormat format = new SimpleDateFormat(DATE_FORMAT);
            date = format.parse(arr[EXPENSE_SUBMISSION_DATE_INDEX]);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + " DATE: " + arr[EXPENSE_SUBMISSION_DATE_INDEX]);
        }
        return new CsvRow(
                Integer.parseInt(arr[EMPLOYEE_ID_INDEX]),
                arr[EMPLOYEE_NAME_INDEX],
                Integer.parseInt(arr[DEPARTMENT_ID_INDEX]),
                arr[DEPARTMENT_NAME_INDEX],
                Integer.parseInt(arr[EXPENSE_SUBMISSION_ID_INDEX]),
                date,
                Integer.parseInt(arr[EXPENSE_ID_INDEX]),
                arr[EXPENSE_NAME_INDEX],
                Integer.parseInt(arr[DET_ID_INDEX]),
                Double.valueOf(arr[DET_AMOUNT_INDEX])
        );
    };


    public static void main(String[] args) throws Exception {

        String fPath = System.getProperty("user.dir") + "/src/main/java/utn/frc/backend/parcial/expenses/Expenses.csv";
        loadCollecions(fPath);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = emf.createEntityManager();

        //pruebas
//        departmentMap.values().forEach(el -> System.out.println(el));

//        em.getTransaction().begin();
//        populate(em);
//        em.getTransaction().commit();

        //detalle de viaticos para un empleado determinado
        // no me acuerdo lo del scanner sc = new Scan()
        // en el 3 va el valor ingresado por teclado

        employeeExpenses(3);

        //detalle de viaticos por departamento
        departmentExpenses(6);


//        expenseSummary(null, null, "1900-01-01", "2100-12-31");

        em.close();
        emf.close();
    }

    private static void populate(EntityManager em) {

        departmentMap.values().forEach(el ->
                em.createNativeQuery("insert into department(did, dname) values(?, ?)")
                .setParameter(1, el.id)
                .setParameter(2, el.name)
                .executeUpdate());

        expMapper.values().forEach(el ->
                em.createNativeQuery("insert into expense(expid, expname) values(?, ?)")
                        .setParameter(1, el.id)
                        .setParameter(2, el.name)
                        .executeUpdate());

    employeeMap.values().forEach(el ->
            em.createNativeQuery("insert into employee(empid, empname, did) values(?, ?, ?)")
                    .setParameter(1, el.id)
                    .setParameter(2, el.name)
                    .setParameter(3, el.department.id)
                    .executeUpdate());
    expenSubMap.values().forEach(el ->
            em.createNativeQuery("insert into expense_submission(sid, empid, sdate) values(?, ?, ?)")
                    .setParameter(1, el.id)
                    .setParameter(2, el.employee.id)
                    .setParameter(3,el.subDate)
                    .executeUpdate());

    expenSubDetailMap.values().forEach(el ->
            em.createNativeQuery("insert into expense_submission_details(id, sid, expid, amount) values(?, ?, ?, ?)")
                    .setParameter(1, el.id)
                    .setParameter(2, el.expenSub.id)
                    .setParameter(3, el.expen.id)
                    .setParameter(4, el.amount)
                    .executeUpdate());


    }


    private static void loadCollecions(String fPath) {
        loadExpensesList(fPath);
        rowList.forEach(el -> updateCollections(el));
    }

    private static void updateCollections(CsvRow el) {

        Department dep = departmentMap.get(el.dptId);
        if (dep == null) {
            dep = new Department(el.dptId, el.dptName);
            departmentMap.put(el.dptId, dep);
        }

        Employee emp = employeeMap.get(el.empId);
        if (emp == null) {
            emp = new Employee(el.empId, el.empName, dep);
            employeeMap.put(el.empId, emp);
        }

        Expen exp = expMapper.get(el.expId);
        if(exp == null) {
            exp = new Expen(el.expId, el.empName);
            expMapper.put(el.expId, exp);
        }

        ExpenSub expenSub = expenSubMap.get(el.subId);
        if (expenSub == null) {
            expenSub = new ExpenSub(el.subId, emp, el.subDate);
            expenSubMap.put(el.subId, expenSub);
        }

        ExpenSubDetail expenSubDetail = expenSubDetailMap.get(el.detId);
        if (expenSubDetail == null) {
            expenSubDetail = new ExpenSubDetail(el.detId, exp, expenSub, el.detAmmount);
            expenSubDetailMap.put(el.detId, expenSubDetail);
        }

    }

    private static void loadExpensesList(String fPath) {
        String delim = ",";

        rowList = getLineFromFile(fPath)
                .stream()
                .skip(1)
                .map(e -> Arrays.stream(e.split(delim))
                        .map(el -> el.trim())
                        .toArray(String[]::new))
                .map(arr -> csvRowMapper.apply(arr))
                .toList();
    }

    private static List<String> getLineFromFile(String fPath) {
        try(Stream<String> s = Files.lines(Paths.get(fPath))){
            return s.toList();
        } catch (Exception e){
            return new ArrayList<>();
        }
    }

    private static void employeeExpenses(Integer empId) {

        Employee emp = employeeMap.get(empId);
        System.out.printf("Employee: %3d, %s\n", emp.id, emp.name);
        List<ExpenSub> exp = new ArrayList<>();

        expenSubMap.values().forEach(el -> {
            if (el.employee.id == emp.id){
                exp.add(el);
            }
        });

        expenSubDetailMap.values().forEach(el -> {
            double subtotal = 0;
            for (ExpenSub e : exp) {

                if (el.expenSub.id == e.id){
                    System.out.printf("\t%d, %s\n", e.id, e.subDate);
                    System.out.printf("\t\t%3d, %32s: %8.2f\n",
                            el.id,
                            el.expen.name,
                            el.amount
                    );
                    subtotal += el.amount;
                }
            }
            if (subtotal > 0) {
                System.out.println("\t\t===============================================");
                System.out.printf("\t\tTOTAL: %40.2f\n\n", subtotal);
            }
            total += subtotal;
        });
        System.out.println("\t\t===============================================");
        System.out.printf("TOTAL: %48.2f\n", total);

    }

    private static void departmentExpenses(Integer dptId) {
        Department dep = departmentMap.get(dptId);

        System.out.printf("Department: %3d, %s\n", dep.id, dep.name);
        List<Employee> emp = new ArrayList<>();

        employeeMap.values().forEach(el -> {
            if (el.department.id == dep.id){
                emp.add(el);
            }
        });

        double total = 0.0;
        for(ExpenSub expenSub : expenSubMap.values()){
            for (Employee e : emp) {
                if (expenSub.employee.id == e.id) {
                    double subtotal = 0.0;

                    for(ExpenSubDetail detail: expenSubDetailMap.values()){
                        if (detail.expenSub.id == expenSub.id){
                            subtotal += detail.amount;
                            }

                    }
                    System.out.println("\t\t===============================================");
                    System.out.printf("\t\t%3d, %32s: %8.2f\n", e.id, e.name, subtotal);
                    total += subtotal;
                }
            }
        }
        System.out.println("\t\t===============================================");
        System.out.printf("TOTAL: %48.2f\n", total);

    }

    private static void expenseSummary(EntityManager em, Integer expId, String fDesde, String fHasta) {
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        try {
            expenseSummary(em, expId, format.parse(fDesde), format.parse(fHasta));
        } catch (ParseException pe) {
            throw new RuntimeException(pe.getMessage());
        }
    }

    private static void expenseSummary(EntityManager em, Integer expId, Date fDesde, Date fHasta) {
        System.out.printf(
                "%d, %s, desde %s hasta %s\n",
                999, "Expense Name",
                "yyyy-mm-dd", "yyyy-mm-dd"
        );
        System.out.printf("\t%3d, %s\n", 999, "Employee's Name");
        System.out.printf("\t\t%s: %35.2f\n", "yyyy-mm-dd", 999.99d);
        System.out.printf("\t\t%s: %35.2f\n", "yyyy-mm-dd", 999.99d);
        System.out.println("\t\t===============================================");
        System.out.printf("\t\tTotal: %40.2f\n\n", 9999.99d); // Employee Total
        System.out.printf("\t%3d, %s\n", 999, "Employee's Name");
        System.out.printf("\t\t%s: %35.2f\n", "yyyy-mm-dd", 999.99d);
        System.out.printf("\t\t%s: %35.2f\n", "yyyy-mm-dd", 999.99d);
        System.out.println("\t\t===============================================");
        System.out.printf("\t\tTotal: %40.2f\n\n", 9999.99d); // Employee Total
        System.out.printf("TOTAL: %48.2f\n", 99999.99d);

    }

}
