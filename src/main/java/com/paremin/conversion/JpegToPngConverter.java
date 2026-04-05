package com.paremin.conversion;

import com.paremin.model.ConversionTask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;

public class JpegToPngConverter implements ImageConverter {

    @Override
    public void convert(ConversionTask task) throws IOException {
        BufferedImage image = ImageIO.read(task.getInputPath().toFile());
        if (image == null) throw new IOException("Could not read JPEG: " + task.getInputPath());

        Files.createDirectories(task.getOutputPath().getParent());
        ImageIO.write(image, "png", task.getOutputPath().toFile());
    }

    @Override
    public String getConversionKey() { return "JPEG_TO_PNG"; }
}