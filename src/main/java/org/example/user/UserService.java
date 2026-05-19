package org.example.user;



public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public void register(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User is null");
        }

        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }

        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalStateException("User already exists");
        }

        userRepository.save(user);
    }
    public User getByEmail(String email) {
        if (email == null) {
            throw new IllegalArgumentException("Email is null");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        return user;
    }

    public void delete(String email) {
        User user = getByEmail(email);
        userRepository.delete(user.getEmail());
    }
}
