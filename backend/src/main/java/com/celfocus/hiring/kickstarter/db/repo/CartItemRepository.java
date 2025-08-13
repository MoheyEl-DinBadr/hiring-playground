package com.celfocus.hiring.kickstarter.db.repo;

import com.celfocus.hiring.kickstarter.db.entity.CartItemEntity;
import com.celfocus.hiring.kickstarter.db.entity.CartItemPK;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, CartItemPK> {


    @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
    @Query("SELECT ci FROM CartItemEntity ci WHERE ci.cartId = :cartId AND ci.itemId = :itemId")
    Optional<CartItemEntity> findByIdForUpdate(@Param("cartId") Long cartId, @Param("itemId") String itemId);


    @Modifying
    @Query("UPDATE CartItemEntity ci SET ci.quantity = ci.quantity + :increment WHERE ci.cartId = :cartId AND  ci.itemId = :itemId")
    int updateQuantityById(@Param("cartId") Long cartId, @Param("itemId") String itemId, @Param("increment") Integer increment);


}
