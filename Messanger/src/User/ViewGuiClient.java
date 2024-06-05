package User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Set;

public class ViewGuiClient {
    private final Client client;
    private JFrame frame = new JFrame("Чат");
    private JTextArea messages = new JTextArea(30, 20);
    private JTextArea users = new JTextArea(30, 15);
    private JPanel panel = new JPanel();
    private RoundedTextField textField = new RoundedTextField("Повідомлення...", 30);
    private JButton buttonDisable = new JButton("Від'єднатися від чата");
    private JButton buttonConnect = new JButton("Приєднатися до чату");
    private JButton buttonSend = new JButton("Надіслати повідомлення");
    private JButton buttonLightTheme = new JButton("☀️");
    private JButton buttonDarkTheme = new JButton("🌙");
    private JButton buttonEmoji = new JButton("😊");
    private JPanel topPanelLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private JPanel topPanelRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    public ViewGuiClient(Client client) {
        this.client = client;
    }

    protected void initFrameClient() {

        messages.setEditable(false);
        users.setEditable(false);
        frame.add(new JScrollPane(messages), BorderLayout.CENTER);
        frame.add(new JScrollPane(users), BorderLayout.EAST);
        panel.add(textField);
        panel.add(buttonEmoji);
        panel.add(buttonSend);

        topPanelLeft.add(buttonConnect);
        topPanelLeft.add(buttonDisable);

        topPanelRight.add(buttonLightTheme);
        topPanelRight.add(buttonDarkTheme);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(topPanelLeft, BorderLayout.WEST);
        topPanel.add(topPanelRight, BorderLayout.EAST);

        frame.add(topPanel, BorderLayout.NORTH);

        frame.add(panel, BorderLayout.SOUTH);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (client.isConnect()) {
                    client.quitClient();
                }
                System.exit(0);
            }
        });
        frame.setVisible(true);

        buttonLightTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setLightTheme();
            }
        });
        buttonDarkTheme.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDarkTheme();
            }
        });
        buttonDisable.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { client.quitClient(); }
        });
        buttonConnect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.connectToServer();
            }
        });
        buttonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        textField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        buttonEmoji.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectEmoji();
            }
        });
    }

    private void sendMessage() {
        String message = textField.getText();
        if (!message.isEmpty() && !message.equals("Повідомлення...")) {
            client.sendMessageOnServer(message);
            textField.setText("Повідомлення...");
            textField.setForeground(Color.GRAY);
        }
    }

    protected void addMessage(String text) {
        messages.append(text + "\n");
    }

    protected void refreshListUsers(Set<String> listUsers) {
        users.setText("");
        if (client.isConnect()) {
            StringBuilder text = new StringBuilder("Список користувачів:\n");
            for (String user : listUsers) {
                text.append(user).append("\n");
            }
            users.append(text.toString());
        }
    }

    protected String getServerAddressFromOptionPane() {
        while (true) {
            String addressServer = JOptionPane.showInputDialog(
                    frame, "Введіть адресу сервера:",
                    "Введення адреси сервера",
                    JOptionPane.QUESTION_MESSAGE
            );
            return addressServer.trim();
        }
    }

    protected int getPortServerFromOptionPane() {
        while (true) {
            String port = JOptionPane.showInputDialog(
                    frame, "Введіть порт сервера:",
                    "Введення порту сервера",
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                return Integer.parseInt(port.trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        frame, "Введено неправильний порт сервера. Спробуйте ще раз.",
                        "Помилка введення порту сервера", JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }

    protected String getNameUser() {
        return JOptionPane.showInputDialog(
                frame, "Введіть ім'я користувача:",
                "Введення імені користувача",
                JOptionPane.QUESTION_MESSAGE
        );
    }

    protected void errorDialogWindow(String text) {
        JOptionPane.showMessageDialog(
                frame, text,
                "Помилка", JOptionPane.ERROR_MESSAGE
        );
    }

    private void selectEmoji() {
        JFrame emojiFrame = new JFrame("Вибір смайлика");
        emojiFrame.setSize(200, 200);
        emojiFrame.setLocationRelativeTo(frame);
        emojiFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel emojiPanel = new JPanel(new GridLayout(2, 3));

        JButton emojiButton1 = new JButton("😊");
        JButton emojiButton2 = new JButton("😄");
        JButton emojiButton3 = new JButton("😍");
        JButton emojiButton4 = new JButton("😎");
        JButton emojiButton5 = new JButton("😂");

        emojiButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertEmoji("😊");
                emojiFrame.dispose();
            }
        });

        emojiButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertEmoji("😄");
                emojiFrame.dispose();
            }
        });

        emojiButton3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertEmoji("😍");
                emojiFrame.dispose();
            }
        });

        emojiButton4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertEmoji("😎");
                emojiFrame.dispose();
            }
        });

        emojiButton5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                insertEmoji("😂");
                emojiFrame.dispose();
            }
        });
        emojiPanel.add(emojiButton1);
        emojiPanel.add(emojiButton2);
        emojiPanel.add(emojiButton3);
        emojiPanel.add(emojiButton4);
        emojiPanel.add(emojiButton5);

        emojiFrame.add(emojiPanel);
        emojiFrame.setVisible(true);
    }
    private void insertEmoji(String emoji) {
        String currentText = textField.getText();
        textField.setText(currentText + emoji);
    }

    private void setDarkTheme() {
        Color bgColor = new Color(45, 45, 45);
        Color fgColor = Color.WHITE;
        Color textAreaBgColor = new Color(30, 30, 30);
        Color textAreaFgColor = Color.WHITE;
        Color panelBgColor = new Color(50, 50, 50);

        frame.getContentPane().setBackground(bgColor);
        panel.setBackground(panelBgColor);
        topPanelLeft.setBackground(panelBgColor);
        topPanelRight.setBackground(panelBgColor);

        messages.setBackground(textAreaBgColor);
        messages.setForeground(textAreaFgColor);

        users.setBackground(textAreaBgColor);
        users.setForeground(textAreaFgColor);

        textField.setBackground(textAreaBgColor);
        textField.setForeground(fgColor);

        buttonDisable.setBackground(bgColor);
        buttonDisable.setForeground(fgColor);

        buttonConnect.setBackground(bgColor);
        buttonConnect.setForeground(fgColor);

        buttonSend.setBackground(bgColor);
        buttonSend.setForeground(fgColor);

        buttonLightTheme.setBackground(bgColor);
        buttonLightTheme.setForeground(fgColor);

        buttonDarkTheme.setBackground(bgColor);
        buttonDarkTheme.setForeground(fgColor);

        buttonEmoji.setBackground(bgColor);
        buttonEmoji.setForeground(fgColor);
    }

    private void setLightTheme() {
        Color bgColor = new Color(245, 245, 245);
        Color fgColor = Color.BLACK;
        Color textAreaBgColor = new Color(255, 255, 255);
        Color textAreaFgColor = Color.BLACK;
        Color panelBgColor = new Color(230, 230, 230);

        frame.getContentPane().setBackground(bgColor);
        panel.setBackground(panelBgColor);
        topPanelLeft.setBackground(panelBgColor);
        topPanelRight.setBackground(panelBgColor);

        messages.setBackground(textAreaBgColor);
        messages.setForeground(textAreaFgColor);

        users.setBackground(textAreaBgColor);
        users.setForeground(textAreaFgColor);

        textField.setBackground(textAreaBgColor);
        textField.setForeground(fgColor);

        buttonDisable.setBackground(bgColor);
        buttonDisable.setForeground(fgColor);

        buttonConnect.setBackground(bgColor);
        buttonConnect.setForeground(fgColor);

        buttonSend.setBackground(bgColor);
        buttonSend.setForeground(fgColor);

        buttonLightTheme.setBackground(bgColor);
        buttonLightTheme.setForeground(fgColor);

        buttonDarkTheme.setBackground(bgColor);
        buttonDarkTheme.setForeground(fgColor);

        buttonEmoji.setBackground(bgColor);
        buttonEmoji.setForeground(fgColor);
    }
}