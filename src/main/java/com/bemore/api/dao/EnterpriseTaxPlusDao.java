package com.bemore.api.dao;

import com.bemore.api.entity.EnterpriseTax;
import com.bemore.api.entity.EnterpriseTaxPlus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EnterpriseTaxPlusDao extends JpaRepository<EnterpriseTaxPlus, String>, JpaSpecificationExecutor<EnterpriseTaxPlus> {

    EnterpriseTax findByEnterpriseNameAndDate(String enterpriseName, String date);

    List<EnterpriseTaxPlus> findByEnterpriseNameAndDateBetween(String enterpriseName, String max, String min);

    @Query(nativeQuery = true, value = "select sum(t.total_tax_total) from t_enterprise_tax t where date=:date and enterprise_name in (select enterprise_name from t_support_contract c where c.platform_id=:platformId and is_fund=:isFund and c.status=1)")
    Double sumTaxByPlatformIdAndDate(String platformId, String date,int isFund);

    @Query(nativeQuery = true, value = "select sum(t.added_tax),sum(t.income_tax),sum(t.person_tax) from t_enterprise_tax t where year=:year and date<=:date and enterprise_name = :enterpriseName")
    List<double[]> sumAllTaxByEnterpriseAndDateAndYear(String enterpriseName, String date,int year);


}
