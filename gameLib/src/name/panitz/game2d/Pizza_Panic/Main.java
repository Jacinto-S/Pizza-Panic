package name.panitz.game2d.Pizza_Panic;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import name.panitz.game2d.*;

import java.util.ArrayList;
import java.awt.event.*;
import java.util.Objects;

import static java.awt.event.KeyEvent.*;

//Grundlegendes
// TODO: Multiplayer (lokal)
//      -> TODO: Zwei Lieferungen Zähler
//      -> TODO: Sieg für Spieler mit mehr Punkten
//      -> TODO: Wenn einer stirbt, Sieg für den anderen Spieler
// TODO: Hauptmenü aus dem Game Over Bildschirm
// TODO: Items
//      -> TODO: Kunden mit Items spawnen
//      -> TODO: Mountain Dew Feature Implementieren: Dritter Gang und freischalten
//      -> TODO: Medipack Feauture Implementieren: Medipack geben und Kunden heilen.
//TODO: New Highscore reparieren
//Extra Features
// TODO: Reset High Score - Einstellung
// TODO: High Scores für jeden Schwierigkeitsgrad einzeln
// TODO: Animationen für die Kunden und das Kotzen
// TODO: Krasses Erklärungsmenü im Hauptmenü

final class Main implements Game {
    List<ImageObject> player;
    List<List<? extends GameObj>> goss;
    int width;
    int height;
    int[] lieferungen;
    List<GameObj> hintergrund;
    List<ImageObject> kunde;
    List<GameObj> wolken;
    List<GameObj> texte;
    List<GameObj> ziel;

    List<GameObj> ende;
    private int timer;
    List<GameObj> eingang;
    int spawnrate = 2400;
    boolean lose = false;
    public boolean pause = false;
    double gang = 0;
    double gang1 = 0;
    double schwenkung = 0;
    double schwenkung1 = 0;
    int zeitspeicher = 0;
    boolean startbildschirm = true;
    int menuewahl = 0;
    double schwierigkeit = 0;
    int pausenmenue = 0;
    boolean steuerung;
    boolean singleplayer = true;
    int zwischenspeicher1 = 0;
    int menuestufen = 0;
    int menuewahl1 = 0;
    int menuewahl2 = 0;
    boolean erklaerung = false;
    static int highscore = loadHighscore();
    static int blinken = 0;
    int score1;
    int score2;


    Main(List<ImageObject> player, List<List<? extends GameObj>> goss
            , int width, int height, int[] lieferungen
            , List<GameObj> hintergrund, List<ImageObject> gegner
            , List<GameObj> wolken, List<GameObj> texte
            , List<GameObj> ziel, int timer, List<GameObj> ende
            , List<GameObj> eingang) {
        this.player = player;
        this.goss = goss;
        this.width = width;
        this.height = height;
        this.lieferungen = lieferungen;
        this.hintergrund = hintergrund;
        this.kunde = gegner;
        this.wolken = wolken;
        this.texte = texte;
        this.ziel = ziel;
        this.timer = timer;
        this.ende = ende;
        this.eingang = eingang;
    }


    Main() {
        this(new ArrayList<>()
                , new ArrayList<>(), 1920, 768, new int[]{0}
                , new ArrayList<>(), new ArrayList<>()
                , new ArrayList<>(), new ArrayList<>()
                , new ArrayList<>(), (int) (120 * SwingScreen.umwandlung)
                , new ArrayList<>(), new ArrayList<>());
    }


    public void init() {
        goss().clear();
        goss().add(hintergrund());
        goss().add(player());
        goss().add(kunden());
        goss().add(wolken());
        goss().add(texte());
        goss().add(ziel());
        goss().add(ende);
        goss().add(eingang);
        hintergrund().clear();
        kunden().clear();
        wolken().clear();
        texte().clear();
        player().clear();
        if (singleplayer) {
            texte().add(new TextObject(new Vertex(10, 30), "Lieferungen: " + 0));
            texte().add(new TextObject(new Vertex(10, 60), "Timer: " + 0));
        } else {
            texte().add(new TextObject(new Vertex(10, 30), "Timer: " + 0));
            texte().add(new TextObject(new Vertex(width() / 2D - 120, 30), "Spieler 1   " + 0 + "   |   " + 0 + "   Spieler 2"));
            score1 = 0;
            score2 = 0;
        }
        ziel().clear();
        eingang().clear();
        this.timer = (int) (121 * SwingScreen.umwandlung);
        this.lieferungen[0] = 0;
        for (var p : player()) {
            p.velocity().x = 0;
            p.velocity().y = 0;
            p.pos().x = 0;
            p.pos().y = this.height() / 2D - p.height() / 2D;
        }


        hintergrund().add(new ImageObject("straße.png"));


        //TODO: Charaktere mit Items einbauen
        /*wolken().add(new ImageObject(
                new Vertex(800, 10), new Vertex(-1, 0), "wolke.png"));*/

        if (singleplayer) {
            player().add(new ImageObject(
                    new Vertex(0, height() / 2D - 21), new Vertex(0, 0), "fahrradkurier.png"));
            eingang().add(new ImageObject(
                    new Vertex(0, this.height() / 2D - 30), new Vertex(0, 0), "startbereich.png"));
        } else {
            player().add(new ImageObject(
                    new Vertex(0, height() / 2D - 51), new Vertex(0, 0), "fahrradkurier.png"));
            player().add(new ImageObject(
                    new Vertex(0, height() / 2D + 10), new Vertex(0, 0), "spieler1.png"));
            eingang().add(new ImageObject(
                    new Vertex(0, this.height() / 2D - 60), new Vertex(0, 0), "startbereich-groß.png"));
        }

        ziel().add(new ImageObject(
                new Vertex(this.width() - 134, this.height() / 2D - 236 - 30), new Vertex(0, 0), "restaurant.png"));

        kunden().add(new ImageObject(
                new Vertex(width() - 134 - 35, height() / 2D - 60), new Vertex(-1.5, 0), "kunde.gif"));

        if (singleplayer) ((TextObject) texte().get(0)).text = "Lieferungen: 0";
        else ((TextObject) texte().get(1)).text = "Spieler 1   " + 0 + "   |   " + 0 + "   Spieler 2";
    }


    public void doChecks() {

        /*for (var w : wolken()) {
            if (w.isLeftOf(0)) {
                w.pos().x = width();

            }
        }*/


        for (var z : kunden()) {

            //Kunden sollen zurückgesetzt werden, wenn sie am Ende ankommen und dabei den Spieler nicht an seinem
            //Startpunkt treffen können
            if (z.isLeftOf(0) || z.touches(eingang().get(0))) {
                z.pos().x = width() - ziel().get(0).width() - kunden().get(0).width();
                z.pos().y = height() / 2D - 60;
            }


            //Der Spieler soll verlieren, falls er einen Kunden berührt
            for (var p : player) {
                if (p.touches(z)) {
                    z.pos().moveTo(new Vertex(width() + 10, z.pos().y));
                    lose = true;
                    timer = 0;
                }
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
            kunden().add(new ImageObject(new Vertex(width() - 134 - 35, height() / 2D - 60), new Vertex(-1.5, 0), "kunde.gif"));
        }

        //Die Kunden haben eine Lebensmittelvergiftung und sollen durch die Gegend taumeln.
        for (var z : kunden()) {
            if (timer % 20 == 0 && !z.touches(eingang().get(0)) && !pause) {
                z.velocity().y = ((Math.random() > 0.5 ? 1 : -1)) * (int) ((Math.random() * 10) / 3);
            }

            if (pause) {
                z.setImage("pausenkunde.png");
            } else {
                z.setImage("kunde.gif");
            }
        }


        // Timer
        if (singleplayer) ((TextObject) texte().get(1)).text = "Zeit: " + (int) (timer / SwingScreen.umwandlung);
        else ((TextObject) texte().get(0)).text = "Zeit: " + (int) (timer / SwingScreen.umwandlung);
        if (timer > 0 && !pause) {
            timer--;
        }

        for (var p : player) {
            //Der Spieler soll daran gehindert werden, das Spielfeld zu verlassen.
            //1. Test: Wenn er am Rand angekommen ist, wird seine Geschwindigkeit in die Richtung 0 gesetzt, damit er da nicht mehr hin kann
            //2. Test: Falls er versucht, weiter nach oben zu drücken, wird seine Position zurückgesetzt
            if (p.isAbove(42) && p.velocity().y <= -1) p.velocity().y = 0;
            if (p.isAbove(42)) p.pos().y = 0;

            if (p.isUnderneath(this.height - p.height()) && p.velocity().y >= 1)
                p.velocity().y = 0;
            if (p.isUnderneath(this.height - p.height()))
                p.pos().y = this.height - p.height();

            if (p.isLeftOf(90) && p.velocity().x <= -1) p.velocity().x = 0;
            if (p.isLeftOf(90)) p.pos().x = 0;

            if (p.isRightOf(this.width - p.width()) && p.velocity().x >= 1)
                p.velocity().x = 0;
            if (p.isRightOf(this.width - p.width())) p.pos().x = this.width - p.width();


            //Wenn man steht, sollte man nicht weiter nach oben oder unten fahren. Daher muss man dann angehalten werden.
            if (p.velocity().x == 0 && p.velocity().y != 0) p.velocity().y = 0;
            //Außerdem soll man zurückgefahren werden, wenn man an der Que Mara vorbei fährt.
            if (p.pos().x == width - p.width() && p.pos().x != width - p.width() - ziel().get(0).width()) {
                p.velocity().x = -1;
                p.velocity().y = 0;
            }
            if (p.pos().x == width - p.width() - ziel().get(0).width()) p.velocity().x = 0;

            if (timer == 0) blinken++;
            else if (blinken > 0) blinken = 0;
        }


        //Je nach Gang soll das Bild bzw. die Animation des Spielers anders aussehen:
        if (player().get(0).velocity().x == 0) player().get(0).setImage("fahrradkurier.png");
        if (player().get(0).velocity().x == 3) player().get(0).setImage("fahrradkurier.gif");
        if (player().get(0).velocity().x == 6) player().get(0).setImage("fahrradkurier-schnell.gif");

        if (!singleplayer) {
            if (player().get(1).velocity().x == 0) player().get(1).setImage("spieler1.png");
            if (player().get(1).velocity().x == 3) player().get(1).setImage("spieler1.gif");
            if (player().get(1).velocity().x == 6) player().get(1).setImage("spieler1-schnell.gif");
        }

        //Aktionen beim Berühren vom Ziel: Zurückgesetzt werden
        if (player().get(0).touches(ziel().get(0))) {
            player().get(0).pos().x = 0;
            player().get(0).pos().y = height() / 2D - 31 - player().get(0).height() / 2D;
            player().get(0).velocity().x = 0;
            player().get(0).velocity().y = 0;
            if (singleplayer) {
                lieferungen[0]++;
                ((TextObject) texte().get(0)).text = "Lieferungen: " + lieferungen[0];
            } else {
                score1++;
                ((TextObject) texte().get(1)).text = "Spieler 1   " + score1 + "   |   " + score2 + "   Spieler 2";
            }

        }

        if (!singleplayer && player().get(1).touches(ziel().get(0))) {
            player().get(1).pos().x = 0;
            player().get(1).pos().y = height() / 2D + 30 - player().get(1).height() / 2D;
            player().get(1).velocity().x = 0;
            player().get(1).velocity().y = 0;
            score2++;
            ((TextObject) texte().get(1)).text = "Spieler 1   " + score1 + "   |   " + score2 + "   Spieler 2";
        }
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
        Font gross = g.getFont().deriveFont(24.0f);
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
            g.setFont(gross);
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
            g.fillRoundRect(width / 2 - 300, height / 2 + 200, 200, 60, 50, 50);
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
            switch (menuewahl) {
                //einfach
                case 0 -> g.drawRoundRect(width / 2 - 500, height / 2 - 150, 200, 60, 50, 50);
                //medium
                case 1 -> g.drawRoundRect(width / 2 - 100, height / 2 - 150, 200, 60, 50, 50);
                //schwer
                case 2 -> g.drawRoundRect(width / 2 + 500 - 200, height / 2 - 150, 200, 60, 50, 50);
            }
            //Spielermodus
            if (menuestufen != 0) {
                switch (menuewahl1) {
                    //singleplayer
                    case 0 -> g.drawRoundRect(width / 2 - 300, height / 2 + 25, 200, 60, 50, 50);
                    //multiplayer
                    case 1 -> g.drawRoundRect(width / 2 + 100, height / 2 + 25, 200, 60, 50, 50);
                }
            }
            //Erklärungen
            if (menuestufen == 2) {
                g.setColor(Color.ORANGE);
                switch (menuewahl2) {
                    //Steuerung
                    case 0 -> g.drawRoundRect(width / 2 - 300, height / 2 + 200, 200, 60, 50, 50);
                    //Erkärung
                    case 1 -> g.drawRoundRect(width / 2 + 100, height / 2 + 200, 200, 60, 50, 50);
                }
            }
            //Schriftgröße zurücksetzen
            g.setFont(klein);
        }
        if (!startbildschirm) {
            for (var gos : goss()) gos.forEach(go -> go.paintTo(g));
            for (var p : player) p.paintTo(g);
            //Game Over Bildschirm
            if (timer == 0) {
                g.setColor(Color.black);
                g.fillRect(0, 0, width, height);
                g.setColor(Color.white);
                if (singleplayer) {
                    g.drawString("Game Over", width / 2 - 35, height / 2);
                    //Game over bei Niederlage
                    if (lose) g.drawString("You lose!", width / 2 - 25, height / 2 + 35);
                    else {
                        //New Highscore Animation
                        if (lieferungen[0] > highscore) {
                            g.setColor(Color.ORANGE);
                            g.fillRoundRect(width / 2 - 300, 200, 600, 100, 50, 50);
                            g.setColor(Color.RED);
                            g.setFont(h1);
                            g.drawString("New Highscore!", width / 2 - 150, 270);
                            if (blinken % 40 >= 20) {
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
                } else {
                    g.setFont(h1);
                    if (score1 > score2) g.drawString("Player 1 has won!", width / 2 - 190, 370);
                    else if (score1 < score2) g.drawString("Player 2 has won!", width / 2 - 177, 370);
                    else g.drawString("It's a draw!", width / 2 - 103, 370);
                }
                g.setFont(gross);
                g.drawString("Spieler 1   " + score1, width / 2 - 171 - ((String.valueOf(score1).length()-1) * 13), height / 2 + 30);
                g.drawString("|", width / 2, height / 2 + 30);
                g.drawString(score2 + "   Spieler 2", width / 2 + 50, height / 2 + 30);
                g.setFont(mittel);
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
                if (pausenmenue == 0) {
                    g.drawRoundRect(width / 2 - 300, height / 2 - 50, 600, 100, 50, 50);
                }
                if (pausenmenue == 1) {
                    g.drawRoundRect(width / 2 - 300 + 9, height / 2 + 60, 180, 30, 25, 25);
                }
                if (pausenmenue == 2) {
                    g.drawRoundRect(width / 2 - 100 + 9, height / 2 + 60, 180, 30, 25, 25);
                }
                if (pausenmenue == 3) {
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
            g.setFont(gross);
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
        if (erklaerung) {
            g.setColor(new Color(0f, 0f, 0f, 0.8f));
            g.fillRect(0, 0, width, height);
            g.setColor(Color.white);
            g.fillRoundRect(width / 2 - 600, height / 2 - 250, 1200, 400, 50, 50);
            g.setColor(Color.red);
            g.setFont(gross);
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
        for (var gos : goss()) gos.forEach(GameObj::move);
    }

    public void keyPressedReaction(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            //Developer Keys
            case VK_O -> timer = 1;
            case VK_P -> saveScore();
            case VK_L -> kunden().add(new ImageObject(
                    new Vertex(width() - 134 - 35, height() / 2D - 60), new Vertex(-1.5, 0), "kunde.gif"));
            //Steuerung
            case VK_D -> {
                if (!singleplayer && !lose && !pause && player().get(1).velocity().x < 4)
                    player().get(1).velocity().add(new Vertex(3, 0));
            }
            case VK_A -> {
                if (!singleplayer && !lose && !pause && player().get(1).velocity().x > 0)
                    player().get(1).velocity().add(new Vertex(-3, 0));
            }
            case VK_S -> {
                if (!singleplayer && !lose && !pause && player().get(1).velocity().y < 4 && player().get(1).velocity().x != 0 && (!(player().get(1).velocity().x == -1)))
                    player().get(1).velocity().add(new Vertex(0, 1));
            }
            case VK_W -> {
                if (!singleplayer && !lose && !pause && player().get(1).velocity().y > -4 && player().get(1).velocity().x != 0 && (!(player().get(1).velocity().x == -1)))
                    player().get(1).velocity().add(new Vertex(0, -1));
            }
            case VK_RIGHT -> {
                if (player().get(0).velocity().x < 4 && !pause && !startbildschirm)
                    player().get(0).velocity().add(new Vertex(3, 0));
                else if (startbildschirm) {
                    switch (menuestufen) {
                        case 0 -> {
                            if (menuewahl < 2) menuewahl++;
                        }
                        case 1 -> {
                            if (menuewahl1 == 0) menuewahl1++;
                        }
                        case 2 -> {
                            if (menuewahl2 == 0) menuewahl2++;
                        }
                    }
                } else if (pause && pausenmenue != 0 && pausenmenue < 3) pausenmenue++;
            }
            case VK_LEFT -> {
                if (player().get(0).velocity().x > 0 && !pause && !startbildschirm)
                    player().get(0).velocity().add(new Vertex(-3, 0));
                else if (startbildschirm) {
                    switch (menuestufen) {
                        case 0 -> {
                            if (menuewahl != 0) menuewahl--;
                        }
                        case 1 -> {
                            if (menuewahl1 != 0) menuewahl1--;
                        }
                        case 2 -> {
                            if (menuewahl2 != 0) menuewahl2--;
                        }
                    }
                } else if (pause && pausenmenue > 0) pausenmenue--;
            }
            case VK_DOWN -> {
                if (startbildschirm) {
                    if (menuestufen == 0) {
                        if (menuewahl == 0) menuewahl1 = 0;
                        else if (menuewahl == 2) menuewahl1 = 1;
                        menuestufen = 1;
                    } else if (menuestufen == 1) {
                        menuestufen = 2;
                        menuewahl2 = menuewahl1;
                    }
                }
                if (pause && pausenmenue == 0) {
                    pausenmenue = (zwischenspeicher1 == 0 ? 1 : zwischenspeicher1);
                }
                if (player().get(0).velocity().y < 4 && player().get(0).velocity().x != 0
                        && (!(player().get(0).velocity().x == -1)) && !pause) {
                    player().get(0).velocity().add(new Vertex(0, 1));
                }
            }
            case VK_UP -> {
                if (startbildschirm && menuestufen > 0) menuestufen--;
                if (pause && pausenmenue != 0) {
                    zwischenspeicher1 = pausenmenue;
                    pausenmenue = 0;
                }
                if (player().get(0).velocity().y > -4 && player().get(0).velocity().x != 0
                        && (!(player().get(0).velocity().x == -1)) && !pause) {
                    player().get(0).velocity().add(new Vertex(0, -1));
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
                    if (menuestufen == 0) {
                        menuestufen++;
                    } else if (menuestufen == 1) {
                        switch (menuewahl) {
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
                        switch (menuewahl1) {
                            case 0 -> singleplayer = true;
                            case 1 -> singleplayer = false;
                        }
                        init();
                    } else if (menuestufen == 2) {
                        switch (menuewahl2) {
                            case 0 -> steuerung = true;
                            case 1 -> erklaerung = true;
                        }
                    }
                }
                if (pause && !steuerung) {
                    switch (pausenmenue) {
                        case 0 -> {
                            for (var z : kunden()) {
                                z.velocity().x = -1.5;
                            }
                            player().get(0).velocity().x = gang;
                            player().get(0).velocity().y = schwenkung;
                            if (!singleplayer) {
                                player().get(1).velocity().x = gang1;
                                player().get(1).velocity().y = schwenkung1;
                            }
                            timer = zeitspeicher;
                            pause = false;
                        }
                        case 1 -> steuerung = true;

                        case 2 -> {
                            pause = false;
                            init();
                        }
                        case 3 -> {
                            startbildschirm = true;
                            pause = false;
                        }
                    }
                    pausenmenue = 0;
                }
            }
            case VK_ESCAPE -> {
                if (!pause && !lose && !startbildschirm) {
                    pausenmenue = 0;
                    gang = player().get(0).velocity().x;
                    schwenkung = player().get(0).velocity().y;
                    if (!singleplayer) {
                        gang1 = player().get(1).velocity().x;
                        schwenkung1 = player().get(1).velocity().y;
                    }
                    for (var p : player) {
                        p.velocity().x = 0;
                        p.velocity().y = 0;
                    }
                    for (var z : kunden()) {
                        z.velocity().x = 0;
                        z.velocity().y = 0;
                    }
                    zeitspeicher = timer;
                    pause = true;
                } else if (!lose && !startbildschirm && !steuerung) {
                    for (var z : kunden()) {
                        z.velocity().x = -1.5;
                    }
                    player().get(0).velocity().x = gang;
                    player().get(0).velocity().y = schwenkung;
                    if (!singleplayer) {
                        player().get(1).velocity().x = gang1;
                        player().get(1).velocity().y = schwenkung1;

                    }
                    timer = zeitspeicher;
                    zwischenspeicher1 = 0;
                    pause = false;
                } else if (steuerung) steuerung = false;
                else if (erklaerung) erklaerung = false;
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
    public List<ImageObject> player() {
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

    public List<ImageObject> kunden() {
        return kunde;
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
                Objects.equals(this.kunde, that.kunde) &&
                Objects.equals(this.wolken, that.wolken) &&
                Objects.equals(this.texte, that.texte) &&
                Objects.equals(this.ziel, that.ziel) &&
                this.timer == that.timer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, goss, width, height, lieferungen, hintergrund, kunde, wolken, texte, ziel, timer);
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
                "gegner=" + kunde + ", " +
                "wolken=" + wolken + ", " +
                "texte=" + texte + ", " +
                "ziel=" + ziel + ", " +
                "timer=" + timer + ']';
    }

}

