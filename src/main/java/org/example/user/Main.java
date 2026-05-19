package org.example.user;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) throws Exception {


        String url = "jdbc:sqlite:users.db";
        Connection connection = DriverManager.getConnection(url);


        UserRepository repo = new UserRepositoryImpl(connection);


        UserService service = new UserService(repo);


        UserController controller = new UserController(service);


        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/users", controller::handle);

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port 8080");
    }
}
