package fr.siamois.infrastructure.api.dto;

import lombok.Data;

@Data
public class PurlInfoDTO {
    private String value;
    private String type;
    private String datatype;
    private String lang;

    public PurlInfoDTO() {}

    public PurlInfoDTO(String dataType, String value) {
        this.value = value;
        this.datatype = dataType;
    }

    public PurlInfoDTO(String dataType, String value, String lang) {
        this.value = value;
        this.datatype = dataType;
        this.lang = lang;
    }

}
