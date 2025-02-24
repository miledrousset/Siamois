package fr.siamois.bean;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.faces.bean.SessionScoped;
import java.io.Serializable;

@Setter
@Getter
@Component
@SessionScoped
public class ArkBean implements Serializable {


    //  -- Fields

    // Toggles
    private boolean vIsArkServer;
    private boolean vIsLocalArkServer;

    // Local Ark Server
    private String vLocalNaan;
    private String vLocalPrefix;
    private String vLocalSize;
    private boolean vLocalIsCaps = false;


    public void getSaveLocalArkConfig() {
        // I will do it later
    }
}
