import java.net.*;
import java.io.*;
import java.util.*;

public class MetroServer {
    private Map<Integer, Card> cards;
    public MetroServer() {
        this.cards = new HashMap<>();
    }
    public void issueCard(int id) {
        if (!cards.containsKey(id)) {
            cards.put(id, new Card(id));
            System.out.println("Оформлено нову картку з ID: " + id);
        } else {
            System.out.println("Картка з ID " + id + " вже існує.");
        }
    }
    public String getCardInfo(int id) {
        Card card = cards.get(id);
        if (card != null) {
            System.out.println("Взято інформацію про картку: ID: " + card.getId() + ", Баланс: " + card.getBalance());
            return "Інформація про картку: ID: " + card.getId() + ", Баланс: " + card.getBalance();
        } else {
            System.out.println("Взято інформацію про картку з ID " + id + " , яку не знайдено.");
            return "Картку з ID " + id + " не знайдено.";
        }
    }
    public void topUpBalance(int id, double amount) {
        Card card = cards.get(id);
        if (card != null) {
            card.deposit(amount);
            System.out.println("Поповнено баланс картки з ID " + id + " " + amount);
        } else {
            System.out.println("Картку з ID " + id + " не знайдено.");
        }
    }
    public String payFare(int id, double fare) {
        Card card = cards.get(id);
        if (card != null) {
            if (card.withdraw(fare)) {
                System.out.println("Оплата проїзду картки з ID " + id + ": " + fare);
                return "Оплата проїзду картки з ID " + id + ": " + fare;
            } else {
                System.out.println("Недостатньо коштів на картці з ID " + id);
                return "Недостатньо коштів на картці з ID " + id;
            }
        } else {
            System.out.println("Картку з ID " + id + " не знайдено.");
            return "Картку з ID " + id + " не знайдено.";
        }
    }
    public String checkBalance(int id) {
        Card card = cards.get(id);
        if (card != null) {
            return "Баланс картки з ID " + id + ": " + card.getBalance();
        } else {
            return String.valueOf(-1.0);
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            serverSocket.setSoTimeout(10 * 1000); // Таймаут 10 мс.
            System.out.println("Сервер запущено. Очікування клієнтів...");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Клієнт підключився: " + clientSocket);

                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (SocketTimeoutException e) {
                    System.out.println("Немає клієнтського підключення впродовж " + 10 + " секунд. Вихід.");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private MetroServer server;
        private boolean allRequestsProcessed;
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.server = new MetroServer();
            this.allRequestsProcessed = false;
        }
        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] tokens = inputLine.split(" ");
                    String command = tokens[0];
                    int id;
                    double amount;
                    switch (command) {
                        case "ВИДАЧА":
                            id = Integer.parseInt(tokens[1]);
                            server.issueCard(id);
                            break;
                        case "ІНФО":
                            id = Integer.parseInt(tokens[1]);
                            out.println(server.getCardInfo(id));
                            break;
                        case "ПОПОВНЕННЯ":
                            id = Integer.parseInt(tokens[1]);
                            amount = Double.parseDouble(tokens[2]);
                            server.topUpBalance(id, amount);
                            break;
                        case "ОПЛАТА":
                            id = Integer.parseInt(tokens[1]);
                            amount = Double.parseDouble(tokens[2]);
                            out.println(server.payFare(id, amount));
                            break;
                        case "БАЛАНС":
                            id = Integer.parseInt(tokens[1]);
                            out.println(server.checkBalance(id));
                            break;
                        case "ВИХІД":
                            allRequestsProcessed = true;
                            break;
                        default:
                            out.println("Недійсна команда.");
                            break;
                    }
                    if (allRequestsProcessed) {
                        break;
                    }
                }
                clientSocket.close();
                System.out.println("Клієнт від'єднаний. Закінчення роботи.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}