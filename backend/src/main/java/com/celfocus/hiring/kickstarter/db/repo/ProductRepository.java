package com.celfocus.hiring.kickstarter.db.repo;


import com.celfocus.hiring.kickstarter.db.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    List<ProductEntity> findAll();

    Optional<ProductEntity> findBySku(String sku);

}
