package fr.siamois.ui.bean.postlogin;

public interface PostLoginGate {
    boolean shouldApply();
    void apply();
}
