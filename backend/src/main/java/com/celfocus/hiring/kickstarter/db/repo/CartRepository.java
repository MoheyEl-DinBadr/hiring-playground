package com.celfocus.hiring.kickstarter.db.repo;

import com.celfocus.hiring.kickstarter.db.entity.CartEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CartEntity c WHERE c.userId = :username")
    Optional<CartEntity> findByUserIdForUpdate(String username);

    Optional<CartEntity> findByUserId(String username);

    @Modifying
    void deleteByUserId(String username);
}
