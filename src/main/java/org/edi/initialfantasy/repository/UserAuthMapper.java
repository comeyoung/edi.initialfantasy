package org.edi.initialfantasy.repository;

import org.edi.initialfantasy.bo.userauthrization.UserAuth;

/**
 * Created by asus on 2018/5/30.
 */
public interface UserAuthMapper {
    void saveLoginRecord(UserAuth userAuth);
}