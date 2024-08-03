package models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "portfolio_privacy_info")
@Data
public class PortfolioPrivacyInfoEntity {
    @Id
    private long portfolioID;
    @Column(name = "is_sharable", nullable = false)
    private boolean isSharable;
}
