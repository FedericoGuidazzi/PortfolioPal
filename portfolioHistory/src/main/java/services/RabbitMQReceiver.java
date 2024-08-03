package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import models.IntradayMovementBin;
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
        System.out.println("\n\n[R] Received < " + msg + " >");

        try {
            UpdatePortfolioInfoBin privacyBin = objectMapper.readValue(msg, UpdatePortfolioInfoBin.class);
            this.portfolioService.updatePrivacySetting(privacyBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @RabbitListener(queues = "intradayMovements")
    public void addIntradayMovement(String msg) {
        System.out.println("\n\n[R] Received < " + msg + " >");

        try {
            IntradayMovementBin intradayMovementBin = objectMapper.readValue(msg, IntradayMovementBin.class);
            this.portfolioService.insertIntradayMovement(intradayMovementBin);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}