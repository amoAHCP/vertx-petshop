package org.jacpfx.petstore.model;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by amo on 16.01.14.
 */
public class Customer implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================

    private Long id;
    @Size(min = 2, max = 50)
    private String firstname;
    @Size(min = 2, max = 50)
    private String lastname;
    private String telephone;
    private String email;
    @Valid
    private Address homeAddress = new Address();

    private Integer age;


    // ======================================
    // =            Constructors            =
    // ======================================

    public Customer() {
    }

    public Customer(String firstname, String lastname, String email, Address address) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.homeAddress = address;
    }


    // ======================================
    // =         Getters & setters          =
    // ======================================

    public Long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public Address getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(Address homeAddress) {
        this.homeAddress = homeAddress;
    }

    public Integer getAge() {
        return age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;

        Customer customer = (Customer) o;

        if (age != null ? !age.equals(customer.age) : customer.age != null) return false;
        if (email != null ? !email.equals(customer.email) : customer.email != null) return false;
        if (firstname != null ? !firstname.equals(customer.firstname) : customer.firstname != null) return false;
        if (homeAddress != null ? !homeAddress.equals(customer.homeAddress) : customer.homeAddress != null)
            return false;
        if (id != null ? !id.equals(customer.id) : customer.id != null) return false;
        if (lastname != null ? !lastname.equals(customer.lastname) : customer.lastname != null) return false;
        if (telephone != null ? !telephone.equals(customer.telephone) : customer.telephone != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (firstname != null ? firstname.hashCode() : 0);
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (telephone != null ? telephone.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (homeAddress != null ? homeAddress.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Customer");
        sb.append("{id=").append(id);
        sb.append(", firstname='").append(firstname).append('\'');
        sb.append(", lastname='").append(lastname).append('\'');
        sb.append(", telephone='").append(telephone).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", homeAddress=").append(homeAddress);
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }
}