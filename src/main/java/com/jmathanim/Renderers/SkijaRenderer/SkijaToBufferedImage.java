package com.jmathanim.Renderers.SkijaRenderer;


import io.github.humbleui.skija.Bitmap;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.ImageInfo;
import io.github.humbleui.skija.Surface;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

public class SkijaToBufferedImage {
    /**
     * Convert Skija image to BGRA Imagebuffer
     * @param surface Skija surface
     * @return BufferImage output
     */
    public static BufferedImage convertTo4ByteRGBA(Surface surface) {
        int width = surface.getWidth();
        int height = surface.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        int[] argb = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        try (Image snapshot = surface.makeImageSnapshot();
             Bitmap bitmap = new Bitmap()) {

            bitmap.allocPixels(ImageInfo.makeN32Premul(width, height));

            boolean ok = snapshot.readPixels(null, bitmap, 0, 0, true);
            if (!ok) throw new RuntimeException("readPixels failed");

            ByteBuffer buffer = bitmap.peekPixels();
            buffer.rewind();

            for (int i = 0; i < width * height; i++) {
                int b = buffer.get() & 0xFF;
                int g = buffer.get() & 0xFF;
                int r = buffer.get() & 0xFF;
                int a = buffer.get() & 0xFF;
                argb[i] = (a << 24) | (r << 16) | (g << 8) | b;
            }
        }

        return image;
    }

    /**
     * Convert Skija image to BGR Imagebuffer, with no alpha channel.
     * @param surface Skija surface
     * @return BufferImage output
     */
    public static BufferedImage convertTo3ByteBGR(Surface surface) {
        int width = surface.getWidth();
        int height = surface.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        try (Image snapshot = surface.makeImageSnapshot();
             Bitmap bitmap = new Bitmap()) {

            bitmap.allocPixels(ImageInfo.makeN32Premul(width, height));

            boolean ok = snapshot.readPixels(null, bitmap, 0, 0, true);
            if (!ok) throw new RuntimeException("readPixels failed");

            ByteBuffer buffer = bitmap.peekPixels();
            buffer.rewind();

            for (int i = 0, j = 0; i < width * height; i++, j += 3) {
                byte b = buffer.get();   // B
                byte g = buffer.get();   // G
                byte r = buffer.get();   // R
                buffer.get();            // A → lo ignoramos
                data[j] = b;
                data[j + 1] = g;
                data[j + 2] = r;
            }
        }

        return image;
    }

}
