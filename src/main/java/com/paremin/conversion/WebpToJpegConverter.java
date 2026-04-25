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

public class WebpToJpegConverter implements ImageConverter {

    @Override
    public void convert(ConversionTask task) throws IOException {
        validate(task);
        BufferedImage original = ImageIO.read(task.getInputPath().toFile());
        if (original == null) throw new IOException("Could not read WebP: " + task.getInputPath());

        // Flatten transparency onto white background — JPEG has no alpha
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
        params.setCompressionQuality(0.9f);

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(task.getOutputPath().toFile())) {
            writer.setOutput(ios);
            writer.write(null, new IIOImage(flattened, null, null), params);
        }
        finally {
            writer.dispose();
        }
    }

    @Override
    public String getConversionKey() { return "WEBP_TO_JPEG"; }
}