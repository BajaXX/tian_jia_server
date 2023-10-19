package com.bemore.api.dao;

import com.bemore.api.entity.TPerson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;


public interface TPersonDao extends JpaRepository<TPerson, String>, JpaSpecificationExecutor<TPerson> {


    @Query(nativeQuery = true,value="SELECT * FROM t_person ORDER BY id LIMIT :offset,:limit")
    List<TPerson> findAllTPerson(@Param("offset") int offset, @Param("limit") int limit);

    @Query(nativeQuery = true,value="SELECT * FROM t_person WHERE idcard like :keyword or name like :keyword ORDER BY id LIMIT :offset,:limit")
    List<TPerson> searchAllTPerson(@Param("keyword") String keyword, @Param("offset") int offset, @Param("limit") int limit);

}
