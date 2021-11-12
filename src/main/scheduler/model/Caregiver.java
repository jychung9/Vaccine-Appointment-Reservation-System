package scheduler.model;

import scheduler.db.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Caregiver {
    private final String username;
    private final String email;
    private final byte[] salt;
    private final byte[] hash;

    private Caregiver(CaregiverBuilder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.salt = builder.salt;
        this.hash = builder.hash;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getHash() {
        return hash;
    }

    public static class CaregiverBuilder {
        private final String username;
        private final String email;
        private final byte[] salt;
        private final byte[] hash;

        public CaregiverBuilder(String username, String email, byte[] salt, byte[] hash) {
            this.username = username;
            this.email = email;
            this.salt = salt;
            this.hash = hash;
        }

        public Caregiver build() throws SQLException {
            ConnectionManager cm = new ConnectionManager();
            Connection con = cm.createConnection();

            String addCaregiver = "INSERT INTO caregivers VALUES (? , ?, ?, ?)";
            PreparedStatement statement = con.prepareStatement(addCaregiver);
            statement.setString(1, this.username);
            statement.setString(2, this.username);
            statement.setBytes(3, this.salt);
            statement.setBytes(4, this.hash);
            statement.executeUpdate();

            cm.closeConnection();
            return new Caregiver(this);
        }
    }
}
