package com.amsidh.mvc.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import lombok.Data;

import java.util.List;

@Data
public class GenericGraphQlException extends RuntimeException implements GraphQLError {
    private String message;
    private String key;
    private boolean dynamicMessage;

    public GenericGraphQlException(String key, String message, boolean dynamicMessage) {
        this.key = key;
        this.message = message;
        this.dynamicMessage = dynamicMessage;
    }

    public GenericGraphQlException(String message) {
        super(message);
        this.message = message;
    }


    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }
}
