package User;

import Connection.*;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Connection connection;
    private static ModelGuiClient model;
    private static ViewGuiClient gui;
    private volatile boolean isConnect = false;

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    protected void connectToServer() {
        if (!isConnect) {
            while (true) {
                try {
                    String addressServer = gui.getServerAddressFromOptionPane();
                    int port = gui.getPortServerFromOptionPane();
                    Socket socket = new Socket(addressServer, port);
                    connection = new Connection(socket);
                    isConnect = true;
                    gui.addMessage("Сервісне повідомлення Ви підключилися до сервера.\n");
                    break;
                } catch (Exception e) {
                    gui.errorDialogWindow("Виникла помилка! Можливо, Ви ввели неправильну адресу сервера або порт. Спробуйте ще раз.");
                    break;
                }
            }
        } else gui.errorDialogWindow("Ви вже підключені!");
    }

    protected void nameUserRegistration() {
        while (true) {
            try {
                Message message = connection.receive();
                if (message.getTypeMessage() == MessageType.REQUEST_NAME_USER) {
                    String nameUser = gui.getNameUser();
                    connection.send(new Message(MessageType.USER_NAME, nameUser));
                }
                if (message.getTypeMessage() == MessageType.NAME_USED) {
                    gui.errorDialogWindow("Це ім'я вже зайняте, введіть інше.");
                    String nameUser = gui.getNameUser();
                    connection.send(new Message(MessageType.USER_NAME, nameUser));
                }
                if (message.getTypeMessage() == MessageType.NAME_ACCEPTED) {
                    gui.addMessage("Сервісне повідомлення: Ваше ім'я прийнято!\n");
                    model.setUsers(message.getListUsers());
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                gui.errorDialogWindow("Сталася помилка під час реєстрації. Спробуйте перезайти.");
                try {
                    connection.close();
                    isConnect = false;
                    break;
                } catch (IOException ex) {
                    gui.errorDialogWindow("Помилка при закритті з'єднання.");
                }
            }

        }
    }


    protected void sendMessageOnServer(String text) {
        try {
            connection.send(new Message(MessageType.TEXT_MESSAGE, text));
        } catch (Exception e) {
            gui.errorDialogWindow("Помилка при надсиланні повідомлення.");
        }
    }


    protected void receiveMessageFromServer() {
        while (isConnect) {
            try {
                Message message = connection.receive();
                if (message.getTypeMessage() == MessageType.TEXT_MESSAGE) {
                    gui.addMessage(message.getTextMessage());
                }
                if (message.getTypeMessage() == MessageType.USER_ADDED) {
                    model.addUser(message.getTextMessage());
                    gui.refreshListUsers(model.getUsers());
                    gui.addMessage(String.format("Сервісне повідомлення: користувач %s приєднався до чату.\n", message.getTextMessage()));
                }
                if (message.getTypeMessage() == MessageType.REMOVED_USER) {
                    model.removeUser(message.getTextMessage());
                    gui.refreshListUsers(model.getUsers());
                    gui.addMessage(String.format("Сервісне повідомлення: користувач %s залишив чат.\n", message.getTextMessage()));
                }
            } catch (Exception e) {
                gui.errorDialogWindow("Помилка при отриманні повідомлення від сервера.");
                setConnect(false);
                gui.refreshListUsers(model.getUsers());
                break;
            }
        }
    }

    protected void quitClient() {
        try {
            if (isConnect) {
                connection.send(new Message(MessageType.DISABLE_USER));
                model.getUsers().clear();
                isConnect = false;
                gui.refreshListUsers(model.getUsers());
            } else gui.errorDialogWindow("Ви вже відключені.");
        } catch (Exception e) {
            gui.errorDialogWindow("Сервісне повідомлення: сталася помилка під час вимкнення.");
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        model = new ModelGuiClient();
        gui = new ViewGuiClient(client);
        gui.initFrameClient();
        while (true) {
            if (client.isConnect()) {
                client.nameUserRegistration();
                client.receiveMessageFromServer();
                client.setConnect(false);
            }
        }
    }
}
