package org.edi.initialfantasy.repository;

import org.edi.initialfantasy.bo.company.Company;

/**
 * Created by asus on 2018/5/31.
 */
public interface CompanyMapper {
   Company serchCompanyId(String companyName);
}
