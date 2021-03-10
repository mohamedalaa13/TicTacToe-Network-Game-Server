package serverPackage;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class PlayerForListInServer {

    private SimpleStringProperty username;
    private SimpleStringProperty email;
    private SimpleIntegerProperty score;
    private SimpleStringProperty level;
    private SimpleStringProperty status;

    PlayerForListInServer(String username, String email, int score) {
        this.username = new SimpleStringProperty(username);
        this.email = new SimpleStringProperty(email);
        this.score = new SimpleIntegerProperty(score);
        if(score > 500) {
            this.level = new SimpleStringProperty("Expert");
        } else if(200 < score && score < 500) {
            this.level = new SimpleStringProperty("Intermediate");
        } else {
            this.level = new SimpleStringProperty("Beginner");
        }
    }

    public PlayerForListInServer(String username, int score, int status) {
        this.username = new SimpleStringProperty(username);
        this.score = new SimpleIntegerProperty(score);
        if(status == 0) {
            this.status = new SimpleStringProperty("Offline");
        }
        else {
            this.status = new SimpleStringProperty("Online");
        }
    }

    public String getUsername() {
        return username.get();
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) { this.email.set(email);}

    public int getScore() {
        return score.get();
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public String getLevel() {
        return level.get();
    }

    public  void setLevel(String level) {
        this.level.set(level);
    }

    public String getStatus() {return status.get();}

    public void setStatus(String status) {this.status.set(status);}
}
