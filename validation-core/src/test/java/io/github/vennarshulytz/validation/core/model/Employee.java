package io.github.vennarshulytz.validation.core.model;

import java.util.List;

/**
 * 员工
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public class Employee {
    private String id;
    private String name;
    private String phone;
    private String number;
    private String address;
    private Address address1;
    private List<Address> addressList1;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Address getAddress1() { return address1; }
    public void setAddress1(Address address1) { this.address1 = address1; }
    public List<Address> getAddressList1() { return addressList1; }
    public void setAddressList1(List<Address> addressList1) { this.addressList1 = addressList1; }
}