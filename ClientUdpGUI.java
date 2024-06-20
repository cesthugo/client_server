// Import des bibliothèques nécessaires
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// Définition de la classe ClientUdpGUI qui étend JFrame pour créer l'interface graphique
public class ClientUdpGUI extends JFrame {
    // Définition des constantes pour le port et l'adresse IP du serveur
    private static final int SERVER_PORT = 324;
    private static final String SERVER_ADDRESS = "10.42.169.195";

    // Déclaration des composants de l'interface utilisateur
    private DatagramSocket socket;
    private JTextField pseudoField;
    private JTextField destPseudoField;
    private JTextField messageField;
    private JTextArea chatArea;

    // Constructeur de la classe ClientUdpGUI
    public ClientUdpGUI() {
        // Configuration de la fenêtre
        setTitle("Client UDP");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initialisation des composants de l'interface utilisateur
        initComponents();

        try {
            // Création du socket UDP
            socket = new DatagramSocket();

            // Démarrage du thread de réception
            startReceiveThread();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour initialiser les composants de l'interface utilisateur
    private void initComponents() {
        // Initialisation des champs de texte et des boutons
        pseudoField = new JTextField(15);
        destPseudoField = new JTextField(15);
        messageField = new JTextField(30);
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        JButton sendPseudoButton = new JButton("Envoyer Pseudo");
        JButton sendMessageButton = new JButton("Envoyer Message");

        // Ajout des écouteurs d'événements pour les boutons
        sendPseudoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendPseudo();
            }
        });

        sendMessageButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        // Configuration de la disposition des composants dans la fenêtre
        JPanel panel = new JPanel(new BorderLayout());
        JPanel inputPanel = new JPanel();

        // Panel pour le pseudo
        JPanel pseudoPanel = new JPanel();
        pseudoPanel.add(new JLabel("Pseudo:"));
        pseudoPanel.add(pseudoField);
        pseudoPanel.add(sendPseudoButton);

        // Panel pour le message
        JPanel messagePanel = new JPanel();
        messagePanel.add(new JLabel("Destinataire:"));
        messagePanel.add(destPseudoField);
        messagePanel.add(new JLabel("Message:"));
        messagePanel.add(messageField);
        messagePanel.add(sendMessageButton);

        inputPanel.setLayout(new GridLayout(2, 1));
        inputPanel.add(pseudoPanel);
        inputPanel.add(messagePanel);

        panel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    // Méthode pour envoyer le pseudo au serveur
    private void sendPseudo() {
        try {
            String pseudo = pseudoField.getText().trim();
            if (!pseudo.isEmpty()) {
                String helloMessage = "hello serveur RX302 " + pseudo;
                byte[] sendData = helloMessage.getBytes();
                InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);
                chatArea.append("Pseudo envoyé : " + pseudo + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour envoyer un message au destinataire spécifié
    private void sendMessage() {
        try {
            String destPseudo = destPseudoField.getText().trim();
            String message = messageField.getText().trim();
            if (!destPseudo.isEmpty() && !message.isEmpty()) {
                String formattedMessage = "msg " + destPseudo + " " + message;
                byte[] sendData = formattedMessage.getBytes();
                InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, SERVER_PORT);
                socket.send(sendPacket);
                chatArea.append("Message envoyé à " + destPseudo + " : " + message + "\n");
                messageField.setText("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour démarrer le thread de réception des messages du serveur
    private void startReceiveThread() {
        new Thread(() -> {
            try {
                byte[] receiveData = new byte[1024];
                while (true) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    socket.receive(receivePacket);
                    String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                    chatArea.append("Message reçu : " + message + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Méthode principale pour lancer l'application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ClientUdpGUI client = new ClientUdpGUI();
            client.setVisible(true);
        });
    }
}
