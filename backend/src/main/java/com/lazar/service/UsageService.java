package com.lazar.service;

import com.lazar.config.LazarProperties;
import com.lazar.dto.ChatDtos.UsageResponse;
import com.lazar.exception.ApiException;
import com.lazar.model.*;
import com.lazar.repository.DailyUsageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;

@Service
public class UsageService {
    private final DailyUsageRepository usage;
    private final LazarProperties properties;
    public UsageService(DailyUsageRepository usage, LazarProperties properties) {
        this.usage = usage; this.properties = properties;
    }

    @Transactional
    public void consume(AppUser user) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        DailyUsage record = usage.findByUser_IdAndUsageDate(user.getId(), today)
            .orElseGet(() -> usage.save(new DailyUsage(user, today)));
        if (record.getRequestCount() >= properties.usage().freeDailyLimit())
            throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "Daily free usage limit reached");
        record.increment();
    }

    @Transactional(readOnly = true)
    public UsageResponse status(AppUser user) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        int used = usage.findByUser_IdAndUsageDate(user.getId(), today)
            .map(DailyUsage::getRequestCount).orElse(0);
        int limit = properties.usage().freeDailyLimit();
        return new UsageResponse(used, limit, Math.max(0, limit - used), today.plusDays(1).atStartOfDay(ZoneOffset.UTC).toString());
    }
}
