package io.github.vennarshulytz.validation.core;

import io.github.vennarshulytz.validation.core.model.Address;
import io.github.vennarshulytz.validation.core.model.Department;
import io.github.vennarshulytz.validation.core.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 字段访问器测试
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
class FieldAccessorTest {

    @Test
    @DisplayName("测试获取字段值")
    void testGetFieldValue() {
        Employee employee = new Employee();
        employee.setId("emp001");
        employee.setName("张三");

        Object id = FieldAccessor.getFieldValue(employee, "id");
        Object name = FieldAccessor.getFieldValue(employee, "name");

        assertEquals("emp001", id);
        assertEquals("张三", name);
    }

    @Test
    @DisplayName("测试获取null对象的字段值")
    void testGetFieldValue_WhenObjectIsNull() {
        Object result = FieldAccessor.getFieldValue(null, "id");
        assertNull(result);
    }

    @Test
    @DisplayName("测试按类型查找实例 - 无路径限制")
    void testFindInstancesByType_WithoutPath() {
        Department department = createTestDepartment();

        List<FieldAccessor.TypedInstance> instances = FieldAccessor.findInstancesByType(
                department, Employee.class, "", Collections.emptySet());

        // 应该找到 manager1 和 managerList1 中的所有Employee
        assertEquals(3, instances.size());
    }

    @Test
    @DisplayName("测试按类型查找实例 - 指定路径")
    void testFindInstancesByType_WithSpecificPath() {
        Department department = createTestDepartment();

        List<FieldAccessor.TypedInstance> instances = FieldAccessor.findInstancesByType(
                department, Employee.class, "managerList1", Collections.emptySet());

        // 只应该找到 managerList1 中的Employee
        assertEquals(2, instances.size());
        for (FieldAccessor.TypedInstance instance : instances) {
            assertTrue(instance.getPath().contains("managerList1"));
        }
    }

    @Test
    @DisplayName("测试按类型查找实例 - 排除指定路径")
    void testFindInstancesByType_WithExcludedPaths() {
        Department department = createTestDepartment();

        Set<String> excludedPaths = new HashSet<>();
        excludedPaths.add("managerList1");

        List<FieldAccessor.TypedInstance> instances = FieldAccessor.findInstancesByType(
                department, Employee.class, "", excludedPaths);

        // 应该只找到 manager1，排除 managerList1 中的Employee
        assertEquals(1, instances.size());
        assertEquals("manager1", instances.get(0).getPath());
    }

    @Test
    @DisplayName("测试嵌套路径查找")
    void testFindInstancesByType_NestedPath() {
        Department department = createTestDepartment();

        List<FieldAccessor.TypedInstance> instances = FieldAccessor.findInstancesByType(
                department, Address.class, "managerList1.address1", Collections.emptySet());

        // 应该找到 managerList1 中所有 Employee 的 address1
        assertEquals(2, instances.size());
    }

    private Department createTestDepartment() {
        Department department = new Department();
        department.setName("技术部");

        // manager1
        Employee manager1 = new Employee();
        manager1.setId("m001");
        manager1.setName("经理1");
        manager1.setPhone("13800000001");
        manager1.setNumber("001");
        department.setManager1(manager1);

        // managerList1
        List<Employee> managerList1 = new ArrayList<>();

        Employee emp1 = new Employee();
        emp1.setId("e001");
        emp1.setName("员工1");
        emp1.setPhone("13800000002");
        emp1.setAddress("地址1");
        Address addr1 = new Address();
        addr1.setId("a001");
        addr1.setProvince("北京");
        emp1.setAddress1(addr1);
        managerList1.add(emp1);

        Employee emp2 = new Employee();
        emp2.setId("e002");
        emp2.setName("员工2");
        emp2.setPhone("13800000003");
        emp2.setAddress("地址2");
        Address addr2 = new Address();
        addr2.setId("a002");
        addr2.setProvince("上海");
        emp2.setAddress1(addr2);
        managerList1.add(emp2);

        department.setManagerList1(managerList1);

        return department;
    }
}