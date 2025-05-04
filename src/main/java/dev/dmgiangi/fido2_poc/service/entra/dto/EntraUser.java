package dev.dmgiangi.fido2_poc.service.entra.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EntraUser {
    private String givenName;
    private String surname;
    private String userPrincipalName;
    private String mail;
    private String id;
    private String employeeId;
    private LocalDate employeeHireDate;

    @JsonSetter
    public void setEmployeeHireDate(String employeeHireDate) {
        if (employeeHireDate == null)
            return;

        final var cleanedDate = employeeHireDate.replaceAll("T.*", "");
        this.employeeHireDate = LocalDate.parse(cleanedDate);
    }
}
