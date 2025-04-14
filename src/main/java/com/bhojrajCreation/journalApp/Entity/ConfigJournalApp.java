package com.bhojrajCreation.journalApp.Entity;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "config_journal_app")
@Data
public class ConfigJournalApp {
    @Indexed(unique = true)
    @NonNull
    private String key;
    @NonNull
    private String value;


}