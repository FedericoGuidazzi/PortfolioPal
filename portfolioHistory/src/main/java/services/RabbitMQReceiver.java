package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.MovementBin;
import models.UpdatePortfolioInfoBin;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class RabbitMQReceiver {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PortfolioHistoryService portfolioService;

    @RabbitListener(queues = "privacyUpdates")
    public void updatePrivacy(String msg) {
        System.out.println("\n\n[R] Received privacyUpdates < " + msg + " >");

        try {
            UpdatePortfolioInfoBin privacyBin = objectMapper.readValue(msg, UpdatePortfolioInfoBin.class);
            this.portfolioService.updatePrivacySetting(privacyBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @RabbitListener(queues = "intradayMovements")
    public void addIntradayMovement(String msg) {
        System.out.println("\n\n[R] Received intradayMovements < " + msg + " >");

        try {
            MovementBin movementBin = objectMapper.readValue(msg, MovementBin.class);
            this.portfolioService.insertIntradayMovement(movementBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @RabbitListener(queues = "updateOldMovements")
    public void updateOldMovements(String msg) {
        System.out.println("\n\n[R] Received updateOldMovements < " + msg + " >");

        try {
            MovementBin movementBin = objectMapper.readValue(msg, MovementBin.class);
            this.portfolioService.updateOldMovements(movementBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}