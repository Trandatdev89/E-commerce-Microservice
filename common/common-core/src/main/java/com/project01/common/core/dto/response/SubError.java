package com.project01.common.core.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubError implements Serializable {
    private static final long serialVersionUID = 1L;

    private String field;
    private Object rejectedValue;
    private String message;
}
