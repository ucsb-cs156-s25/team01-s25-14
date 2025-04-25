package edu.ucsb.cs156.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/*
    Entity for UCSB Organizations
*/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "UCSBOrganizations")
public class UCSBOrganizations {

    @Id

    private String orgCode;

    private String orgTranslationShort;

    private String orgTranslation;

    private boolean inactive;
}