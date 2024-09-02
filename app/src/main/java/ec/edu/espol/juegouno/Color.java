package ec.edu.espol.juegouno;

import android.os.Build;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public enum Color {
    ROJO("rojo"),
    AMARILLO("amarillo"),
    VERDE("verde"),
    AZUL("azul"),
    NEGRO("negro");

    private final String color;

    // Constructor único que acepta el nombre completo del color
    Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    private static Random rand;

    static {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                rand = SecureRandom.getInstanceStrong();
            }
        } catch (NoSuchAlgorithmException e) {
            rand = new SecureRandom(); // Fallback a una implementación predeterminada
        }
    }

    public static Color getRandomColor() {
        int x = rand.nextInt(Color.values().length);
        return Color.values()[x];
    }
}

