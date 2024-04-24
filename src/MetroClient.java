import java.net.*;
import java.io.*;

public class MetroClient {
    public static void main(String[] args) {
        int attempts = 0;
        boolean connected = false;
        while (attempts < 5) {
            try {
                Socket socket = new Socket("localhost", 8888);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                out.println("ВИДАЧА 123");
                out.println("ІНФО 123");
                out.println("ПОПОВНЕННЯ 123 20.0");
                out.println("ОПЛАТА 123 10.0");
                out.println("БАЛАНС 123");

                out.println("ВИДАЧА 456");
                out.println("ПОПОВНЕННЯ 456 50.0");
                out.println("ІНФО 456");
                out.println("ОПЛАТА 456 10.0");

                out.println("ІНФО 789");

                out.println("ВИХІД");

                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println("Відповідь сервера: " + response);
                }

                socket.close();
                connected = true;

                System.out.println("Усі запити виконано успішно. Вихід.");
                break;
            } catch (IOException e) {
                System.out.println("Спроба підключення не вдалася. Повторна спроба...");
                attempts++;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        if (!connected) {
            System.out.println("Клієнт не знайшов сервера після " + 5 + " невдалих спроб підключення. Вихід.");
        }
    }
}