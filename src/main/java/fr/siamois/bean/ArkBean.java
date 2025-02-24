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

    // Ark Server
    private String vArkServer;
    private String vArkUri;
    private String vArkNaan;
    private String vArkPrefix;
    private String vArkUser;
    private String vArkPassword;

    // Local Ark Server
    private String vLocalNaan;
    private String vLocalPrefix;
    private String vLocalSize;
    private boolean vLocalIsCaps = false;

    public void arkServerToggle() {
        if (vIsArkServer && vIsLocalArkServer) {
            vIsLocalArkServer = false;
        }
    }

    public void arkLocalToggle() {
        if (vIsLocalArkServer && vIsArkServer) {
            vIsArkServer = false;
        }
    }

    public void saveArkConfig() {
        // TODO
    }

    public void getSaveLocalArkConfig() {
        // TODO
    }
}
