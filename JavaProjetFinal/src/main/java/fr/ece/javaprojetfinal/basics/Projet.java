package fr.ece.javaprojetfinal.basics;

import java.util.Date;

public class Projet {
    private int id;
    private String nom;
    private String description;
    private Date dateCreation;
    private Date dateEcheance;
    private Integer responsable;
    private String statut;

    public Projet(int id, String nom, String description, Date dateCreation, Date dateEcheance, Integer responsable, String statut) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.dateCreation = dateCreation;
        this.dateEcheance = dateEcheance;
        this.responsable = responsable;
        this.statut = statut;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getDateEcheance() { return dateEcheance; }
    public void setDateEcheance(Date dateEcheance) { this.dateEcheance = dateEcheance; }

    public Integer getResponsable() { return responsable; }
    public void setResponsable(Integer responsable) { this.responsable = responsable; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    @Override
    public String toString() {
        String s = nom != null ? nom : "Projet";
        if (statut != null) s += " (" + statut + ")";
        return s;
    }
}
