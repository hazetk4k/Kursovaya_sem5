package Server.mvc;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class Model {

    public String connection;

    public String getConnection() {
        return connection;
    }

    public boolean serverStatus = true;

    public boolean isServerStatus() {
        return serverStatus;
    }

    public void setServerStatus(boolean serverStatus) {
        this.serverStatus = serverStatus;
    }

    public Model() {

    }

    public class ServerStatusThread implements Runnable {
        Thread thread;
        ServerSocket server;

        public ServerStatusThread(ServerSocket server) {
            this.server = server;
            thread = new Thread(this, "ServerStatusThread");
            thread.start();

        }

        @Override
        public void run() {
            while (isServerStatus()) {
                try {
                    thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                this.server.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void ModelMain() {
        ServerSocket server = null;
        try {
            try {
                server = new ServerSocket(3311);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                server.setReuseAddress(true);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            Socket client = null;
            ServerStatusThread serverStatusThread = new ServerStatusThread(server);
            while (isServerStatus()) {

                try {
                    client = server.accept();
                } catch (IOException e) {
                    break;
                }

                this.connection = ("Подключен новый клиент "
                        + client.getInetAddress().getHostAddress());

                ClientHandler clientSock = new ClientHandler(client);
                try {
                    clientSock.thread.join();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                if (clientSock.isTurnOffServer()) {
                    break;
                }
            }
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}

class ClientHandler implements Runnable {
    Thread thread;
    private final Socket clientSocket;
    private boolean turnOffServer = false;

    public ClientHandler(Socket socket) {
        this.thread = new Thread(this, "Client thread");
        this.thread.start();
        this.clientSocket = socket;
    }

    public boolean isTurnOffServer() {
        return this.turnOffServer;
    }

    @Override
    public void run() {

    }
}
