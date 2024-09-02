package ec.edu.espol.juegouno;

public class Jugador {
    private final Mano mano;
    private final String nombre;
 
    public Jugador(String nombre) {
       this.nombre = nombre;
       this.mano = new Mano();
    }
 
    public Carta tomarCarta(Baraja baraja) {
       Carta carta = baraja.robarCarta();
       mano.agregarCarta(carta);
       return carta;
    }
 
    public Carta jugarCarta(int indice) {
       return mano.removerCarta(indice);
    }
 
    public Mano getMano() {
       return mano;
    }
 
    public String getNombre() {
       return nombre;
    }
 
    public boolean notieneCartas() {
       return mano.getCartas().isEmpty();
    }

}
