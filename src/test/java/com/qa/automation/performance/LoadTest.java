package com.qa.automation.performance;

import io.qameta.allure.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Performance Testing")
@Feature("API Load & Stress Tests")
public class LoadTest {

    private static final Logger log = LogManager.getLogger(LoadTest.class);
    private static final String TARGET_URL = "https://reqres.in/api/users?page=1";
    private static final double P95_THRESHOLD_MS = 3000.0;
    private static final double P99_THRESHOLD_MS = 5000.0;
    private static final double ERROR_RATE_THRESHOLD = 0.05;

    @Test(groups = {"performance"})
    @Story("Smoke load test — 10 concurrent users")
    @Description("10 virtual users send requests concurrently; p95 < 3s, error rate < 5%")
    @Severity(SeverityLevel.CRITICAL)
    public void testSmokeLoad() throws Exception {
        PerformanceResult result = runLoad(10, 20, Duration.ofSeconds(30));
        logResults("Smoke Load", result);
        assertPerformanceSla(result);
    }

    @Test(groups = {"performance"})
    @Story("Standard load test — 25 concurrent users")
    @Description("25 virtual users sustained over 60 seconds; validates normal traffic capacity")
    @Severity(SeverityLevel.CRITICAL)
    public void testStandardLoad() throws Exception {
        PerformanceResult result = runLoad(25, 50, Duration.ofSeconds(60));
        logResults("Standard Load", result);
        assertPerformanceSla(result);
    }

    @Test(groups = {"performance"})
    @Story("Spike test — sudden burst of 75 users")
    @Description("Simulates a sudden traffic spike; error rate must stay below 10%")
    @Severity(SeverityLevel.NORMAL)
    public void testSpikeLoad() throws Exception {
        PerformanceResult result = runLoad(75, 75, Duration.ofSeconds(30));
        logResults("Spike Load", result);
        assertThat(result.errorRate())
                .as("Error rate under spike should stay below 10%%")
                .isLessThanOrEqualTo(0.10);
    }

    @Test(groups = {"performance"})
    @Story("Stress test — 50 concurrent users")
    @Description("50 virtual users to find the breaking point; p99 < 5s")
    @Severity(SeverityLevel.NORMAL)
    public void testStressLoad() throws Exception {
        PerformanceResult result = runLoad(50, 100, Duration.ofSeconds(60));
        logResults("Stress Load", result);

        assertThat(result.errorRate())
                .as("Error rate under stress should stay below %.0f%%", ERROR_RATE_THRESHOLD * 100)
                .isLessThanOrEqualTo(ERROR_RATE_THRESHOLD);
        assertThat(result.p99())
                .as("p99 latency should stay below %sms", P99_THRESHOLD_MS)
                .isLessThanOrEqualTo(P99_THRESHOLD_MS);
    }

    private PerformanceResult runLoad(int threads, int totalRequests, Duration timeout) throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        List<Long> latencies = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(TARGET_URL))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalRequests; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                long t0 = System.currentTimeMillis();
                try {
                    HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
                    long latency = System.currentTimeMillis() - t0;
                    latencies.add(latency);
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        successCount.incrementAndGet();
                    } else {
                        errorCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    log.warn("Request failed: {}", e.getMessage());
                }
            }, pool);
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(timeout.toSeconds(), TimeUnit.SECONDS);

        long totalDurationMs = System.currentTimeMillis() - startTime;
        pool.shutdown();

        return PerformanceResult.compute(latencies, successCount.get(), errorCount.get(), totalDurationMs);
    }

    private void assertPerformanceSla(PerformanceResult result) {
        assertThat(result.errorRate())
                .as("Error rate must be below %.0f%%", ERROR_RATE_THRESHOLD * 100)
                .isLessThanOrEqualTo(ERROR_RATE_THRESHOLD);
        assertThat(result.p95())
                .as("p95 latency must be below %sms", P95_THRESHOLD_MS)
                .isLessThanOrEqualTo(P95_THRESHOLD_MS);
    }

    @Attachment(value = "Performance Report", type = "text/plain")
    private String logResults(String testName, PerformanceResult r) {
        String report = String.format("""
                ── %s Performance Results ──
                Total Requests : %d
                Successful     : %d
                Failed         : %d
                Error Rate     : %.2f%%
                Throughput     : %.2f req/s
                Min Latency    : %d ms
                Avg Latency    : %.2f ms
                p95 Latency    : %.2f ms
                p99 Latency    : %.2f ms
                Max Latency    : %d ms
                ────────────────────────────
                """,
                testName,
                r.totalRequests(), r.successCount(), r.errorCount(),
                r.errorRate() * 100, r.throughput(),
                r.min(), r.avg(), r.p95(), r.p99(), r.max());
        log.info("\n{}", report);
        return report;
    }

    record PerformanceResult(
            int totalRequests, int successCount, int errorCount,
            double errorRate, double throughput,
            long min, long max, double avg, double p95, double p99
    ) {
        static PerformanceResult compute(List<Long> latencies, int success, int errors, long durationMs) {
            if (latencies.isEmpty()) {
                return new PerformanceResult(errors, 0, errors, 1.0, 0, 0, 0, 0, 0, 0);
            }
            List<Long> sorted = new ArrayList<>(latencies);
            Collections.sort(sorted);
            int total = success + errors;
            double avg = sorted.stream().mapToLong(Long::longValue).average().orElse(0);
            double p95 = sorted.get((int) Math.ceil(sorted.size() * 0.95) - 1);
            double p99 = sorted.get((int) Math.ceil(sorted.size() * 0.99) - 1);
            double throughput = total / (durationMs / 1000.0);
            return new PerformanceResult(
                    total, success, errors,
                    (double) errors / total, throughput,
                    sorted.get(0), sorted.get(sorted.size() - 1), avg, p95, p99
            );
        }
    }
}
