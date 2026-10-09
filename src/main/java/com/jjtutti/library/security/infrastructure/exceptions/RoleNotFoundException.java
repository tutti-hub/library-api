package com.jjtutti.library.security.infrastructure.exceptions;

import com.jjtutti.library.security.dao.RoleName;

public class RoleNotFoundException extends RuntimeException {

    public RoleNotFoundException (RoleName roleName) {
        super("Role " + roleName + " not found");
    }

}
