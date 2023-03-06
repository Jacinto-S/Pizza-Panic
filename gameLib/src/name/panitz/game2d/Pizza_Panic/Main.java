package name.panitz.game2d.Pizza_Panic;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import name.panitz.game2d.*;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.awt.event.*;
import java.util.Objects;

import static java.awt.event.KeyEvent.*;

// TODO: Items
// TODO: Multiplayer (lokal)
// TODO: Rest High Score - Einstellung
// TODO: High Scores für jeden Schwierigkeitsgrad einzeln
// TODO: Hauptmenü aus dem Game Over Bildschirm
// TODO: Animationen für die Kunden und das Kotzen
// TODO: Krasses Erklärungsmenü im Hauptmenü

final class Main implements Game {
    private final ImageObject player;
    private final List<List<? extends GameObj>> goss;
    private final int width;
    private final int height;
    public final int[] lieferungen;
    private final List<GameObj> hintergrund;
    private final List<GameObj> gegner;
    private final List<GameObj> wolken;
    private final List<GameObj> texte;
    private final List<GameObj> ziel;

    private final List<GameObj> ende;
    private int timer;
    private final List<GameObj> eingang;
    int spawnrate = 2400;
    boolean lose = false;
    public boolean pause = false;
    double gang = 0;
    double schwenkung = 0;
    int zeitspeicher = 0;
    boolean startbildschirm = true;
    int menüwahl = 0;
    double schwierigkeit = 0;
    int pausenmenü = 0;
    boolean steuerung;
    int zwischenspeicher1 = 0;
    boolean menüunten = false;
    int menüwahl2 = 0;
    boolean erklärung = false;
    static int highscore = loadHighscore();
    static int blinken = 0;


    Main(ImageObject player, List<List<? extends GameObj>> goss
            , int width, int height, int[] lieferungen
            , List<GameObj> hintergrund, List<GameObj> gegner
            , List<GameObj> wolken, List<GameObj> texte
            , List<GameObj> ziel, int timer, List<GameObj> ende
            , List<GameObj> eingang) {
        this.player = player;
        this.goss = goss;
        this.width = width;
        this.height = height;
        this.lieferungen = lieferungen;
        this.hintergrund = hintergrund;
        this.gegner = gegner;
        this.wolken = wolken;
        this.texte = texte;
        this.ziel = ziel;
        this.timer = timer;
        this.ende = ende;
        this.eingang = eingang;
    }


    Main() {
        this(new ImageObject(new Vertex(200, 200), new Vertex(1, 1), "fahrradkurier-schnell.gif")
                , new ArrayList<>(), 1920, 768, new int[]{0}
                , new ArrayList<>(), new ArrayList<>()
                , new ArrayList<>(), new ArrayList<>()
                , new ArrayList<>(), (int) (120 * SwingScreen.umwandlung)
                , new ArrayList<>(), new ArrayList<>());
    }


    public void init() {
        goss().clear();
        goss().add(hintergrund());
        goss().add(gegner());
        goss().add(wolken());
        goss().add(texte());
        goss().add(ziel());
        goss().add(ende);
        goss().add(eingang);
        hintergrund().clear();
        gegner().clear();
        wolken().clear();
        texte().clear();
        texte().add(new TextObject(new Vertex(10, 30), "Lieferungen: " + 0));
        texte().add(new TextObject(new Vertex(10, 60), "Timer: " + 0));
        ziel().clear();
        this.timer = (int) (121 * SwingScreen.umwandlung);
        this.lieferungen[0] = 0;
        player().velocity().x = 0;
        player().velocity().y = 0;
        player().pos().x = 0;
        player().pos().y = this.height() / 2D - player().height() / 2D;


        hintergrund().add(new ImageObject("straße.png"));


        //TODO: Charaktere mit Items einbauen
        /*wolken().add(new ImageObject(
                new Vertex(800, 10), new Vertex(-1, 0), "wolke.png"));*/

        gegner().add(new ImageObject(
                new Vertex(width() - 134 - 35, height() / 2D - 60), new Vertex(-1.5, 0), "kunde.gif"));

        ziel().add(new ImageObject(
                new Vertex(this.width() - 134, this.height() / 2D - 236 - 30), new Vertex(0, 0), "restaurant.png"));

        eingang().add(new ImageObject(
                new Vertex(0, this.height() / 2D - 30), new Vertex(0, 0), "startbereich.png"));

        ((TextObject) texte().get(0)).text = "Lieferungen: 0";
    }


    public void doChecks() {

        /*for (var w : wolken()) {
            if (w.isLeftOf(0)) {
                w.pos().x = width();

            }
        }*/


        for (var z : gegner()) {

            //Kunden sollen zurückgesetzt werden, wenn sie am Ende ankommen und dabei den Spieler nicht an seinem
            //Startpunkt treffen können
            if (z.isLeftOf(0) || z.touches(eingang().get(0))) {
                z.pos().x = width() - ziel().get(0).width() - gegner().get(0).width();
                z.pos().y = height() / 2D - 60;
            }


            //Der Spieler soll verlieren, falls er einen Kunden berührt
            if (player.touches(z)) {
                z.pos().moveTo(new Vertex(width() + 10, z.pos().y));
                lose = true;
                timer = 0;
            }


            //Die Kunden sollen beim Taumeln nicht aus dem Spielfeld taumeln können
            if (z.isAbove(42) && z.velocity().y <= -1) z.velocity().y = 0;
            if (z.isAbove(42)) z.pos().y = 0;

            if (z.isUnderneath(this.height - z.height()) && z.velocity().y >= 1)
                z.velocity().y = 0;
            if (z.isUnderneath(this.height - z.height()))
                z.pos().y = this.height - z.height();

            //Die Kunden sollen nicht gegen den Spieler laufen, solange sich dieser im Eingangsbereich befindet
            //if (z.touches(eingang().get(0)) && z.pos().x <= eingang().get(0).width()) {z.velocity().x = 0; System.out.println("Vorne");}

        }

        //Es soll alle x Sekunden ein Gegner generiert werden.
        if (timer % (spawnrate / schwierigkeit) == 0 && timer > 0 && !pause) {
            gegner().add(new ImageObject(new Vertex(width() - 134 - 35, height() / 2D - 60), new Vertex(-1.5, 0), "kunde.gif"));
            System.out.println("Kunden: " + gegner().size());
        }

        //Die Kunden haben eine Lebensmittelvergiftung und sollen durch die Gegend taumeln.
        for (var z : gegner()) {
            if (timer % 20 == 0 && !z.touches(eingang().get(0)) && !pause) {
                z.velocity().y = ((Math.random() > 0.5 ? 1 : -1)) * (int) ((Math.random() * 10) / 3);
            }
        }

        //Der Spieler soll daran gehindert werden, das Spielfeld zu verlassen.
        //1. Test: Wenn er am Rand angekommen ist, wird seine Geschwindigkeit in die Richtung 0 gesetzt, damit er da nicht mehr hin kann
        //2. Test: Falls er versucht, weiter nach oben zu drücken, wird seine Position zurückgesetzt
        if (player().isAbove(42) && player().velocity().y <= -1) player().velocity().y = 0;
        if (player().isAbove(42)) player().pos().y = 0;

        if (player().isUnderneath(this.height - player().height()) && player().velocity().y >= 1)
            player().velocity().y = 0;
        if (player().isUnderneath(this.height - player().height()))
            player().pos().y = this.height - player.height();

        if (player().isLeftOf(90) && player().velocity().x <= -1) player().velocity().x = 0;
        if (player().isLeftOf(90)) player().pos().x = 0;

        if (player().isRightOf(this.width - player().width()) && player().velocity().x >= 1)
            player().velocity().x = 0;
        if (player().isRightOf(this.width - player().width())) player().pos().x = this.width - player.width();

        // Timer
        ((TextObject) texte().get(1)).text = "Zeit: " + (int) (timer / SwingScreen.umwandlung);
        if (timer > 0 && !pause) {
            timer--;
        }

        //Aktionen beim Berühren vom Ziel: Zurückgesetzt werden
        if (player().touches(ziel().get(0))) {
            player().pos().x = 0;
            player().pos().y = this.height() / 2D - player().height() / 2D;
            player().velocity().x = 0;
            player().velocity().y = 0;
            lieferungen[0]++;
            ((TextObject) texte().get(0)).text = "Lieferungen: " + lieferungen[0];
        }

        //Je nach Gang soll das Bild bzw. die Animation des Spielers anders aussehen:
        if (player().velocity().x == 0) player().setImage("fahrradkurier.png");
        if (player().velocity().x == 3) player().setImage("fahrradkurier.gif");
        if (player().velocity().x == 6) player().setImage("fahrradkurier-schnell.gif");

        //Wenn man steht, sollte man nicht weiter nach oben oder unten fahren. Daher muss man dann angehalten werden.
        if (player().velocity().x == 0 && player().velocity().y != 0) player().velocity().y = 0;
        //Außerdem soll man zurückgefahren werden, wenn man an der Que Mara vorbei fährt.
        if (player().pos().x == width - player().width() && player().pos().x != width - player().width() - ziel().get(0).width()) {
            player().velocity().x = -1;
            player().velocity().y = 0;
        }
        if (player().pos().x == width - player().width() - ziel().get(0).width()) player().velocity().x = 0;

        if(timer == 0) blinken++;
        else if(blinken > 0) blinken = 0;
    }

    //High Score speichern in Dokument
    public void saveScore() {
        try {
            FileWriter writer = new FileWriter("highscore.txt");
            writer.write(Integer.toString(lieferungen[0]));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //High Score abrufen
    public static int loadHighscore() {
        try {
            java.io.File file = new java.io.File("highscore.txt");
            java.util.Scanner scanner = new java.util.Scanner(file);
            int highscore = scanner.nextInt();
            scanner.close();
            return highscore;
        } catch (FileNotFoundException e) {
            return 0;
        }
    }

    //Menüs
    @Override
    public void paintTo(Graphics g) {
        Font h1 = g.getFont().deriveFont(48.0f);
        Font groß = g.getFont().deriveFont(24.0f);
        Font mittel = g.getFont().deriveFont(18.0f);
        Font klein = g.getFont().deriveFont(12.0f);
        //Main Menu
        if (startbildschirm) {
            //Hintergrund
            g.setFont(h1);
            g.setColor(Color.black);
            g.fillRect(0, 0, width, height);
            g.setColor(new Color(255, 163, 49));
            g.fillRoundRect(width / 2 - 250, 50, 500, 100, 100, 100);
            g.setColor(new Color(255, 0, 0));
            g.drawString("PIZZA PANIC", width / 2 - 140, 115);

            //Menüoptionen Schwierigkeit
            g.setFont(groß);
            //einfach
            g.setColor(Color.darkGray);
            g.fillRoundRect(width / 2 - 500, height / 2 - 150, 200, 60, 50, 50);
            g.setColor(Color.white);
            g.drawString("easy", width / 2 - 430, height / 2 - 113);
            //mittel
            g.setColor(Color.darkGray);
            g.fillRoundRect(width / 2 - 100, height / 2 - 150, 200, 60, 50, 50);
            g.setColor(Color.white);
            g.drawString("medium", width / 2 - 45, height / 2 - 113);
            //schwer
            g.setColor(Color.darkGray);
            g.fillRoundRect(width / 2 + 500 - 200, height / 2 - 150, 200, 60, 50, 50);
            g.setColor(Color.white);
            g.drawString("hard", width / 2 + 376, height / 2 - 113);
            //Menüoptionen Spielermodus
            //Singleplayer
            g.setColor(Color.darkGray);
            g.fillRoundRect(width / 2 - 300, height / 2 + 25, 200, 60, 50, 50);
            g.setColor(Color.white);
            g.drawString("singleplayer", width / 2 - 264, height / 2 + 62);
            //Multiplayer
            g.setColor(Color.darkGray);
            g.fillRoundRect(width / 2 + 100, height / 2 + 25, 200, 60, 50, 50);
            g.setColor(Color.white);
            g.drawString("multiplayer", width / 2 + 143, height / 2 + 62);
            //Menüoptionen Erklärungen
            //Steuerung
            g.setColor(Color.darkGray);
            g.fillRoundRect(width / 2 - 300 /*-100*/, height / 2 + 200, 200, 60, 50, 50);
            g.setColor(Color.white);
            g.drawString("controls", width / 2 - 247 /*-100*/, height / 2 + 237);
            //Spielbeschreibung
            g.setColor(Color.darkGray);
            g.fillRoundRect(width / 2 + 100 /*+100*/, height / 2 + 200, 200, 60, 50, 50);
            g.setColor(Color.white);
            g.drawString("about", width / 2 + 167 /*+100*/, height / 2 + 237);
            //Auswahl der Stufen
            g.setColor(Color.red);
            //einfach
            if (!menüunten) {
                if (menüwahl == 0) {
                    g.drawRoundRect(width / 2 - 500, height / 2 - 150, 200, 60, 50, 50);
                }
                //medium
                if (menüwahl == 1) {
                    g.drawRoundRect(width / 2 - 100, height / 2 - 150, 200, 60, 50, 50);
                }
                //schwer
                if (menüwahl == 2) {
                    g.drawRoundRect(width / 2 + 500 - 200, height / 2 - 150, 200, 60, 50, 50);
                }
            }
            //Erklärungen
            if (menüunten) {
                if (menüwahl2 == 0) {
                    g.setColor(Color.red);
                    g.drawRoundRect(width / 2 - 300, height / 2 + 200, 200, 60, 50, 50);
                }
                if (menüwahl2 == 1) {
                    g.setColor(Color.red);
                    g.drawRoundRect(width / 2 + 100, height / 2 + 200, 200, 60, 50, 50);
                }
            }
            //Schriftgröße zurücksetzen
            g.setFont(klein);
        }
        if (!startbildschirm) {
            for (var gos : goss()) gos.forEach(go -> go.paintTo(g));
            player().paintTo(g);
            //Game Over Bildschirm
            if (timer == 0) {
                g.setColor(Color.black);
                g.fillRect(0, 0, width, height);
                g.setColor(Color.white);
                g.drawString("Game Over", width / 2 - 35, height / 2);
                //Game over bei Niederlage
                if (lose) g.drawString("You lose!", width / 2 - 25, height / 2 + 35);
                else {
                    //New Highscore Animation
                    if (lieferungen[0] > highscore) {
                        g.setColor(Color.ORANGE);
                        g.fillRoundRect(width/2 - 300, 200, 600, 100, 50, 50);
                        g.setColor(Color.RED);
                        g.setFont(h1);
                        g.drawString("New Highscore!", width / 2 - 150, 270);
                        if(blinken % 40 >= 20) {
                            g.setColor(Color.ORANGE);
                            g.fillRoundRect(width / 2 - 300, 200, 600, 100, 50, 50);
                        }
                    }
                    g.setFont(mittel);
                    g.setColor(Color.white);
                    g.drawString("Score: " + lieferungen[0] + " | Highscore: " + loadHighscore(),
                            //Der String soll so verschoben werden, dass er mittig Zentriert bleibt, wenn der Score steigt.
                            width / 2 - 80 - (int) (5.5 * ((String.valueOf(lieferungen[0]).length() - 1) + String.valueOf(loadHighscore()).length())),
                            height / 2 + 35);
                }
                g.drawString("To restart, press Space.", width / 2 - 89, height / 2 + 70);
            }
            //Pausebildschirm
            if (pause) {
                g.setColor(new Color(0.41176470588f, 0.41176470588f, 0.41176470588f, 0.6f));
                g.fillRect(0, 0, width, height);
                g.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
                g.fillRoundRect(width / 2 - 300, height / 2 - 50, 600, 100, 50, 50);
                g.setColor(Color.white);
                g.drawString("- PAUSED -", width / 2 - 50, height / 2 + 9);
                //Menü
                //Steuerung
                g.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
                g.fillRoundRect(width / 2 - 300 + 9, height / 2 + 60, 180, 30, 25, 25);
                g.setColor(Color.white);
                g.drawString("Controls", width / 2 - 244, height / 2 + 82);
                //Neustart
                g.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
                g.fillRoundRect(width / 2 - 100 + 9, height / 2 + 60, 180, 30, 25, 25);
                g.setColor(Color.white);
                g.drawString("Restart", width / 2 - 38, height / 2 + 82);
                //Hauptmenü
                g.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
                g.fillRoundRect(width / 2 + 100 + 9, height / 2 + 60, 180, 30, 25, 25);
                g.setColor(Color.white);
                g.drawString("Menu", width / 2 + 173, height / 2 + 82);
                //Menüauswahl
                //Schwierigkeiten
                g.setColor(Color.red);
                if (pausenmenü == 0) {
                    g.drawRoundRect(width / 2 - 300, height / 2 - 50, 600, 100, 50, 50);
                }
                if (pausenmenü == 1) {
                    g.drawRoundRect(width / 2 - 300 + 9, height / 2 + 60, 180, 30, 25, 25);
                }
                if (pausenmenü == 2) {
                    g.drawRoundRect(width / 2 - 100 + 9, height / 2 + 60, 180, 30, 25, 25);
                }
                if (pausenmenü == 3) {
                    g.drawRoundRect(width / 2 + 100 + 9, height / 2 + 60, 180, 30, 25, 25);
                }
            }
        }
        //Hier wird die Steuerung erklärt, wenn man man die Menüoption angewählt hat
        if (steuerung) {
            g.setColor(new Color(0f, 0f, 0f, 0.8f));
            g.fillRect(0, 0, width, height);
            g.setColor(Color.white);
            g.fillRoundRect(width / 2 - 300, height / 2 - 250, 600, 400, 50, 50);
            g.setColor(Color.red);
            g.setFont(groß);
            g.drawString("Steuerung", width / 2 - 59, 195);
            g.setColor(Color.black);
            g.setFont(mittel);
            g.drawString("Menüführung -> ", width / 2 - 250, 240);
            g.drawString("Steuerung mit den Pfeiltasten", width / 2 - 250 + 210, 240);
            g.drawString("Auswählen mit Enter", width / 2 - 250 + 210, 240 + 25);
            g.drawString("Verlassen mit Escape", width / 2 - 250 + 210, 240 + 25 * 2);
            g.drawString("Pausieren -> ", width / 2 - 250, 240 + 25 * 4);
            g.drawString("Währen dem Spiel mit Escape", width / 2 - 250 + 210, 240 + 25 * 4);
            g.drawString("Menüführung bleibt gleich", width / 2 - 250 + 210, 240 + 25 * 5);
            g.drawString("Spielsteuerung -> ", width / 2 - 250, 240 + 25 * 7);
            g.drawString("Steuerung mit den Pfeiltasten", width / 2 - 250 + 210, 240 + 25 * 7);
            g.drawString("Rechts/Links: Gänge wechseln", width / 2 - 250 + 210, 240 + 25 * 8);
            g.drawString("Bim fahren Oben/Unten: Lenken", width / 2 - 250 + 210, 240 + 25 * 9);
        }
        //Hier wird erklärt, worum es bei dem Spiel geht.
        if (erklärung) {
            g.setColor(new Color(0f, 0f, 0f, 0.8f));
            g.fillRect(0, 0, width, height);
            g.setColor(Color.white);
            g.fillRoundRect(width / 2 - 600, height / 2 - 250, 1200, 400, 50, 50);
            g.setColor(Color.red);
            g.setFont(groß);
            g.drawString("Pizza Panic", width / 2 - 59, 195);
            g.setColor(Color.black);
            g.setFont(mittel);
            g.drawString("Willkommen in der erschreckenden Welt von Que Mara, dem schlechtesten Restaurant der Stadt!", 525, 240);
            g.drawString("Hier werden die Kunden regelmäßig mit furchtbar schlechtem Essen vergiftet. Aber es gibt eine Rettung:", 525, 240 + 25);
            g.drawString("Die Pizzas des Lieferdienstes! Du bist der Held dieses Spiels und musst die Pizzen durch die Straßen", 525, 240 + 25 * 2);
            g.drawString("an den Kunden vorbei navigieren, die das Restaurant verlassen.", 525, 240 + 25 * 3);
            g.drawString("Aber Vorsicht! Alle Kunden haben eine Lebensmittelvergiftung. Wenn dich einer von ihnen ankotzt,", 525, 240 + 25 * 4);
            g.drawString("ist das Spiel vorbei. Dein Ziel ist es, möglichst viele Pizzen zu liefern, bevor die Zeit abläuft.", 525, 240 + 25 * 5);
            g.drawString("Bist du bereit für diese spannende Herausforderung? Starte das Spiel, um es herauszufinden!", 525, 240 + 25 * 6);
            g.drawString("Disclaimer: Die Handlung und alle handelnden Personen und Organisationen sind frei erfunden.", 525, 240 + 25 * 8);
            g.drawString("Jegliche Ähnlichkeit mit realen Personen/Organisationen sind rein zufällig und nicht beabsichtigt.", 525, 240 + 25 * 9);
        }
    }

    //Anhalten Spieler
    @Override
    public void move() {
        if (timer == 0) return;
        for (var gos : goss()) gos.forEach(go -> go.move());
        player().move();
    }

    public void keyPressedReaction(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case VK_O -> timer = 1;
            case VK_P -> saveScore();
            case VK_RIGHT -> {
                if (startbildschirm && menüunten && menüwahl2 == 0) menüwahl2 = 1;
                if (pause && pausenmenü != 0 && pausenmenü < 3) pausenmenü++;
                if (startbildschirm && menüwahl < 2 && !menüunten) menüwahl++;
                if (player().velocity().x < 4 && !pause && !startbildschirm) player().velocity().add(new Vertex(3, 0));
            }
            case VK_LEFT -> {
                if (pause && pausenmenü > 0) {
                    pausenmenü--;
                }
                if (startbildschirm && menüunten && menüwahl2 != 0) menüwahl2--;
                if (startbildschirm && menüwahl > 0 && !menüunten) menüwahl--;
                if (player().velocity().x > 0 && !pause && !startbildschirm) player().velocity().add(new Vertex(-3, 0));
            }
            case VK_DOWN -> {
                if (startbildschirm && !menüunten){
                    if(menüwahl == 0) menüwahl2 = 0;
                    else if(menüwahl == 2) menüwahl2 = 1;
                    menüunten = true;
                }
                if (pause && pausenmenü == 0) {
                    pausenmenü = (zwischenspeicher1 == 0 ? 1 : zwischenspeicher1);
                }
                if (player().velocity().y < 4 && player().velocity().x != 0
                        && (!(player.velocity().x == -1)) && !pause) {
                    player().velocity().add(new Vertex(0, 1));
                }
            }
            case VK_UP -> {
                if (startbildschirm && menüunten) menüunten = false;
                if (pause && pausenmenü != 0) {
                    zwischenspeicher1 = pausenmenü;
                    pausenmenü = 0;
                }
                if (player().velocity().y > -4 && player().velocity().x != 0
                        && (!(player.velocity().x == -1)) && !pause) {
                    player().velocity().add(new Vertex(0, -1));
                }
            }
            /*case VK_Q -> {
                gegner().get(0).pos().x = width() - ziel().get(0).width() - gegner().get(0).width();
                gegner().get(0).pos().y = height()/2D - 60;
            }*/
            case VK_SPACE -> {
                if (timer == 0) {
                    if (loadHighscore() < lieferungen[0]) saveScore();
                    init();
                }
                lose = false;
            }
            case VK_ENTER -> {
                if (startbildschirm && !steuerung) {
                    if (!menüunten) {
                        switch (menüwahl) {
                            case 0 -> {
                                schwierigkeit = 0.5;
                                startbildschirm = false;
                            }
                            case 1 -> {
                                schwierigkeit = 1;
                                startbildschirm = false;
                            }
                            case 2 -> {
                                schwierigkeit = 2;
                                startbildschirm = false;
                            }
                        }
                        init();
                    } else {
                        switch (menüwahl2) {
                            case 0 -> steuerung = true;
                            case 1 -> erklärung = true;
                        }
                    }
                }
                if (pause && !steuerung) {
                    switch (pausenmenü) {
                        case 0 -> {
                            for (var z : gegner()) {
                                z.velocity().x = -1.5;
                            }
                            player().velocity().x = gang;
                            player().velocity().y = schwenkung;
                            timer = zeitspeicher;
                            pause = false;
                        }
                        case 1 -> {
                            steuerung = true;
                        }
                        case 2 -> {
                            pause = false;
                            init();
                        }
                        case 3 -> {
                            startbildschirm = true;
                            pause = false;
                        }
                    }
                    pausenmenü = 0;
                }
            }
            case VK_ESCAPE -> {
                if (!pause && !lose && !startbildschirm) {
                    pausenmenü = 0;
                    gang = player().velocity().x;
                    schwenkung = player().velocity().y;
                    player().velocity().x = 0;
                    player().velocity().y = 0;
                    for (var z : gegner()) {
                        z.velocity().x = 0;
                        z.velocity().y = 0;
                    }
                    zeitspeicher = timer;
                    pause = true;
                } else if (!lose && !startbildschirm && !steuerung) {
                    for (var z : gegner()) {
                        z.velocity().x = -1.5;
                    }
                    player().velocity().x = gang;
                    player().velocity().y = schwenkung;
                    timer = zeitspeicher;
                    zwischenspeicher1 = 0;
                    pause = false;
                } else if (steuerung) steuerung = false;
                else if (erklärung) erklärung = false;
            }
        }
    }

    public void keyReleasedReaction(KeyEvent keyEvent) {
        /*switch (keyEvent.getKeyCode()) {
            case VK_DOWN -> player().velocity().add(new Vertex(0, -player.velocity().y));
            case VK_UP -> player().velocity().add(new Vertex(0, player.velocity().y));
        }*/
    }

    @Override
    public boolean won() {
        return false;
    }

    @Override
    public boolean lost() {
        return false;
    }


    public static void main(String... args) {
        new Main().play();
    }

    @Override
    public ImageObject player() {
        return player;
    }

    @Override
    public List<List<? extends GameObj>> goss() {
        return goss;
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    public int[] schaden() {
        return lieferungen;
    }

    public List<GameObj> hintergrund() {
        return hintergrund;
    }

    public List<GameObj> gegner() {
        return gegner;
    }

    public List<GameObj> wolken() {
        return wolken;
    }

    public List<GameObj> texte() {
        return texte;
    }

    public List<GameObj> ziel() {
        return ziel;
    }

    public List<GameObj> eingang() {
        return eingang;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Main) obj;
        return Objects.equals(this.player, that.player) &&
                Objects.equals(this.goss, that.goss) &&
                this.width == that.width &&
                this.height == that.height &&
                Objects.equals(this.lieferungen, that.lieferungen) &&
                Objects.equals(this.hintergrund, that.hintergrund) &&
                Objects.equals(this.gegner, that.gegner) &&
                Objects.equals(this.wolken, that.wolken) &&
                Objects.equals(this.texte, that.texte) &&
                Objects.equals(this.ziel, that.ziel) &&
                this.timer == that.timer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, goss, width, height, lieferungen, hintergrund, gegner, wolken, texte, ziel, timer);
    }

    @Override
    public String toString() {
        return "Main[" +
                "player=" + player + ", " +
                "goss=" + goss + ", " +
                "width=" + width + ", " +
                "height=" + height + ", " +
                "schaden=" + lieferungen + ", " +
                "hintergrund=" + hintergrund + ", " +
                "gegner=" + gegner + ", " +
                "wolken=" + wolken + ", " +
                "texte=" + texte + ", " +
                "ziel=" + ziel + ", " +
                "timer=" + timer + ']';
    }

}

