import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
    private class Cartas {
        String value;
        String type;

        Cartas(String value, String type) {
            this.value = value;
            this.type = type;
        }

        public String toString() {
            return value + "-" + type;
        }

        public int getValue() {
            if ("AJQK".contains(value)) { // A J K Q
                if (value.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value); // de 2 até 10
        }

        public boolean isAce() {
            return value.equals("A");
        }

        public String getImagePath() {
            return "./cartas/" + toString() + ".png";
        }
    }

    ArrayList<Cartas> deck;
    Random random = new Random();
    Cartas hiddenCartas;
    ArrayList<Cartas> dealerHand;
    int dealerSum;
    int dealerAceCount;

    ArrayList<Cartas> playerHand;
    int playerSum;
    int playerAceCount;

    int boardWidth = 600;
    int boardHeight = boardWidth;

    int cartasWidth = 110;
    int cartasHeight = 154;

    JFrame frame = new JFrame("Black Jack dos Crias");
    JPanel gamPanel = new JPanel() {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            try {
                // Carta para baixo
                Image hiddenCartasImg = new ImageIcon(getClass().getResource("./cartas/BACK.png")).getImage();
                if (!stayButton.isEnabled()) {
                    hiddenCartasImg = new ImageIcon(getClass().getResource(hiddenCartas.getImagePath())).getImage();
                }
                g.drawImage(hiddenCartasImg, 20, 20, cartasWidth, cartasHeight, null);

                // Empate mesa
                for (int i = 0; i < dealerHand.size(); i++) {
                    Cartas cartas = dealerHand.get(i);
                    Image cartasImg = new ImageIcon(getClass().getResource(cartas.getImagePath())).getImage();
                    g.drawImage(cartasImg, cartasWidth + 25 + (cartasWidth + 5) * i, 20, cartasWidth, cartasHeight, null);
                }

                // Empate jogador
                for (int i = 0; i < playerHand.size(); i++) {
                    Cartas cartas = playerHand.get(i);
                    Image cartasImg = new ImageIcon(getClass().getResource(cartas.getImagePath())).getImage();
                    g.drawImage(cartasImg, 20 + (cartasWidth + 5) * i, 320, cartasWidth, cartasHeight, null);
                }

                if (!stayButton.isEnabled()) {
                    dealerSum = reduceDealerAce();
                    playerSum = reducePlayerAce();
                    System.out.println("Ficar: ");
                    System.out.println(dealerSum);
                    System.out.println(playerSum);

                    String message = "";
                    if (playerSum > 21) {
                        message = "Perdeu otario XD";
                    } else if (dealerSum > 21) {
                        message = "Ganhou!!!";
                    } else if (playerSum == dealerSum) {
                        message = "Empatou";
                    } else if (playerSum > dealerSum) {
                        message = "Ganhou";
                    } else if (playerSum < dealerSum) {
                        message = "Perdeu otário XD";
                    }

                    g.setFont(new Font("Arial", Font.PLAIN, 30));
                    g.setColor(Color.white);
                    g.drawString(message, 220, 250);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Pedir");
    JButton stayButton = new JButton("Ficar");
    JButton restartButton = new JButton("Reiniciar");

    BlackJack() {
        startGame();

        frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamPanel.setLayout(new BorderLayout());
        gamPanel.setBackground(new Color(53, 101, 77));
        frame.add(gamPanel);

        hitButton.setFocusable(false);
        buttonPanel.add(hitButton);
        stayButton.setFocusable(false);
        buttonPanel.add(stayButton);
        restartButton.setFocusable(false);
        buttonPanel.add(restartButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        hitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Cartas cartas = deck.remove(deck.size() - 1);
                playerSum += cartas.getValue();
                playerAceCount += cartas.isAce() ? 1 : 0;
                playerHand.add(cartas);
                if (reducePlayerAce() > 21) {
                    hitButton.setEnabled(false);
                }
                gamPanel.repaint();
            }
        });

        stayButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hitButton.setEnabled(false);
                stayButton.setEnabled(false);

                while (dealerSum < 17) {
                    Cartas cartas = deck.remove(deck.size() - 1);
                    dealerSum += cartas.getValue();
                    dealerAceCount += cartas.isAce() ? 1 : 0;
                    dealerHand.add(cartas);
                }
                gamPanel.repaint();
            }
        });

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame(); // Reinicia o jogo
                hitButton.setEnabled(true);
                stayButton.setEnabled(true);
                gamPanel.repaint();
            }
        });

        gamPanel.repaint();
    }

    public void startGame() {
        buildDeck();
        shuffleDeck();

        dealerHand = new ArrayList<Cartas>();
        dealerSum = 0;
        dealerAceCount = 0;

        hiddenCartas = deck.remove(deck.size() - 1);
        dealerSum += hiddenCartas.getValue();
        dealerAceCount += hiddenCartas.isAce() ? 1 : 0;

        Cartas cartas = deck.remove(deck.size() - 1);
        dealerSum += cartas.getValue();
        dealerAceCount += cartas.isAce() ? 1 : 0;
        dealerHand.add(cartas);

        System.out.println("Mesa: ");
        System.out.println(hiddenCartas);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);

        playerHand = new ArrayList<Cartas>();
        playerSum = 0;
        playerAceCount = 0;

        for (int i = 0; i < 2; i++) {
            cartas = deck.remove(deck.size() - 1);
            playerSum += cartas.getValue();
            playerAceCount += cartas.isAce() ? 1 : 0;
            playerHand.add(cartas);
        }

        System.out.println("Jogador: ");
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);
    }

    public void buildDeck() {
        deck = new ArrayList<Cartas>();
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] types = {"C", "D", "H", "S"};

        for (String type : types) {
            for (String value : values) {
                Cartas cartas = new Cartas(value, type);
                deck.add(cartas);
            }
        }

        System.out.println("Seu baralho: ");
        System.out.println(deck);
    }

    public void shuffleDeck() {
        for (int i = 0; i < deck.size(); i++) {
            int j = random.nextInt(deck.size());
            Cartas currCartas = deck.get(i);
            Cartas randomCartas = deck.get(j);
            deck.set(i, randomCartas);
            deck.set(j, currCartas);
        }

        System.out.println("Embaralhado");
        System.out.println(deck);
    }

    public int reducePlayerAce() {
        while (playerSum > 21 && playerAceCount > 0) {
            playerSum -= 10;
            playerAceCount -= 1;
        }
        return playerSum;
    }

    public int reduceDealerAce() {
        while (dealerSum > 21 && dealerAceCount > 0) {
            dealerSum -= 10;
            dealerAceCount -= 1;
        }
        return dealerSum;
    }

    }

