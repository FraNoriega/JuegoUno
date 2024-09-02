package ec.edu.espol.juegouno;

import java.util.ArrayList;
import java.util.Collections;


public class Baraja {
    private final ArrayList<Carta> cartas;

   public Baraja() {
      cartas = new ArrayList<>();
      inicializarBaraja();
      barajar();
   }

   private void inicializarBaraja() {
      for (Color color : Color.values()){
         for(Tipo tipo : Tipo.values()){
            if (color != Color.NEGRO && tipo != Tipo.CAMBIO_DE_COLOR && tipo != Tipo.MAS_CUATRO)
                cartas.add(new Carta(color, tipo));
         }
      }
      for(int i = 0; i < 4; ++i) {
         cartas.add(new Carta(Color.NEGRO, Tipo.MAS_CUATRO));
         cartas.add(new Carta(Color.NEGRO, Tipo.CAMBIO_DE_COLOR));
      }

   }

   private  void barajar() {
        Collections.shuffle(cartas);
   }

   public Carta robarCarta() {
      return cartas.remove(cartas.size() - 1);
   }

   public void devolverCarta(Carta carta) {
      cartas.add(0, carta);
   }
}
