import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class Controller implements ActionListener {

    WebSocket webSocket;

    JButton modeSwitchButton;
    JLabel vmixLabel;
    JLabel vmixExampleLabel;
    JTextField vmixTextField;
    JTextField smTextField;
    JButton connectButton;
    JButton logsButton;
    JTextArea logsRawTextArea;
    JScrollPane scrollPane;
    JComboBox<Mixer.Info> microphoneBox;
    JLabel rowLengthLabel;
    JTextField rowLengthTextField;
    JButton subtitleTypeButton;
    JTextField bufferSizeTextField;
    JTextField maxDelayTextField;

    static boolean mode;
    static boolean messageFilter;
    static boolean subtitleType;

    public Controller(JButton modeSwitchButton, JLabel vmixLabel, JLabel vmixExampleLabel, JTextField vmixTextField, JTextField smTextField, JButton connectButton, JButton logsButton, JTextArea logsRawTextArea, JScrollPane scrollPane, JComboBox<Mixer.Info> microphoneBox, JLabel rowLengthLabel, JTextField rowLengthTextField, JButton subtitleTypeButton, JTextField bufferSizeTextField, JTextField maxDelayTextField) {
        this.modeSwitchButton = modeSwitchButton;
        this.vmixLabel = vmixLabel;
        this.vmixExampleLabel = vmixExampleLabel;
        this.vmixTextField = vmixTextField;
        this.smTextField = smTextField;
        this.connectButton = connectButton;
        this.logsButton = logsButton;
        this.logsRawTextArea = logsRawTextArea;
        this.scrollPane = scrollPane;
        this.microphoneBox = microphoneBox;
        this.rowLengthLabel = rowLengthLabel;
        this.rowLengthTextField = rowLengthTextField;
        this.bufferSizeTextField = bufferSizeTextField;
        this.maxDelayTextField = maxDelayTextField;
        this.subtitleTypeButton = subtitleTypeButton;
        this.modeSwitchButton.addActionListener(this);
        this.connectButton.addActionListener(this);
        this.logsButton.addActionListener(this);
        this.subtitleTypeButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == connectButton && Double.parseDouble(maxDelayTextField.getText()) >= 0.7 && Double.parseDouble(maxDelayTextField.getText()) <= 4.0)
            toggleConnection();
        else if (e.getSource() == logsButton) {
            if (!messageFilter) {
                logsButton.setText("Logs (Final)");
                messageFilter = true;
            }
            else {
                logsButton.setText("Logs (No filter)");
                messageFilter = false;
            }
        }
        else if (e.getSource() == subtitleTypeButton) {
            if (!subtitleType) {
                subtitleTypeButton.setText("Subtitles: Refreshing");
                subtitleType = true;
            }
            else {
                subtitleTypeButton.setText("Subtitles: Line by line");
                subtitleType = false;
            }
        }
        else if (e.getSource() == modeSwitchButton) {
            if (!mode) {
                vmixLabel.setText("Stream Key");
                vmixExampleLabel.setText("Przykład: 00zc-se93-tk69-4q4s-7v3a");
                modeSwitchButton.setText("Youtube");
                rowLengthLabel.setVisible(false);
                rowLengthTextField.setVisible(false);
                subtitleTypeButton.setVisible(false);
                mode = true;
            }
            else {
                vmixLabel.setText("vMix Key");
                //vmixTextField.setText("http://10.0.0.20:8088");
                vmixExampleLabel.setText("Example: http://10.0.0.20:8088");
                modeSwitchButton.setText("vMix");
                rowLengthLabel.setVisible(true);
                rowLengthTextField.setVisible(true);
                subtitleTypeButton.setVisible(true);
                mode = false;
            }
        }
    }

    public boolean testConnection() {

        int connectionStatus = 0;

        if (!vmixTextField.getText().isEmpty()) {
            if (!mode) {
                try {
                    URL url = new URL(vmixTextField.getText() + "/api");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    if (connection.getResponseCode() == 200) {
                        vmixTextField.setForeground(Color.GREEN);
                        ++connectionStatus;
                    }
                    else
                        vmixTextField.setForeground(Color.RED);
                }
                catch (Exception e) {
                    vmixTextField.setForeground(Color.RED);
                }
            }
            else {
                try {
                    URL url = new URL("http://upload.youtube.com/closedcaption?cid=" + vmixTextField.getText() + "&seq=1");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    connection.setRequestMethod("POST");
                    Instant date = Instant.now();
                    byte[] input = (date.toString().substring(0, 23) + "\n\n").getBytes(StandardCharsets.UTF_8);
                    connection.setRequestProperty("Content-Type", "text/plain");
                    connection.setRequestProperty("Content-Length", Integer.toString(input.length));
                    connection.setDoOutput(true);
                    try (OutputStream os = connection.getOutputStream()) {
                        os.write(input);
                    }
                    if (connection.getResponseCode() == 200) {
                        vmixTextField.setForeground(Color.GREEN);
                        ++connectionStatus;
                    }
                    else
                        vmixTextField.setForeground(Color.RED);
                }
                catch (Exception e) {
                    vmixTextField.setForeground(Color.RED);
                }
            }
        }
        if (!smTextField.getText().isEmpty()) {
            try {
                URL url = new URL("https://asr.api.speechmatics.com/v2/jobs");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Authorization", "Bearer " + smTextField.getText());
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                if (connection.getResponseCode() == 200) {
                    smTextField.setForeground(Color.GREEN);
                    ++connectionStatus;
                }
                else
                    smTextField.setForeground(Color.RED);
            }
            catch (Exception e) {
                smTextField.setForeground(Color.RED);
            }
        }
        return connectionStatus == 2;

    }

    public void toggleConnection() {

        if (webSocket == null || webSocket.isClosed()) {
            try {
                if (testConnection()) {

                    modeSwitchButton.setEnabled(false);
                    modeSwitchButton.setBackground(Color.LIGHT_GRAY);
                    vmixTextField.setFocusable(false);
                    vmixTextField.setEditable(false);
                    vmixTextField.setBackground(Color.LIGHT_GRAY);
                    smTextField.setFocusable(false);
                    smTextField.setEditable(false);
                    smTextField.setBackground(Color.LIGHT_GRAY);
                    microphoneBox.setEnabled(false);
                    bufferSizeTextField.setFocusable(false);
                    bufferSizeTextField.setEditable(false);
                    bufferSizeTextField.setBackground(Color.LIGHT_GRAY);
                    maxDelayTextField.setFocusable(false);
                    maxDelayTextField.setEditable(false);
                    maxDelayTextField.setBackground(Color.LIGHT_GRAY);
                    connectButton.setText("Rozłącz");
                    URL url = new URL("https://mp.speechmatics.com/v1/api_keys?type=rt");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Authorization", "Bearer " + smTextField.getText());
                    connection.setDoOutput(true);
                    String jsonInputString = "{\"ttl\": 60 }";

                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    connection.getResponseCode();
                    StringBuilder response = new StringBuilder();

                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        String responseLine;
                        while ((responseLine = br.readLine()) != null)
                            response.append(responseLine.trim());
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    URI uri = new URI("wss://eu2.rt.speechmatics.com/v2?jwt=" + response.substring(31, response.length() - 2));
                    webSocket = new WebSocket(uri, this, (Mixer.Info) microphoneBox.getSelectedItem());
                    webSocket.connect();

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else {
            modeSwitchButton.setEnabled(true);
            modeSwitchButton.setBackground(null);
            vmixTextField.setFocusable(true);
            vmixTextField.setEditable(true);
            vmixTextField.setBackground(Color.WHITE);
            vmixTextField.setForeground(Color.BLACK);
            smTextField.setFocusable(true);
            smTextField.setEditable(true);
            smTextField.setBackground(Color.WHITE);
            smTextField.setForeground(Color.BLACK);
            microphoneBox.setEnabled(true);
            bufferSizeTextField.setFocusable(true);
            bufferSizeTextField.setEditable(true);
            bufferSizeTextField.setBackground(Color.WHITE);
            maxDelayTextField.setFocusable(true);
            maxDelayTextField.setEditable(true);
            maxDelayTextField.setBackground(Color.WHITE);
            connectButton.setText("Połącz");
            try {
                webSocket.running = false;
                Thread.sleep(1000);
                webSocket.close();
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}