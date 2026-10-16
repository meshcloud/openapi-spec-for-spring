package io.meshcloud.openapispecforspring.common.schema.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class SchemaBuildMemoTest {

    private final AtomicInteger computations = new AtomicInteger();

    @Test
    void computesAKeyOnceWithinABuild() {
        SchemaBuildMemo.during(() -> {
            assertThat(lookUp("key")).isEqualTo(1);
            assertThat(lookUp("key")).isEqualTo(1);
            return null;
        });
        assertThat(computations).hasValue(1);
    }

    @Test
    void computesEveryTimeOutsideABuild() {
        lookUp("key");
        lookUp("key");
        assertThat(computations).hasValue(2);
    }

    @Test
    void computesAgainInTheNextBuild() {
        SchemaBuildMemo.during(() -> lookUp("key"));
        SchemaBuildMemo.during(() -> lookUp("key"));
        assertThat(computations).hasValue(2);
    }

    @Test
    void concurrentBuildsComputeTheSameKeyEachForThemselves() throws Exception {
        CountDownLatch bothBuildsStarted = new CountDownLatch(2);
        Runnable build = () -> SchemaBuildMemo.during(() -> {
            bothBuildsStarted.countDown();
            awaitQuietly(bothBuildsStarted);
            return lookUp("key");
        });
        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            CompletableFuture.allOf(
                            CompletableFuture.runAsync(build, executor), CompletableFuture.runAsync(build, executor))
                    .get(10, TimeUnit.SECONDS);
        } finally {
            executor.shutdownNow();
        }
        assertThat(computations).hasValue(2);
    }

    private int lookUp(String key) {
        return SchemaBuildMemo.computeIfAbsent(key, computations::incrementAndGet);
    }

    private static void awaitQuietly(CountDownLatch latch) {
        try {
            assertThat(latch.await(10, TimeUnit.SECONDS)).isTrue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
