package repositories;

import jakarta.transaction.Transactional;
import models.entities.IntradayMovementsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IntradayMovementsRepository extends JpaRepository<IntradayMovementsEntity, Long> {

    List<IntradayMovementsEntity> findByPortfolioID(long portfolioID);

    @Modifying
    @Transactional
    @Query("DELETE FROM IntradayMovementsEntity p WHERE p.id = :portfolioID")
    void deleteByPortfolioID(@Param("portfolioID") long portfolioID);
}
