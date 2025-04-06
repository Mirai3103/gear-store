package com.ecom.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "persistent_logins")
@Data

public class PersistentLogin implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "username", nullable = false, length = 64)
    private String username;
    
    @Id
    @Column(name = "series", length = 64)
    private String series;
    
    @Column(name = "token", nullable = false, length = 64)
    private String token;
    
    @Column(name = "last_used", nullable = false)
    private Date lastUsed;

}