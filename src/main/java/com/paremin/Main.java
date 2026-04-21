package com.paremin;

import com.paremin.model.ConversionResult;
import com.paremin.model.ConversionTask;
import com.paremin.model.ConversionTask.Format;
import com.paremin.service.BatchConversionService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Entry point for paremin — a concurrent image format converter.
 *
 * Usage:
 *   java -jar paremin.jar <inputDir> <outputDir> <targetFormat>
 *
 * Example:
 *   java -jar paremin.jar ./images ./output WEBP
 */
public class Main {

    public static void main(String[] args) {
        // ── Demo: hardcoded tasks, replace with real files to test ──
        Path outputDir = Paths.get("output");

        List<ConversionTask> tasks = List.of(
                new ConversionTask(Paths.get("samples/photo1.png"),  outputDir.resolve("photo1.jpg"),  Format.JPEG),
                new ConversionTask(Paths.get("samples/photo2.png"),  outputDir.resolve("photo2.webp"), Format.WEBP),
                new ConversionTask(Paths.get("samples/photo3.jpg"),  outputDir.resolve("photo3.png"),  Format.PNG),
                new ConversionTask(Paths.get("samples/photo4.jpg"),  outputDir.resolve("photo4.webp"), Format.WEBP)
        );

        System.out.println("paremin — concurrent image converter");
        System.out.printf("Converting %d image(s) using %d thread(s)%n%n",
                tasks.size(),
                Math.max(2, Runtime.getRuntime().availableProcessors()));

        try (BatchConversionService service = new BatchConversionService()) {

            // onResult callback fires as each image finishes
            List<ConversionResult> results = service.convertAll(tasks, result ->
                    System.out.println("[DONE] " + result)
            );

            // Summary
            long succeeded = results.stream().filter(ConversionResult::isSuccess).count();
            long failed    = results.size() - succeeded;

            System.out.println("\n── Summary ──────────────────────────────");
            System.out.printf("  Succeeded : %d%n", succeeded);
            System.out.printf("  Failed    : %d%n", failed);
            System.out.println("─────────────────────────────────────────");
        }
    }
}