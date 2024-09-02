package ec.edu.espol.juegouno;

import android.os.Looper;
import android.util.Log;
import android.os.Handler;
import java.util.HashMap;
import java.util.Map;


public class JuegoUNO {
    public Baraja baraja;
    public Jugador jugador;
    public Jugador maquina;
    public Carta cartaEnJuego;
    public boolean turno;
    private int cartasParaRobar = 0;
    private final MainActivity mainActivity;
    private boolean esperandoCambioColor = false;
    public boolean dijoUno;
    boolean bloqRever;
    private boolean juegoTerminado = false;

    public boolean isEsperandoCambioColor() {
        return esperandoCambioColor;
    }

    public void setEsperandoCambioColor(boolean esperandoCambioColor) {
        this.esperandoCambioColor = esperandoCambioColor;
    }
    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public void setJuegoTerminado(boolean juegoTerminado) {
        this.juegoTerminado = juegoTerminado;
    }
    public JuegoUNO(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        baraja = new Baraja();
        jugador = new Jugador("Jugador");
        maquina = new Jugador("Máquina");
        turno = true;
        dijoUno = false;
        bloqRever = false;
    }

    public void iniciarJuego() {
       for(int i = 0; i < 7; ++i) {
          jugador.tomarCarta(baraja);
          maquina.tomarCarta(baraja);
       }
 
       cartaEnJuego = baraja.robarCarta();
       while (cartaEnJuego.esComodin()){
          baraja.devolverCarta(cartaEnJuego);
          cartaEnJuego = baraja.robarCarta();
       }
    }
 
    public boolean validarJugada(Carta carta) {
       return carta.getColor() == cartaEnJuego.getColor() || carta.getTipo() == cartaEnJuego.getTipo() || carta.getColor() == Color.NEGRO;
    }
    public void cambiarTurno() {
        if (bloqRever) {
            // Resetea bloqRever y no cambia el turno
            bloqRever = false;
            Log.d("CambioTurno", "El turno se mantiene debido a una carta de reversa o bloqueo.");
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (turno) {
                        // Si es el turno del jugador, indica que sigue siendo su turno
                        mainActivity.mostrarMensaje("Es tu turno nuevamente");
                    } else {
                        // Si es el turno de la máquina, llama a turnoMaquina para que juegue de nuevo
                        turnoMaquina();
                    }
                }
            }, 1500);
        } else {
            // Cambia el turno normalmente
            turno = !turno;
            if (turno) {
                Log.d("CambioTurno", "Es el turno del jugador");
                mainActivity.mostrarMensaje("Es tu turno");
            } else {
                Log.d("CambioTurno", "Es el turno de la máquina");
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        turnoMaquina();
                    }
                }, 1500);
            }
        }
    }


    private void aplicarEfectoCarta(Carta carta) {
        switch (carta.getTipo()) {
            case MAS_DOS -> {
                cartasParaRobar+=2 ;
                robarCartas(maquina,cartasParaRobar);
                cartasParaRobar = 0;
            }
            case MAS_CUATRO -> {
                cartasParaRobar+=4;
                robarCartas(maquina,cartasParaRobar);
                cartasParaRobar = 0;
                cambiarColor();
            }
            case REVERSE, BLOQUEO -> bloqRever = true;
            case CAMBIO_DE_COLOR -> cambiarColor();
        }
    }

    private void aplicarEfectoCartaMaquina(Carta carta) {
        switch (carta.getTipo()) {
            case MAS_DOS -> {
                cartasParaRobar += 2;
                robarCartas(jugador,cartasParaRobar);
                cartasParaRobar = 0;
            }
            case MAS_CUATRO -> {
                cartasParaRobar += 4;
                robarCartas(jugador,cartasParaRobar);
                cartasParaRobar = 0;
                cambiarColorMaquina(); // La máquina cambia el color después de jugar un +4
            }
            case REVERSE, BLOQUEO -> bloqRever = true;
            case CAMBIO_DE_COLOR -> cambiarColorMaquina();
        }
    }


    public void cambiarColorMaquina() {
        Map<Color, Integer> colorCount = new HashMap<>();
        setEsperandoCambioColor(true);
        // Inicializar el contador para cada color
        for (Color color : Color.values()) {
            colorCount.put(color, 0);
        }

        // Contar las cartas de cada color en la mano de la máquina
        for (Carta carta : maquina.getMano().getCartas()) {
            if (carta.getColor() != Color.NEGRO) {  // Ignorar cartas comodín
                colorCount.put(carta.getColor(), colorCount.get(carta.getColor()) + 1);
            }
        }

        // Encontrar el color más abundante
        Color colorSeleccionado = null;
        int maxCount = -1;

        for (Map.Entry<Color, Integer> entry : colorCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                colorSeleccionado = entry.getKey();
            }
        }

        if (colorSeleccionado != null) {
            actualizarColorEnJuego(colorSeleccionado);
            setEsperandoCambioColor(false);
            mainActivity.mostrarMensaje("La máquina ha cambiado el color a " + colorSeleccionado);
        } else {
            mainActivity.mostrarMensaje("Error: No se pudo determinar un color válido.");
        }
    }

    public void cambiarColor() {
        setEsperandoCambioColor(true);  // Activar la espera de color
        mainActivity.mostrarDialogoCambioDeColor();
    }


    public void actualizarColorEnJuego(Color nuevoColor) {
        // Cambia el color de la carta en juego
        cartaEnJuego = new Carta(nuevoColor, Tipo.CAMBIO_DE_COLOR);
        mainActivity.actualizarCartaCentral(cartaEnJuego);
    }

    private void robarCartas(Jugador jugador, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
                // Añadir la carta al layout correspondiente y mapearla
            if (jugador.equals(this.jugador)) {
                mainActivity.updateCard(jugador, mainActivity.getHandPlayer());
            } else {
                mainActivity.updateCard(jugador, mainActivity.getHandMachine());
            }

        }
    }

    void turnoJugador(Carta carta) {
        verificarFinDelJuego();
        if (validarJugada(carta)) {
            cartaEnJuego = carta;
            aplicarEfectoCarta(carta);
            verificarFinDelJuego();
            if (!cartaEnJuego.esEspecial()){
                baraja.devolverCarta(cartaEnJuego);
            }
        } else {
            mainActivity.mostrarMensaje("Jugada inválida");
        }
        jugadorDiceUno();
        verificarFinDelJuego();
        cambiarTurno();
    }
    void jugadorDiceUno(){
        if(jugador.getMano().getCartas().size()==1){
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!dijoUno) {
                        // El jugador dijo "UNO" a tiempo
                        mainActivity.mostrarMensaje("¡No dijiste UNO!");
                        robarCartas(jugador,2);
                    }
                }
            }, 3000); // 3000 milisegundos = 3 segundos
        }
    }

    public void turnoMaquina() {
        if (isJuegoTerminado()) {
            Log.d("TurnoMaquina", "El juego ha terminado, la máquina no puede jugar.");
            return;  // Salir del método si el juego ha terminado
        }

        Log.d("TurnoMaquina", "Inicio del turno de la máquina");
        verificarFinDelJuego();

        if (isEsperandoCambioColor()) {
            Log.d("TurnoMaquina", "Esperando a que el jugador elija un color...");
            return;  // Salir del método si estamos esperando que el jugador elija un color
        }

        int i = 0;
        boolean jugadaValida = false;
        while (i < maquina.getMano().getCartas().size() && !jugadaValida) {
            Carta carta = maquina.getMano().obtenerCarta(i);
            Log.d("TurnoMaquina", "Revisando carta: " + carta.toString() + " en el índice " + i);

            if (validarJugada(carta)) {
                Log.d("TurnoMaquina", "La carta " + carta.toString() + " es válida y será jugada");
                cartaEnJuego = maquina.jugarCarta(i);

                jugadaValida = true;

                // Actualiza la UI con la carta jugada
                mainActivity.actualizarCartaCentral(cartaEnJuego);
                // Eliminar la carta jugada del layout
                if(carta.esComodin()){
                    aplicarEfectoCartaMaquina(carta);
                }

                if (mainActivity.getHandMachine().getChildAt(i) != null) {
                    mainActivity.getHandMachine().removeViewAt(i);
                } else {
                    Log.e("TurnoMaquina", "Error: Vista nula al intentar remover.");
                }

            } else {
                Log.d("TurnoMaquina", "La carta " + carta.toString() + " no es válida");
                i++;  // Incrementa el índice solo si no se ha jugado una carta válida
            }
        }

        if (!jugadaValida) {
            Log.d("TurnoMaquina", "La máquina no encontró una jugada válida y tomará una carta");
            mainActivity.updateCard(maquina, mainActivity.handMachine);
            mainActivity.mostrarMensaje("La máquina ha tomado una carta");
        }

        if (maquina.getMano().getCartas().size() == 1) {
            Log.d("TurnoMaquina", "La máquina tiene una sola carta, dice UNO");
            mainActivity.mostrarMensaje("¡Máquina dice uno!");
        }
        Log.d("TurnoMaquina", "Fin del turno de la máquina");
        if (!cartaEnJuego.esEspecial())
            baraja.devolverCarta(cartaEnJuego);
        verificarFinDelJuego();
        cambiarTurno();
    }

    private void verificarFinDelJuego() {
        if (jugador.notieneCartas()) {
            setJuegoTerminado(true);
            mainActivity.mostrarDialogoFinDelJuego("¡Felicitaciones, has ganado!", "Jugar de nuevo", "Salir");
        } else if (maquina.notieneCartas()) {
            setJuegoTerminado(true);
            mainActivity.mostrarDialogoFinDelJuego("Has perdido, inténtalo de nuevo.", "Jugar de nuevo", "Salir");
        }
    }
}