package com.david.beerclient.dao.model;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BeerDto {

    @NotBlank
    private String beerName;

    @NonNull
    private String beerStyle;

    @NotBlank
    private String upc;

    private Integer quantityOnHand;

    @NotNull
    private BigDecimal price;

    @Null
    private UUID id;

    private Integer version;

    private Date createdDate;

    private Date lastModifiedDate;
}
