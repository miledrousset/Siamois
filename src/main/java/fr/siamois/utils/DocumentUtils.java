package fr.siamois.utils;

import fr.siamois.domain.models.document.Document;
import fr.siamois.domain.services.vocabulary.ConceptService;
import fr.siamois.ui.bean.dialog.document.DocumentCreationBean;
import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.model.file.UploadedFile;
import org.springframework.util.MimeType;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
            if (mimeType.toString().equalsIgnoreCase("*/*"))
                return "*";

            String subtype = mimeType.getSubtype();
            extensions.add(subtype);
        }

        return String.format("/(\\.|\\/)(%s)$/", String.join("|", extensions));
    }

    public static String allowedExtensionsStringList(List<MimeType> allowedTypes) {
        List<String> extensions = new ArrayList<>();
        for (MimeType type : allowedTypes) {
            if (type.toString().equalsIgnoreCase("*/*"))
                return "*";
            extensions.add("." + type.getSubtype());
        }
        return String.join(",", extensions);
    }

    public static Document prepareDocumentFrom(ConceptService conceptService, UploadedFile uploadedFile, DocumentCreationBean bean) {
        Document document = new Document();
        document.setTitle(bean.getDocTitle());
        document.setNature(conceptService.saveOrGetConcept(bean.getDocNature()));
        document.setScale(conceptService.saveOrGetConcept(bean.getDocScale()));
        document.setFormat(conceptService.saveOrGetConcept(bean.getDocType()));
        document.setMimeType(uploadedFile.getContentType());
        document.setFileName(uploadedFile.getFileName());
        document.setSize(uploadedFile.getSize());
        document.setDescription(bean.getDocDescription());
        return document;
    }

}
