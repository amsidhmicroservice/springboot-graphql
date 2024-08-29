package com.amsidh.mvc.graphql.resolver.mutation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class SaveEmployee implements Serializable {
    private String name;
    private String emailId;
}
