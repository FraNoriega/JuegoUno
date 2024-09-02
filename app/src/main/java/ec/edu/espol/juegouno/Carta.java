package ec.edu.espol.juegouno;

import androidx.annotation.NonNull;

public class Carta {
    private final Color color;
    private final Tipo tipo;

    public Carta(Color color, Tipo tipo) {
       this.color = color;
       this.tipo = tipo;
    }

    public Color getColor() {
       return color;
    }

    public Tipo getTipo() {
       return tipo;
    }
    public boolean esComodin() {
       return tipo == Tipo.REVERSE || tipo == Tipo.BLOQUEO || tipo == Tipo.CAMBIO_DE_COLOR || tipo == Tipo.MAS_CUATRO || tipo == Tipo.MAS_DOS;
    }
    public boolean esEspecial(){
        return ((color == Color.AMARILLO || color == Color.ROJO || color == Color.VERDE || color == Color.AZUL) && (tipo == Tipo.CAMBIO_DE_COLOR));
    }

    @NonNull
    @Override
    public String toString() {
       return  "carta_" + color.getColor() + "_" + tipo.getSimbolo();
    }
}
