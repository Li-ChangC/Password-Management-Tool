package pack;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Password implements Serializable {
    private static final long serialVersionUID = 1L;
    // Attributes
    private UUID identifier;
    private String username;
    private String url;
    private String password;
    private LocalDateTime editDate;
    private double strength;

    // Constructor
    public Password(String username, String url, String password, double strength) {
        this.identifier = UUID.randomUUID();
        this.username = username;
        this.url = url;
        this.password = password;
        this.editDate = LocalDateTime.now();
        this.strength = strength;
    }

    public Password(String username, String url, String password) {
        this.identifier = UUID.randomUUID();
        this.username = username;
        this.url = url;
        this.password = password;
        this.editDate = LocalDateTime.now();
        this.strength = 0.0; // Initial strength, can be updated later
    }

    public Password() {
        this.identifier = UUID.randomUUID();
        this.editDate = LocalDateTime.now();
        this.strength = 0.0;
    }

    // Getter and setter methods
    public UUID getIdentifier() {
        return identifier;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDateTime getEditDate() {
        return editDate;
    }

    public void setEditDate(LocalDateTime editDate) {
        this.editDate = editDate;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = strength;
    }

    // Override the toString method for debugging or logging
    @Override
    public String toString() {
        return "Password{" +
                "identifier=" + identifier +
                ", username='" + username + '\'' +
                ", url='" + url + '\'' +
                ", password='" + password + '\'' +
                ", editDate=" + editDate +
                ", strength=" + strength +
                '}';
    }
}
