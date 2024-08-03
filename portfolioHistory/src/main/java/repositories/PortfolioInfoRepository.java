package repositories;

import jakarta.transaction.Transactional;
import models.entities.PortfolioPrivacyInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioInfoRepository extends JpaRepository<PortfolioPrivacyInfoEntity, Long> {
    @Query("SELECT p.portfolioID FROM PortfolioPrivacyInfoEntity p")
    List<Long> findAllPortfolioIds();

    @Modifying
    @Transactional
    @Query("UPDATE PortfolioPrivacyInfoEntity r SET r.is_sharable = :isSharable WHERE r.portfolioID = :portfolioID")
    void updateSharabilityById(@Param("portfolioID") Long portfolioID, @Param("condivisibile") boolean isSharable);

    @Modifying
    @Transactional
    @Query("DELETE FROM PortfolioPrivacyInfoEntity p WHERE p.portfolioID = :portfolioID")
    void deleteByPortfolioID(@Param("portfolioID") long portfolioID);
}
