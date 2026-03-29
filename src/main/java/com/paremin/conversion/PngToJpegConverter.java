package com.paremin.conversion;

import com.paremin.model.ConversionTask;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Iterator;

public class PngToJpegConverter implements ImageConverter {

    @Override
    public void convert(ConversionTask task) throws IOException {
        BufferedImage original = ImageIO.read(task.getInputPath().toFile());
        if (original == null) throw new IOException("Could not read PNG: " + task.getInputPath());

        // PNG may have transparency — JPEG does not support alpha channel
        // so we flatten it onto a white background first
        BufferedImage flattened = new BufferedImage(
                original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = flattened.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, flattened.getWidth(), flattened.getHeight());
        g.drawImage(original, 0, 0, null);
        g.dispose();

        Files.createDirectories(task.getOutputPath().getParent());

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpeg");
        if (!writers.hasNext()) throw new IOException("No JPEG writer found");
        ImageWriter writer = writers.next();

        ImageWriteParam params = writer.getDefaultWriteParam();
        params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        params.setCompressionQuality(0.9f); // high quality default

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(task.getOutputPath().toFile())) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(flattened, null, null), params);
        } finally {
            writer.dispose();
        }
    }

    @Override
    public String getConversionKey() { return "PNG_TO_JPEG"; }
}