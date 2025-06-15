package org.example.newjournal.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
@Document
@Data
@NoArgsConstructor
public class JournalEntry {

    @Id
    private String id;
    @NonNull
    private String title;
    private String content;
    private LocalDateTime date;
    

}