package com.urlshortener.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating QR codes
 */
@Slf4j
@Service
public class QrCodeService {

    private static final int QR_CODE_SIZE = 250;

    /**
     * Generate QR code for a URL and return as Base64 encoded string
     */
    public String generateQrCode(String url) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);
            BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            return "data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes);
        } catch (WriterException | IOException e) {
            log.error("Error generating QR code for URL: {}", url, e);
            return null;
        }
    }
}
