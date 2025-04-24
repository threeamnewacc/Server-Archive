package me.devkevin.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * Created by DevKevin
 * Project: WebAPI
 * Date: 02/03/2022 @ 18:43
 */
@Data @NoArgsConstructor @AllArgsConstructor @Entity(name = "user")
public class Register implements Serializable {
    @Id
    private String uuid;

    private String password;
    private String password_key;
}
