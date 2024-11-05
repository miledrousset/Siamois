package fr.siamois.bean;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Data;
import org.springframework.stereotype.Service;

@Data
@SessionScoped
@Service

@Named(value = "validateInput")
public class ValidateInput implements java.io.Serializable {
    private String input1;
    private String input2;
    private String input3;


    public void validate() {
        /// connexion à la BDD
        //constructeur pour les Helpers
        // commande de création de l'action en BDD + retour

        // capter le retour

        // générer des messages
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Erreur", "la Création a réussi ");
        FacesContext.getCurrentInstance().addMessage(null, message);

        input1 = "";
        input2 = "";
        input3 = "";
    }
}
