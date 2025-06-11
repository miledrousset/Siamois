package fr.siamois.utils;

import fr.siamois.domain.models.UserInfo;
import fr.siamois.infrastructure.api.dto.FullInfoDTO;
import fr.siamois.infrastructure.api.dto.PurlInfoDTO;

import java.util.Arrays;

public class FullInfoDTOUtils {

    private FullInfoDTOUtils() {}

    public static PurlInfoDTO getPrefLabelOfLang(UserInfo info, FullInfoDTO fullConcept) {
        return Arrays.stream(fullConcept.getPrefLabel())
                .filter((purlInfoDTO -> purlInfoDTO.getLang().equalsIgnoreCase(info.getLang())))
                .findFirst()
                .orElse(fullConcept.getPrefLabel()[0]);
    }

}
