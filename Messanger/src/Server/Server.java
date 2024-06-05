package Server;

import Connection.*;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class Server {
    private ServerSocket serverSocket;
    private static GuiServer gui;
    private static ModelGuiServer model;
    private static volatile boolean isServerStart = false;


    protected void startServer(int port) {
        try {
            serverSocket = new ServerSocket(port);
            isServerStart = true;
            gui.refreshDialogWindowServer("Сервер запущено.\n");
        } catch (Exception e) {
            gui.refreshDialogWindowServer("Не вдалося запустити сервер.\n");
        }
    }

    protected void stopServer() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                for (Map.Entry<String, Connection> user : model.getAllUsersMultiChat().entrySet()) {
                    user.getValue().close();
                }
                serverSocket.close();
                model.getAllUsersMultiChat().clear();
                gui.refreshDialogWindowServer("Сервер зупинено.\n");
            } else gui.refreshDialogWindowServer("Сервер ще не запущено!\n");
        } catch (Exception e) {
            gui.refreshDialogWindowServer("Зупинити сервер не вдалося.\n");
        }
    }

    protected void acceptServer() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                new ServerThread(socket).start();
            } catch (Exception e) {
                gui.refreshDialogWindowServer("Зв'язок із сервером втрачено.\n");
                break;
            }
        }
    }

    protected void sendMessageAllUsers(Message message) {
        for (Map.Entry<String, Connection> user : model.getAllUsersMultiChat().entrySet()) {
            try {
                user.getValue().send(message);
            } catch (Exception e) {
                gui.refreshDialogWindowServer("Помилка надсилання повідомлення всім користувачам!\n");
            }
        }
    }

    private class ServerThread extends Thread {
        private Socket socket;
        public ServerThread(Socket socket) {
            this.socket = socket;
        }
        private String requestAndAddingUser(Connection connection) {
            while (true) {
                try {
                    connection.send(new Message(MessageType.REQUEST_NAME_USER));
                    Message responseMessage = connection.receive();
                    String userName = responseMessage.getTextMessage();
                    if (responseMessage.getTypeMessage() == MessageType.USER_NAME && userName != null && !userName.isEmpty() && !model.getAllUsersMultiChat().containsKey(userName)) {
                        model.addUser(userName, connection);
                        Set<String> listUsers = new HashSet<>();
                        for (Map.Entry<String, Connection> users : model.getAllUsersMultiChat().entrySet()) {
                            listUsers.add(users.getKey());
                        }
                        connection.send(new Message(MessageType.NAME_ACCEPTED, listUsers));
                        sendMessageAllUsers(new Message(MessageType.USER_ADDED, userName));
                        return userName;
                    }
                    else connection.send(new Message(MessageType.NAME_USED));
                } catch (Exception e) {
                    gui.refreshDialogWindowServer("Виникла помилка при запиті та додаванні нового користувача\n");
                }
            }
        }

        private void messagingBetweenUsers(Connection connection, String userName) {
            while (true) {
                try {
                    Message message = connection.receive();
                    if (message.getTypeMessage() == MessageType.TEXT_MESSAGE) {
                        String textMessage = String.format("%s: %s\n", userName, message.getTextMessage());
                        sendMessageAllUsers(new Message(MessageType.TEXT_MESSAGE, textMessage));
                    }
                    if (message.getTypeMessage() == MessageType.DISABLE_USER) {
                        sendMessageAllUsers(new Message(MessageType.REMOVED_USER, userName));
                        model.removeUser(userName);
                        connection.close();
                        gui.refreshDialogWindowServer(String.format("Користувач з віддаленим доступом %s вимкнувся.\n", socket.getRemoteSocketAddress()));
                        break;
                    }
                } catch (Exception e) {
                    gui.refreshDialogWindowServer(String.format("Виникла помилка під час розсилки повідомлення від користувача %s, або вимкнувся!\n", userName));
                    break;
                }
            }
        }

        @Override
        public void run() {
            gui.refreshDialogWindowServer(String.format("Підключився новий користувач із віддаленим сокетом - %s.\n", socket.getRemoteSocketAddress()));
            try {
                Connection connection = new Connection(socket);
                String nameUser = requestAndAddingUser(connection);
                messagingBetweenUsers(connection, nameUser);
            } catch (Exception e) {
                gui.refreshDialogWindowServer(String.format("Виникла помилка при розсилці повідомлення від користувача!\n"));
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        gui = new GuiServer(server);
        model = new ModelGuiServer();
        gui.initFrameServer();
        while (true) {
            if (isServerStart) {
                server.acceptServer();
                isServerStart = false;
            }
        }
    }
}