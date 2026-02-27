package io.github.vennarshulytz.validation.model;

/**
 * 地址
 * 
 * @author vennarshulytz
 * @since 1.0.0
 */
public class Address {
    private String id;
    private String province;
    private String city;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
