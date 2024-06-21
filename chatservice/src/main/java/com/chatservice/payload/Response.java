package com.chatservice.payload;

import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequestMapping(produces = "application/json")
public class Response {

    private String message;

    private boolean success;

    private int status;

    @JsonInclude(Include.NON_NULL)
    private Object data;

    @JsonInclude(Include.NON_NULL)
    private String token;

}

