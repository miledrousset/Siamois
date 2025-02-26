package fr.siamois.domain.utils.builder;

import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;


/**
 * Builder to create an autocompletion request for the Opentheso API.
 * @author Julien Linget
 */
public class AutocompletionRequestBuilder {

    private final String server;
    private final String input;
    private final String thesoExternalId;
    private final Set<String> langs = new TreeSet<>();
    private final Set<String> groupsId = new TreeSet<>();
    private boolean isFull = false;

    private AutocompletionRequestBuilder(String server, String thesoExternalId, String input) {
        input = URLEncoder.encode(input, StandardCharsets.UTF_8);
        if (input.equals("+") || StringUtils.isEmpty(input)) input = "%20";

        this.server = server;
        this.input = input;
        this.thesoExternalId = thesoExternalId;
    }

    public static AutocompletionRequestBuilder getBuilder(String server, String thesoExternalId, String input) {
        return new AutocompletionRequestBuilder(server, thesoExternalId, input);
    }

    public AutocompletionRequestBuilder withLang(String langCode) {
        langs.add(langCode);
        return this;
    }

    public AutocompletionRequestBuilder withGroup(String externalGroupId) {
        groupsId.add(externalGroupId);
        return this;
    }

    public AutocompletionRequestBuilder withFullConcepts() {
        isFull = true;
        return this;
    }

    public AutocompletionRequestBuilder withoutFullConcepts() {
        isFull = false;
        return this;
    }

    public String build() {
        String baseUrl = String.format("%s/openapi/v1/concept/%s/autocomplete/%s",
                server,
                thesoExternalId,
                input);

        StringBuilder sb = new StringBuilder(baseUrl);
        boolean paramCharSet = false;

        if (langs.isEmpty() && groupsId.isEmpty() && !isFull) return baseUrl;

        if (!langs.isEmpty()) {
            paramCharSet = setParamFirstChar(paramCharSet, sb);
            sb.append("lang=");
            sb.append(String.join(",", langs));
        }

        if (!groupsId.isEmpty()) {
            paramCharSet = setParamFirstChar(paramCharSet, sb);
            sb.append("group=");
            sb.append(String.join(",", groupsId));
        }

        if (isFull) {
            paramCharSet = setParamFirstChar(paramCharSet, sb);
            sb.append("full=true");
        }

        return sb.toString();
    }

    private static boolean setParamFirstChar(boolean paramCharSet, StringBuilder sb) {
        if (!paramCharSet) {
            sb.append("?");
        } else {
            sb.append("&");
        }
        return true;
    }

}
