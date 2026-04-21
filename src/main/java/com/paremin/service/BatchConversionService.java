package com.paremin.service;

import com.paremin.conversion.ConverterRegistry;
import com.paremin.conversion.ImageConverter;
import com.paremin.model.ConversionResult;
import com.paremin.model.ConversionTask;

import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Processes a batch of {@link ConversionTask}s in parallel using a fixed
 * thread pool backed by {@link CompletableFuture}.
 *
 * <p>Architecture:
 * <pre>
 *   BatchConversionService
 *     └─ ExecutorService (fixed thread pool, N = CPU cores)
 *          └─ CompletableFuture per task
 *               ├─ validates input file
 *               ├─ dispatches to format-specific ImageConverter
 *               └─ emits ConversionResult (success or failure)
 * </pre>
 */
public class BatchConversionService implements AutoCloseable {

    private final ConverterRegistry registry;
    private final ExecutorService executor;
    private final int threadCount;

    /**
     * Creates a service using all available CPU cores, minimum 2.
     */
    public BatchConversionService() {
        this(Math.max(2, Runtime.getRuntime().availableProcessors()));
    }

    /**
     * Creates a service with an explicit thread count.
     *
     * @param threadCount number of parallel conversion threads
     */
    public BatchConversionService(int threadCount) {
        this.threadCount = threadCount;
        this.registry = new ConverterRegistry();
        this.executor = Executors.newFixedThreadPool(threadCount, r -> {
            Thread t = new Thread(r, "paremin-converter-" + System.nanoTime());
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Submits all tasks for parallel conversion and blocks until every task
     * has either succeeded or failed.
     *
     * @param tasks    list of conversion jobs
     * @param onResult optional callback invoked as each result arrives
     * @return list of results in completion order
     */
    public List<ConversionResult> convertAll(List<ConversionTask> tasks,
                                             Consumer<ConversionResult> onResult) {
        List<CompletableFuture<ConversionResult>> futures = tasks.stream()
                .map(task -> CompletableFuture
                        .supplyAsync(() -> runTask(task), executor)
                        .whenComplete((result, ex) -> {
                            if (onResult != null && result != null) {
                                onResult.accept(result);
                            }
                        }))
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    /**
     * Convenience overload with no progress callback.
     */
    public List<ConversionResult> convertAll(List<ConversionTask> tasks) {
        return convertAll(tasks, null);
    }

    /**
     * Converts a single image synchronously.
     */
    public ConversionResult convertOne(ConversionTask task) {
        return runTask(task);
    }

    // -------------------------------------------------------------------------
    // Internal
    // -------------------------------------------------------------------------

    /**
     * Executes one conversion task and wraps the outcome in a
     * {@link ConversionResult}. Never throws — all exceptions are captured.
     */
    private ConversionResult runTask(ConversionTask task) {
        long start = System.currentTimeMillis();
        try {
            // Check input exists before hitting the registry
            if (!Files.exists(task.getInputPath())) {
                return ConversionResult.failure(task,
                        "Input file not found: " + task.getInputPath(),
                        elapsed(start));
            }

            String key = task.getConversionKey();

            if (!registry.supports(key)) {
                return ConversionResult.failure(task,
                        "Unsupported conversion: " + key, elapsed(start));
            }

            ImageConverter converter = registry.get(key);
            converter.convert(task);

            return ConversionResult.success(task, elapsed(start));

        } catch (Exception e) {
            return ConversionResult.failure(task,
                    e.getClass().getSimpleName() + ": " + e.getMessage(), elapsed(start));
        }
    }

    private long elapsed(long startMs) {
        return System.currentTimeMillis() - startMs;
    }

    public int getThreadCount() { return threadCount; }

    /**
     * Shuts down the thread pool gracefully.
     */
    @Override
    public void close() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}