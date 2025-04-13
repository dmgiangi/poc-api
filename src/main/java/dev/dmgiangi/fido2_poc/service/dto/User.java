package dev.dmgiangi.fido2_poc.service.dto;

import com.fasterxml.jackson.annotation.JsonSetter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class User {
    String givenName;
    String surname;
    String userPrincipalName;
    String mail;
    String id;
    String employeeId;
    LocalDate employeeHireDate;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getEmployeeHireDate() {
        return employeeHireDate;
    }

    @JsonSetter
    public void setEmployeeHireDate(String employeeHireDate) {
        if (employeeHireDate == null)
            return;

        final var cleanedDate = employeeHireDate.replaceAll("T.*", "");
        this.employeeHireDate = LocalDate.parse(cleanedDate);
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
