// Import des bibliothèques nécessaires
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

// Définition de la classe ServeurUdp
public class ServeurUdp {
    // Définition de la constante pour le port d'écoute
    private static final int PORT = 324;

    // Map pour stocker les informations des clients connectés
    private static Map<String, ClientInfo> clients = new HashMap<>();

    // Méthode principale pour démarrer le serveur
    public static void main(String[] args) {
        DatagramSocket socket = null;
        try {
            // Création du socket UDP sur le port spécifié
            socket = new DatagramSocket(PORT);
            System.out.println("Serveur démarré et écoute sur le port " + PORT); // Affichage du message de démarrage

            // Initialisation des tableaux de données pour la réception et l'envoi des paquets UDP
            byte[] receiveData = new byte[1024];
            byte[] sendData;

            // Boucle infinie pour écouter les messages des clients
            while (true) {
                // Réception du paquet
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                socket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                System.out.println("Message reçu : " + message); // Affichage du message reçu pour le débogage

                // Gestion des messages
                if (message.startsWith("hello serveur RX302")) {
                    // Traitement du message d'inscription d'un nouveau client
                    String pseudo = message.substring("hello serveur RX302".length()).trim();
                    clients.put(pseudo, new ClientInfo(clientAddress, clientPort));
                    System.out.println("Nouveau client : " + clientAddress.getHostAddress() + ":" + clientPort + " avec le pseudo : " + pseudo);

                    // Réponse au client
                    String responseMessage = "Serveur RX302 ready";
                    sendData = responseMessage.getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
                    socket.send(sendPacket);
                } else if (message.startsWith("msg")) {
                    // Traitement du message à envoyer à un client spécifique
                    String[] parts = message.split(" ", 3);
                    if (parts.length == 3) {
                        String pseudo = parts[1];
                        String actualMessage = parts[2];

                        ClientInfo destClient = clients.get(pseudo);
                        if (destClient != null) {
                            sendData = actualMessage.getBytes();
                            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, destClient.getAddress(), destClient.getPort());
                            socket.send(sendPacket);
                        } else {
                            System.out.println("Pseudo non trouvé : " + pseudo);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Fermeture du socket lorsque le serveur se termine
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}

// Classe pour stocker les informations d'un client
class ClientInfo {
    private InetAddress address;
    private int port;

    // Constructeur de la classe ClientInfo
    public ClientInfo(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    // Méthode pour récupérer l'adresse IP du client
    public InetAddress getAddress() {
        return address;
    }

    // Méthode pour récupérer le port du client
    public int getPort() {
        return port;
    }

    // Méthode pour vérifier l'égalité entre deux objets ClientInfo
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientInfo that = (ClientInfo) o;

        if (port != that.port) return false;
        return address != null ? address.equals(that.address) : that.address == null;
    }

    // Méthode pour calculer le code de hachage de l'objet ClientInfo
    @Override
    public int hashCode() {
        int result = address != null ? address.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}
