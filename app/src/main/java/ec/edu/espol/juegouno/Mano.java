package ec.edu.espol.juegouno;

import java.util.ArrayList;


public class Mano {
    private final ArrayList<Carta> cartas;

    public Mano() {
        cartas = new ArrayList<>();
    }

    public void agregarCarta(Carta carta) {
        cartas.add(carta);
    }

    public Carta removerCarta(int indice) {
        return cartas.remove(indice);
    }

    public Carta obtenerCarta(int indice) {
        return cartas.get(indice);
    }

    public ArrayList<Carta> getCartas() {
        return cartas;
    }
}
