package org.example.user;
import java.sql.*;

public class UserRepositoryImpl implements UserRepository {


    private final Connection connection;
    public UserRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void save(User user){
        String sql = "INSERT INTO users(name, age, email) VALUES(?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setInt(2, user.getAge());
            stmt.setString(3, user.getEmail());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
     public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                return new User(rs.getString("name"),rs.getInt("age"),rs.getString("email"));
            }
        } catch(SQLException e){ e.printStackTrace();}
        return null;
    }


    @Override
    public void delete(String email){
        String sql = "DELETE FROM users WHERE email = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
