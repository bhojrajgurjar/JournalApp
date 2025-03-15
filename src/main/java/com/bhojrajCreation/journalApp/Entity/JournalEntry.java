package com.bhojrajCreation.journalApp.Entity;

import com.bhojrajCreation.journalApp.Enum.Sentiment;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "journal_entries")
@Data
//@Getter
//@Setter
//@AllArgsConstructor
//@ToString
//@EqualsAndHashCode
@NoArgsConstructor(force = true)
public class JournalEntry {
    @Id
    private ObjectId id;
    @NonNull
    private String tittle;
    private String content;
    private LocalDateTime date;
    private Sentiment sentiment;


}
