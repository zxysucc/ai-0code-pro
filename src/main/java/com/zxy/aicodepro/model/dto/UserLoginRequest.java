package com.zxy.aicodepro.model.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Random;

@Data
public class UserLoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userPassword;
}
