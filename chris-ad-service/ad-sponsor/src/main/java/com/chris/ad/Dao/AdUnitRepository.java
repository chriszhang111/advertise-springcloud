package com.chris.ad.Dao;

import com.chris.ad.entity.AdUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdUnitRepository extends JpaRepository<AdUnit, Long> {

    AdUnit findByPlanIdAndUnitName(Long id, String name);

    List<AdUnit> findAllByUnitStatus(Integer status);

}
