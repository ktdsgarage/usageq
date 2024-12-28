// VoiceUsage.java
package com.ktds.krater.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoiceUsage {
    @Column(name = "voice_total_usage")
    private long totalUsage;

    @Column(name = "voice_free_usage")
    private long freeUsage;

    @Column(name = "voice_excess_usage")
    private long excessUsage;

    public void addUsage(long amount) {
        this.totalUsage += amount;
        calculateExcessUsage();
    }

    private void calculateExcessUsage() {
        this.excessUsage = Math.max(0, totalUsage - freeUsage);
    }
}