package com.paremin.conversion;

import com.paremin.model.ConversionTask;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

/**
 * Shared WebP writing logic for any converter that outputs WebP.
 * Subclasses only need to handle reading their specific source format.
 */
abstract class BaseWebpConverter implements ImageConverter {

    /**
     * Writes a BufferedImage to the output path specified in the task as WebP.
     */
    protected void writeAsWebp(BufferedImage image, ConversionTask task) throws IOException {
        Files.createDirectories(task.getOutputPath().getParent());

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("webp");
        if (!writers.hasNext()) throw new IOException(
                "No WebP writer found — ensure TwelveMonkeys imageio-webp is on the classpath");
        ImageWriter writer = writers.next();

        ImageWriteParam params = writer.getDefaultWriteParam();
        if (params.canWriteCompressed()) {
            params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            params.setCompressionQuality(0.9f);
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(task.getOutputPath().toFile())) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(image, null, null), params);
        } finally {
            writer.dispose();
        }
    }
}