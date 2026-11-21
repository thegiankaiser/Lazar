package com.lazar.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "daily_usage", uniqueConstraints = @UniqueConstraint(name = "uk_usage_user_day", columnNames = {"user_id", "usage_date"}))
public class DailyUsage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;
    @Column(name = "usage_date", nullable = false)
    private LocalDate usageDate;
    @Column(nullable = false)
    private int requestCount;

    protected DailyUsage() {}
    public DailyUsage(AppUser user, LocalDate usageDate) {
        this.user = user; this.usageDate = usageDate; this.requestCount = 0;
    }
    public void increment() { requestCount++; }
    public int getRequestCount() { return requestCount; }
    public LocalDate getUsageDate() { return usageDate; }
}
