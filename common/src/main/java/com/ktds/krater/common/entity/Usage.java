package com.ktds.krater.common.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "usages")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Embedded
    private VoiceUsage voiceUsage;

    @Embedded
    private VideoUsage videoUsage;

    @Embedded
    private MessageUsage messageUsage;

    @Embedded
    private DataUsage dataUsage;

    public void updateUsage(String type, long amount) {
        switch (type) {
            case "VOICE" -> voiceUsage.addUsage(amount);
            case "VIDEO" -> videoUsage.addUsage(amount);
            case "MESSAGE" -> messageUsage.addUsage(amount);
            case "DATA" -> dataUsage.addUsage(amount);
            default -> throw new IllegalArgumentException("Invalid usage type: " + type);
        }
    }
}
