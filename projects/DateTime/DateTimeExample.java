package AllFiles.app1;

import java.time.LocalDateTime;

public class DateTimeExample {
    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("Hello from Dockerized Java application! Current date and time: " + now);
    }
}
