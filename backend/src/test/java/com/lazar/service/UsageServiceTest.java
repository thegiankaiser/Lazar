package com.lazar.service;

import com.lazar.config.LazarProperties;
import com.lazar.model.AppUser;
import com.lazar.model.DailyUsage;
import com.lazar.repository.DailyUsageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import com.lazar.exception.ApiException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsageServiceTest {
    @Test
    void rejectsRequestWhenDailyLimitReached() {
        DailyUsageRepository repo = mock(DailyUsageRepository.class);
        AppUser user = new AppUser("a@example.com", "hash");
        var service = new UsageService(repo, new LazarProperties(
            new LazarProperties.Jwt("VGhpc0lzQVRlc3RTZWNyZXRLZXlXaXRoMzJCeXRlc09yTW9yZQ==", 1000),
            new LazarProperties.Ai("test", "model"), new LazarProperties.Usage(0)));
        when(repo.findByUser_IdAndUsageDate(any(), any())).thenReturn(Optional.empty());
        assertThrows(ApiException.class, () -> service.consume(user));
    }
}
