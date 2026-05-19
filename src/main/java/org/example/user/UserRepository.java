package org.example.user;

public interface UserRepository {
    void save(User user);
    User findByEmail(String email);
    void delete(String email);
}
