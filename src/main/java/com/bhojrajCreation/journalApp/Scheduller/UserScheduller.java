package com.bhojrajCreation.journalApp.Scheduller;

import com.bhojrajCreation.journalApp.Entity.JournalEntry;
import com.bhojrajCreation.journalApp.Entity.User;
import com.bhojrajCreation.journalApp.Enum.Sentiment;
import com.bhojrajCreation.journalApp.Repository.UserRepositoryImp;
import com.bhojrajCreation.journalApp.Services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduller {
    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImp userRepositoryImp;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void fetchUserSAMail() {
        List<User> userForSA = userRepositoryImp.getUserForSA();

        for (User user : userForSA) {
            List<JournalEntry> journalEntries = user.getJournalEntries();

            if (journalEntries == null || journalEntries.isEmpty()) { //  Avoid NullPointerException
                continue;
            }

            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(x -> x != null && x.getDate() != null) //  Null check
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                    .map(JournalEntry::getSentiment)
                    .filter(sentiment -> sentiment != null) //  Avoid null sentiment values
                    .collect(Collectors.toList());

            if (sentiments.isEmpty()) { // If no valid sentiments, skip user
                continue;
            }

            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }

            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;

            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }

            if (mostFrequentSentiment != null) {
                emailService.sendMail(
                        user.getEmail(),
                        "Sentiment for last 7 days",
                        "Your most frequent sentiment in the past week: " + mostFrequentSentiment.toString()
                );
            }
        }
    }
}
