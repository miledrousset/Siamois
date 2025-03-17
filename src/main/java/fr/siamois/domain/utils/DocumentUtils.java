package fr.siamois.domain.utils;

import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.services.document.DocumentService;
import fr.siamois.ui.bean.dialog.DocumentCreationDialog;
import jakarta.annotation.Nullable;
import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import org.springframework.util.MimeType;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentUtils {

    private static final long BYTE = 1L;
    private static final long KB = BYTE << 10;
    private static final long MB = KB << 10;
    private static final long GB = MB << 10;
    private static final long TB = GB << 10;

    private DocumentUtils() {}

    public static long byteParser(String sizeString) {
        Pattern pattern = Pattern.compile("(\\d+)([KMGTP]B)");
        Matcher matcher = pattern.matcher(sizeString.toUpperCase().trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid size format: " + sizeString);
        }

        long size = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        return switch (unit) {
            case "KB" -> size * KB;
            case "MB" -> size * MB;
            case "GB" -> size * GB;
            case "TB" -> size * TB;
            default -> throw new IllegalArgumentException("Invalid unit format: " + unit);
        };
    }

    public static String md5(BufferedInputStream fileInputStream) throws IOException {
        if (!fileInputStream.markSupported()) {
            throw new IOException("Mark/reset not supported");
        }

        fileInputStream.mark(Integer.MAX_VALUE);

        String sum = DigestUtils.md5Hex(fileInputStream);
        fileInputStream.reset();
        return sum;
    }

    public static String allowedTypesRegex(List<MimeType> allowedTypes) {
        List<String> extensions = new ArrayList<>();

        for (MimeType mimeType : allowedTypes) {
            String subtype = mimeType.getSubtype();
            extensions.add(subtype);
        }

        return String.format("/(\\.|\\/)(%s)$/", String.join("|", extensions));
    }

    public static Document prepareDocumentFrom(UploadedFile uploadedFile, DocumentCreationDialog bean) {
        Document document = new Document();
        document.setTitle(bean.getDocTitle());
        document.setNature(bean.getDocNature());
        document.setScale(bean.getDocScale());
        document.setFormat(bean.getDocType());
        document.setMimeType(uploadedFile.getContentType());
        document.setFileName(uploadedFile.getFileName());
        document.setSize(uploadedFile.getSize());

        return document;
    }

    public static @Nullable StreamedContent streamOf(DocumentService documentService, Document document) {
        Optional<FileInputStream> optStream = documentService.findInputStreamOfDocument(document);
        if (optStream.isEmpty()) {
            return null;
        }

        return DefaultStreamedContent.builder()
                .contentType(document.getMimeType())
                .contentLength(document.getSize())
                .name(document.getFileName())
                .stream(optStream::get)
                .build();
    }

}
