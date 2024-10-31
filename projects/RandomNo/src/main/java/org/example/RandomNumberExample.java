package org.example.RandomNumber;

import java.util.Random;

public class RandomNumberExample {
    public static void main(String[] args) {
        Random random = new Random();
        int randomNumber = random.nextInt(100); // Generiert eine Zufallszahl zwischen 0 und 99
        System.out.println("Hello from Dockerized Java application! Random number: " + randomNumber);
    }
}
