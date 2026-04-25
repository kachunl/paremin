package com.paremin.conversion;

import com.paremin.model.ConversionTask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;

public class WebpToPngConverter implements ImageConverter {

    @Override
    public void convert(ConversionTask task) throws IOException {
        validate(task);
        BufferedImage image = ImageIO.read(task.getInputPath().toFile());
        if (image == null) throw new IOException("Could not read WebP: " + task.getInputPath());

        Files.createDirectories(task.getOutputPath().getParent());
        ImageIO.write(image, "png", task.getOutputPath().toFile());
    }

    @Override
    public String getConversionKey() { return "WEBP_TO_PNG"; }
}