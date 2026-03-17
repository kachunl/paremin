package com.paremin.model;

import java.nio.file.Path;

/**
 * Represents a single image conversion job.
 * Immutable value object passed to the conversion pipeline.
 */
public class ConversionTask {
    public enum Format { JPEG, PNG, WEBP }

    private final Path inputPath;
    private final Path outputPath;
    private final Format targetFormat;

    public ConversionTask(Path inputPath, Path outputPath, Format targetFormat) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.targetFormat = targetFormat;
    }
}
