package com.paremin.conversion;

import com.paremin.model.ConversionTask;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Strategy interface for format-specific image converters.
 * Each implementation handles one conversion pair (e.g. PNG → JPEG).
 */
public interface ImageConverter {

    /**
     * Convert the image described by the task and write it to the output path.
     *
     * @param task the conversion job containing input/output paths and target format
     * @throws IOException if reading or writing fails
     */
    void convert(ConversionTask task) throws IOException;

    /**
     * Returns the conversion key this converter handles.
     * e.g. "PNG_TO_JPEG", "JPEG_TO_WEBP"
     */
    String getConversionKey();

    /**
     * Validates the input file exists and is readable.
     * Called by all converters before processing.
     */
    default void validate(ConversionTask task) throws IOException {
        if (!Files.exists(task.getInputPath())) {
            throw new IOException("Input file not found: " + task.getInputPath());
        }
        if (!Files.isReadable(task.getInputPath())) {
            throw new IOException("Input file is not readable: " + task.getInputPath());
        }
    }
}