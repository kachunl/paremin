package com.paremin.conversion;

import com.paremin.model.ConversionTask;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class JpegToWebpConverter extends BaseWebpConverter {

    @Override
    public void convert(ConversionTask task) throws IOException {
        validate(task);
        BufferedImage image = ImageIO.read(task.getInputPath().toFile());
        if (image == null) throw new IOException("Could not read JPEG: " + task.getInputPath());
        writeAsWebp(image, task);
    }

    @Override
    public String getConversionKey() { return "JPEG_TO_WEBP"; }
}