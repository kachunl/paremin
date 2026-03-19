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

    public Path getInputPath() { return inputPath; }
    public Path getOutputPath() { return outputPath; }
    public Format getTargetFormat() { return targetFormat; }

    /** Derive the source format from the input file extension. */
    public Format getSourceFormat() {
        String name = inputPath.getFileName().toString().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return Format.JPEG;
        if (name.endsWith(".png")) return Format.PNG;
        if (name.endsWith(".webp")) return Format.WEBP;
        throw new IllegalStateException("Unsupported format for file: " + name);
    }

    /** Convenience key for registry lookup e.g. "PNG_TO_JPEG" */
    public String getConversionKey() {
        return getSourceFormat() + "_TO_" + targetFormat;
    }

    @Override
    public String toString() {
        return String.format("ConversionTask{input=%s, output=%s, %s → %s}",
                inputPath.getFileName(), outputPath.getFileName(),
                getSourceFormat(), targetFormat);
    }
}
