package com.jjtutti.library.helpers;

import java.util.Date;

import com.jjtutti.library.auth.dao.RefreshToken;
import com.jjtutti.library.security.dao.User;

public class RefreshTokenTestHelpers {

                public static RefreshToken tokenExpiringAt(String tokenHash, User user, Date expiresAt) {
                        return new RefreshToken(
                                        tokenHash,
                                        user,
                                        new Date(),
                                        expiresAt);
                }

                public static RefreshToken tokenExpiringAt(String tokenHash, Date expiresAt) {
                        return tokenExpiringAt(tokenHash, new User("u1", "pass"), expiresAt);
                }

                public static RefreshToken tokenExpiringAt(Date expiresAt) {
                        return tokenExpiringAt("Q3W2E1", new User("u1", "pass"), expiresAt);
                }

        }