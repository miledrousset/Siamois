/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fr.siamois.ws.api.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author julie
 */
public class LangHelper {

    public String translate(String jsonOAS, ResourceBundle bundle) {
        
        Matcher matcher = Pattern.compile("\\$\\{.*?\\}\\$").matcher(jsonOAS);
        
        while (matcher.find()) {
            String s = matcher.group();
            String bracketLessKey = s.replace("${","").replace("}$","");
            if (!bracketLessKey.equals("BASE_SERVER")) {
                String resource = bundle.getString(bracketLessKey);
                resource = resource.replace("\n", "<br>");
                resource = resource.replace("\"", "\\\"");
                jsonOAS = jsonOAS.replace(s, resource);
            }
        }
        
        return jsonOAS;
    }
    
}
