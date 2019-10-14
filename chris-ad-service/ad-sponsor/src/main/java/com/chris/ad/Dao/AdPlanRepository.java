package com.chris.ad.Dao;

import com.chris.ad.entity.AdPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdPlanRepository extends JpaRepository<AdPlan, Long> {

    AdPlan findByIdAndAndUserId(Long id, Long userId);

    List<AdPlan> findAllByIdInAnAndUserId(List<Long> ids, Long userId);

    AdPlan findByUserIdAndPlanName(Long id, String name);

    List<AdPlan> findAllByPlanStatus(Integer status);
}
