package com.paremin.conversion;

import java.util.HashMap;
import java.util.Map;

/**
 * Registry that maps conversion keys to their converter implementations.
 * e.g. "PNG_TO_JPEG" → PngToJpegConverter
 */
public class ConverterRegistry {

    private final Map<String, ImageConverter> converters = new HashMap<>();

    public ConverterRegistry() {
        register(new PngToJpegConverter());
        register(new JpegToPngConverter());
        register(new WebpToJpegConverter());
        register(new WebpToPngConverter());
    }

    public void register(ImageConverter converter) {
        converters.put(converter.getConversionKey(), converter);
    }

    public ImageConverter get(String conversionKey) {
        ImageConverter c = converters.get(conversionKey);
        if (c == null) throw new IllegalArgumentException(
                "No converter registered for: " + conversionKey);
        return c;
    }

    public boolean supports(String conversionKey) {
        return converters.containsKey(conversionKey);
    }
}