package ec.edu.espol.juegouno;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    JuegoUNO juegoUno;
    Jugador jugador;
    Jugador maquina;
    LinearLayout handPlayer;
    LinearLayout handMachine;
    ImageView cardCentral;
    Button pasar;
    Button uno;
    Baraja baraja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Enlazar variables con la clase juegouno
        juegoUno = new JuegoUNO(this);
        juegoUno.iniciarJuego();
        //juegoUno.jugar();
        jugador = juegoUno.jugador;
        maquina = juegoUno.maquina;
        baraja = juegoUno.baraja;
        Carta cartaJuego = juegoUno.cartaEnJuego;
        // Inicializar la UI
        handPlayer = (LinearLayout) findViewById(R.id.hand_player);
        handMachine = (LinearLayout) findViewById(R.id.hand_machine);
        cardCentral = findViewById(R.id.cardcentral);
        pasar = findViewById(R.id.pasar);
        uno = findViewById(R.id.uno);
        //Uso de los metodos
        mostrarCartas(jugador.getMano().getCartas(), handPlayer);
        mostrarCartas(maquina.getMano().getCartas(), handMachine);
        int resourceId = getResources().getIdentifier(cartaJuego.toString(), "drawable", getPackageName());
        cardCentral.setImageResource(resourceId);

        //actualizarCartaCentral(cartaJuego);
        configurarEventosJugador();
    }
    private final Map<ImageView, Carta> cartaImageViewMap = new HashMap<>();
    public LinearLayout getHandMachine() {
        return handMachine;
    }
    public LinearLayout getHandPlayer() {
        return handPlayer;
    }

    void mostrarCartas(List<Carta> cartas, LinearLayout layout) {
        layout.removeAllViews();  // Limpiar el layout antes de agregar nuevas vistas
        for (Carta carta : cartas) {
            ImageView cartaView = new ImageView(this);

            // Genera el nombre del recurso usando color y tipo en formato compatible
            String resourceName = carta.toString();
            int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());

            // Línea de depuración para verificar el nombre del recurso y el ID
            Log.d("ResourceID", "Nombre del recurso: " + resourceName + ", ID: " + resourceId);

            // Verifica si el resourceId es 0, lo que indica que la imagen no se encontró
            if (resourceId == 0) {
                Log.e("Error", "Imagen no encontrada para: " + resourceName);
            } else {
                cartaView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                cartaView.setImageResource(resourceId);
                cartaView.getLayoutParams().width = 100;
                cartaView.setPadding(8,0,8,0);
                cartaView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                // Mapea la ImageView a la carta correspondiente
                cartaImageViewMap.put(cartaView, carta);
                layout.addView(cartaView);

            }
        }
    }
    private void configurarEventosJugador() {
        pasar.setOnClickListener(view -> {
            updateCard(jugador, handPlayer);
            juegoUno.cambiarTurno(); // Solo cambia el turno, no debe procesar nada más
        });

        for (int i = 0; i < handPlayer.getChildCount(); i++) {
            View cartaView = handPlayer.getChildAt(i);
            cartaView.setOnClickListener(view -> {
                ImageView imgView = (ImageView) view;
                Carta cartaSeleccionada = obtenerCartaDeImageView(imgView);

                if (juegoUno.validarJugada(cartaSeleccionada)) {
                    int index = handPlayer.indexOfChild(view);
                    juegoUno.jugador.jugarCarta(index);
                    handPlayer.removeView(view);
                    actualizarCartaCentral(cartaSeleccionada);
                    juegoUno.turnoJugador(cartaSeleccionada);
                } else {
                    mostrarMensaje("Jugada no válida");
                }
                //juegoUno.cambiarTurno(); // Cambia el turno después de completar la jugada
            });
        }
    }


    private Carta obtenerCartaDeImageView(ImageView imgView) {
        // Recupera la carta desde el HashMap
        return cartaImageViewMap.get(imgView);
    }

    void actualizarCartaCentral(Carta carta) {
        String resourceName = carta.toString();
        int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
        cardCentral.setImageResource(resourceId);
    }

    void updateCard (Jugador player, LinearLayout hand){
        Carta card = player.tomarCarta(baraja);
        if (card == null) {
            Log.e("UpdateCard", "La carta tomada es nula. Probablemente la baraja está vacía.");
            return;
        }
        mostrarMensaje(player.getNombre() +" ha tomado una carta");
        ImageView img = new ImageView(this);
        String resourceName = card.toString();
        int resourceId = getResources().getIdentifier(resourceName, "drawable", getPackageName());
        img.setImageResource(resourceId);
        img.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        img.getLayoutParams().width = 100;
        img.setPadding(8,0,8,0);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        hand.addView(img);
        cartaImageViewMap.put(img, card);
        // Solo añadir eventos de clic si la carta pertenece al jugador
        if (player == jugador) {
            img.setOnClickListener(view -> {
                Carta cartaSeleccionada = obtenerCartaDeImageView(img);
                if (cartaSeleccionada != null) {
                    if (juegoUno.validarJugada(cartaSeleccionada)) {
                        actualizarCartaCentral(cartaSeleccionada);
                        juegoUno.turnoJugador(cartaSeleccionada);
                        int index = handPlayer.indexOfChild(view);
                        juegoUno.jugador.jugarCarta(index);
                        hand.removeView(view);
                        //juegoUno.cambiarTurno();
                    } else {
                        mostrarMensaje("Jugada no válida");
                    }
                } else {
                    Log.e("UpdatePlayerCard", "La carta seleccionada es nula.");
                    mostrarMensaje("Error: Carta no válida.");
                }
            });
        }
    }
    public void mostrarMensaje(String msj){
        Toast.makeText(this, msj,Toast.LENGTH_SHORT).show();
    }

    public void mostrarDialogoCambioDeColor() {
        String[] colores = {"Rojo", "Amarillo", "Verde", "Azul"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona un color");

        builder.setItems(colores, (dialog, which) -> {
            Color colorSeleccionado = null;

            switch (which) {
                case 0 -> colorSeleccionado = Color.ROJO; // Rojo

                case 1 -> colorSeleccionado = Color.AMARILLO; // Amarillo

                case 2 -> colorSeleccionado = Color.VERDE; // Verde

                case 3 -> colorSeleccionado = Color.AZUL; // Azul
            }

            // Actualiza el color en juego en JuegoUNO
            juegoUno.actualizarColorEnJuego(colorSeleccionado);
            //actualizarCartaCentral(juegoUno.getCartaEnJuego());

            juegoUno.setEsperandoCambioColor(false);  // Desactivar la espera de color
            // Si es el turno de la máquina, reanudar después de que el jugador haya seleccionado el color
            if (!juegoUno.turno) {
                juegoUno.turnoMaquina();
            } else {
                juegoUno.cambiarTurno(); // Solo cambiar turno si el jugador hizo el cambio de color
            }
        });

        builder.show();
    }
    public void clickUno(View view){
        if(juegoUno.turno && jugador.getMano().getCartas().size()==1) {
            juegoUno.dijoUno = true;
            mostrarMensaje("¡UNO!");
        }
        else {
            mostrarMensaje("No puedes decir UNO en este momento.");
        }

    }public void mostrarDialogoFinDelJuego(String mensaje, String textoBotonPositivo, String textoBotonNegativo) {
        new AlertDialog.Builder(this)
                .setTitle("Fin del Juego")
                .setMessage(mensaje)
                .setPositiveButton(textoBotonPositivo, (dialog, which) -> recreate())
                .setNegativeButton(textoBotonNegativo, (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

}