package com.atypon.nosql.security;

import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@ToString
public class DatabaseAuthority implements GrantedAuthority {

    public static final String READ_DOCUMENTS = "READ_DOCUMENTS";

    public static final String ADD_DOCUMENTS = "ADD_DOCUMENTS";

    public static final String UPDATE_DOCUMENTS = "UPDATE_DOCUMENTS";

    public static final String REMOVE_DOCUMENTS = "REMOVE_DOCUMENTS";

    public static final String GET_COLLECTIONS = "READ_COLLECTIONS";

    public static final String CREATE_COLLECTION = "READ_COLLECTIONS";

    public static final String REMOVE_COLLECTION = "REMOVE_COLLECTION";

    public static final String GET_INDEXES = "READ_INDEXES";

    public static final String CREATE_INDEX = "ADD_INDEX";

    public static final String REMOVE_INDEX = "REMOVE_INDEX";

    public static final String GET_DATABASES = "READ_DATABASES";

    public static final String CREATE_DATABASE = "CREATE_DATABASE";

    public static final String REMOVE_DATABASE = "REMOVE_DATABASE";

    private final String authority;

    public DatabaseAuthority(String authority) {
        this.authority = authority;
    }

    public static DatabaseAuthority from(String authority) {
        return new DatabaseAuthority(authority);
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
