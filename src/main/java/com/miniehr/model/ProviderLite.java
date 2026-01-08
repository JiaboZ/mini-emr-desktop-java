package com.miniehr.model;


public class ProviderLite {
    private int providerId;
    private String fullName;
    private String specialty;

    public int getProviderId() { return providerId; }
    public void setProviderId(int providerId) { this.providerId = providerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }

    @Override
    public String toString() {
        // JComboBox 显示用
        String sp = (specialty == null || specialty.isBlank()) ? "" : (" - " + specialty);
        return fullName + sp;
    }
}
