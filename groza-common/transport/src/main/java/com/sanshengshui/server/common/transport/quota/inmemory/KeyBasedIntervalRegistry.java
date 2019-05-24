package com.sanshengshui.server.common.transport.quota.inmemory;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author james mu
 * @date 19-1-23 下午3:56
 */
@Slf4j
public class KeyBasedIntervalRegistry {

    private final Map<String, IntervalCount> hostCounts = new ConcurrentHashMap<>();
    private final long intervalDurationMs;
    private final long ttlMs;
    private final Set<String> whiteList;
    private final Set<String> blackList;

    public KeyBasedIntervalRegistry(long intervalDurationMs, long ttlMs, String whiteList, String blackList, String name) {
        this.intervalDurationMs = intervalDurationMs;
        this.ttlMs = ttlMs;
        this.whiteList = Sets.newHashSet(StringUtils.split(whiteList, ','));
        this.blackList = Sets.newHashSet(StringUtils.split(blackList, ','));
        validate(name);
    }

    private void validate(String name) {
        if (ttlMs < intervalDurationMs) {
            log.warn("TTL for {} IntervalRegistry [{}] smaller than interval duration [{}]", name, ttlMs, intervalDurationMs);
        }
        log.info("Start {} KeyBasedIntervalRegistry with whitelist {}", name, whiteList);
        log.info("Start {} KeyBasedIntervalRegistry with blacklist {}", name, blackList);
    }

    public long tick(String clientHostId) {
        IntervalCount intervalCount = hostCounts.computeIfAbsent(clientHostId, s -> new IntervalCount(intervalDurationMs));
        long currentCount = intervalCount.resetIfExpiredAndTick();
        if (whiteList.contains(clientHostId)) {
            return 0;
        } else if (blackList.contains(clientHostId)) {
            return Long.MAX_VALUE;
        }
        return currentCount;
    }

    public void clean() {
        hostCounts.entrySet().removeIf(entry -> entry.getValue().silenceDuration() > ttlMs);
    }

    public Map<String, Long> getContent() {
        return hostCounts.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        interval -> interval.getValue().getCount()));
    }
}
