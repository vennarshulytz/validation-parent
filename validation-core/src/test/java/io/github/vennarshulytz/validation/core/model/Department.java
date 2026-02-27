package io.github.vennarshulytz.validation.core.model;

import java.util.List;

/**
 * 部门
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class Department {
    private String name;
    private Employee manager1;
    private List<Employee> managerList1;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Employee getManager1() { return manager1; }
    public void setManager1(Employee manager1) { this.manager1 = manager1; }
    public List<Employee> getManagerList1() { return managerList1; }
    public void setManagerList1(List<Employee> managerList1) { this.managerList1 = managerList1; }
}
