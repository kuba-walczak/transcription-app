import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import javax.sound.sampled.*;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;

public class WebSocket extends WebSocketClient {

    Controller controller;
    Mixer.Info selectedMicrophone;
    TargetDataLine microphoneLine;
    Deque<String> deque1;
    Deque<String> deque2;
    int seqIncrement = 0;

    public volatile boolean running;
    String subtitle;

    public WebSocket(URI serverUri, Controller controller, Mixer.Info selectedMicrophone) {
        super(serverUri);
        this.controller = controller;
        this.selectedMicrophone = selectedMicrophone;
        deque1 = new ArrayDeque<>();
        deque2 = new ArrayDeque<>();
        running = true;
        subtitle = "";
    }

    public void vmixSubtitlesAdd(String word) {
        word = word.substring(0, word.length() - 1);
        boolean punctuationStart = word.startsWith(",") || word.startsWith(".") || word.startsWith("?") || word.startsWith("!");
        if (deque2.isEmpty())
            if (deque1.toString().length() + word.length() > Integer.parseInt(controller.rowLengthTextField.getText()))
                if (punctuationStart) {
                    deque1.add(word.substring(0, 1));
                    deque2.add(word.length() > 1 ? word.substring(2) : "");
                }
                else
                    deque2.addLast(word);
            else
                if (punctuationStart)
                    deque1.add(word);
                else {
                    deque1.add(deque1.isEmpty() ? "" : " ");
                    deque1.add(word);
                }
        else
            if (deque2.toString().length() + word.length() > Integer.parseInt(controller.rowLengthTextField.getText())) {
                if (!Controller.subtitleType)
                    if (punctuationStart) {
                        deque1.clear();
                        deque2.add(word.substring(0, 1));
                        for (String word2 : deque2)
                            deque1.addLast(word2);
                        deque2.clear();
                        deque2.add(word.length() > 1 ? word.substring(2) : "");
                    }
                    else {
                        deque1.clear();
                        for (String word2 : deque2)
                            deque1.addLast(word2);
                        deque2.clear();
                        deque2.add(word);
                    }
                else
                    if (punctuationStart) {
                        deque1.clear();
                        deque2.clear();
                        deque1.add(word.length() > 1 ? word.substring(2) : "");
                    }
                    else {
                        deque1.clear();
                        deque2.clear();
                        deque1.add(word);
                    }
            }
            else
                if (punctuationStart)
                    deque2.add(word);
                else {
                    deque2.add(deque2.isEmpty() ? "" : " ");
                    deque2.add(word);
                }
    }

    public boolean youtubeSubtitlesAdd(String word) {
        word = word.substring(0, word.length() - 1);
        if (word.startsWith(",") || word.startsWith(".") || word.startsWith("?") || word.startsWith("!")) {
            subtitle += word;
        }
        else {
            if (subtitle.isEmpty())
                subtitle += (word);
            else
                subtitle += (" " + word);
        }
        if (subtitle.length() > 10) {
            if (subtitle.startsWith(",") || subtitle.startsWith(".") || subtitle.startsWith("?") || subtitle.startsWith("!"))
                subtitle = subtitle.substring(2);
            return true;
        }
        else
            return false;
    }

    /*word = word.substring(0, word.length() - 1);
        if (word.startsWith(",") || word.startsWith(".") || word.startsWith("?") || word.startsWith("!")) {
        subtitle += word.substring(0, 1);
        if (word.length() > 1)
            subtitleTemp += word.substring(2);
    }
        else {
        if (subtitle.isEmpty())
            subtitle += (word);
        else
            subtitle += (" " + word);
    }
    char[] chars = {',', '.', '?', '!'};
    int index = subtitle.length();
        for (char chr : chars)
            if ((subtitle.indexOf(chr) < index) && (subtitle.indexOf(chr) != -1))
    index = subtitle.indexOf(chr);
        if (index != subtitle.length()) {
        subtitleTemp = subtitle.substring(index + 1);
        subtitle = subtitle.substring(0, index + 1);
        return true;
    }
        return false;*/

    /*public boolean youtubeSubtitlesAdd(String word) {
        word = word.substring(0, word.length() - 1);
        ArrayList<String> result = new ArrayList<>();
        if (word.startsWith(",") || word.startsWith(".") || word.startsWith("?") || word.startsWith("!")) {
            if (word.length() > 1) {
                result.add(word.substring(0, 1));
                result.add(word.substring(2));
            }
            else {
                result.add(word);
            }
        }
        else {
            result.add(" " + word);
        }
    }*/

    /*if (!deque2.isEmpty() && deque1.toString().length() + word1.length() < Integer.parseInt(controller.rowLengthTextField.getText()) || word1.startsWith(",") || word1.startsWith(".") || word1.startsWith("?") || word1.startsWith("!"))
            if (word1.startsWith(",") || word1.startsWith(".") || word1.startsWith("?") || word1.startsWith("!")) {
        deque1.add(word1.substring(0, 1));
        vmixSubtitlesAdd(word1.substring(2));
    }
            else {
        deque1.add(" ");
        deque1.addLast(word1);
    }
        else if (deque2.toString().length() + word1.length() > Integer.parseInt(controller.rowLengthTextField.getText()) && !word1.startsWith(",") && !word1.startsWith(".") && !word1.startsWith("?") && !word1.startsWith("!")) {
        if (!Controller.subtitleType) {
            deque1.clear();
            for (String word2 : deque2)
                deque1.addLast(word2);
            deque2.clear();
            deque2.add(word1);
        }
        else {
            deque1.clear();
            deque2.clear();
            deque1.add(word1);
        }
    }
        else {
        if (word1.startsWith(",") || word1.startsWith(".") || word1.startsWith("?") || word1.startsWith("!")) {
            deque2.add(word1.substring(0, 1));
            vmixSubtitlesAdd(word1.substring(2));
        }
        else {
            deque2.add(" ");
            deque2.addLast(word1);
        }
    }*/

    @Override
    public void onOpen(ServerHandshake handshake) {
        try {
            AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
            Mixer mixer = AudioSystem.getMixer(selectedMicrophone);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, audioFormat);
            microphoneLine = (TargetDataLine) mixer.getLine(info);
            microphoneLine.open(audioFormat, Integer.parseInt(controller.bufferSizeTextField.getText()));
            microphoneLine.start();
            AudioInputStream audioInputStream = new AudioInputStream(microphoneLine);
            send(String.format("{\"message\":\"StartRecognition\",\"audio_format\":{\"type\":\"raw\",\"encoding\":\"pcm_s16le\",\"sample_rate\":16000},\"transcription_config\":{\"language\":\"pl\",\"max_delay\":" + controller.maxDelayTextField.getText() + ",\"enable_partials\":true}}", "pcm_s16le", 16000));
            streamAudio(audioInputStream, Integer.parseInt(controller.bufferSizeTextField.getText()));
            if (!Controller.mode) {
                URL url1 = new URL(controller.vmixTextField.getText() + "/api/?Function=SetText&Input=1&SelectedName=SubtitleRow1.Text&Value=" + URLEncoder.encode("", StandardCharsets.UTF_8));
                URL url2 = new URL(controller.vmixTextField.getText() + "/api/?Function=SetText&Input=1&SelectedName=SubtitleRow2.Text&Value=" + URLEncoder.encode("", StandardCharsets.UTF_8));
                HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                connection1.setRequestMethod("GET");
                connection1.getResponseCode();
                connection1.disconnect();
                HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
                connection2.setRequestMethod("GET");
                connection2.getResponseCode();
                connection2.disconnect();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onMessage(String message) {

        if (!Controller.messageFilter) {
            controller.logsRawTextArea.append(message + "\n");
            controller.scrollPane.getVerticalScrollBar().setValue(controller.scrollPane.getVerticalScrollBar().getMaximum());
        }
        else if (message.contains("AddTranscript")) {
            int transcriptStartIndex = message.indexOf("transcript");
            if (!message.substring(transcriptStartIndex + 14, message.length() - 3).isEmpty()) {
                controller.scrollPane.getVerticalScrollBar().setValue(controller.scrollPane.getVerticalScrollBar().getMaximum());
                controller.logsRawTextArea.append(message.substring(transcriptStartIndex + 14, message.length() - 3) + "\n");
            }
        }

        while (controller.logsRawTextArea.getText().length() > 1500) {
            int firstLineEnd = controller.logsRawTextArea.getText().indexOf('\n');
            String newText = controller.logsRawTextArea.getText().substring(firstLineEnd + 1);
            controller.logsRawTextArea.setText(newText);
        }

        if (message.contains("AddTranscript")) {
            int transcriptStartIndex = message.indexOf("transcript");
            if (message.charAt(transcriptStartIndex + 14) != '\"') {
                if (!Controller.mode) {
                    vmixSubtitlesAdd(message.substring(transcriptStartIndex + 14, message.length() - 3));
                    try {
                        String subtitleRow1 = "";
                        String subtitleRow2 = "";
                        for (String word : deque1)
                            subtitleRow1 += word;
                        for (String word : deque2)
                            subtitleRow2 += word;
                        URL url1 = new URL(controller.vmixTextField.getText() + "/api/?Function=SetText&Input=1&SelectedName=SubtitleRow1.Text&Value=" + URLEncoder.encode(subtitleRow1, StandardCharsets.UTF_8));
                        URL url2 = new URL(controller.vmixTextField.getText() + "/api/?Function=SetText&Input=1&SelectedName=SubtitleRow2.Text&Value=" + URLEncoder.encode(subtitleRow2, StandardCharsets.UTF_8));
                        //api/?Function=SetText&Input=1&SelectedName=Message.Text&Value=
                        HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                        connection1.setRequestMethod("GET");
                        connection1.getResponseCode();
                        connection1.disconnect();
                        HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
                        connection2.setRequestMethod("GET");
                        connection2.getResponseCode();
                        connection2.disconnect();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    try {
                        if (youtubeSubtitlesAdd(message.substring(transcriptStartIndex + 14, message.length() - 3))) {
                            URL url = new URL("http://upload.youtube.com/closedcaption?cid=" + controller.vmixTextField.getText() + "&seq=" + seqIncrement++);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            Instant date = Instant.now();

                            byte[] input = (date.toString().substring(0, 23) + "\n" + subtitle + "\n").getBytes(StandardCharsets.UTF_8);
                            connection.setRequestProperty("Content-Type", "text/plain");
                            connection.setRequestProperty("Content-Length", Integer.toString(input.length));
                            connection.setDoOutput(true);
                            try (OutputStream os = connection.getOutputStream()) {
                                os.write(input);
                            }
                            System.out.print(seqIncrement - 1 + " " + connection.getResponseCode() + " ");
                            System.out.println(date.toString().substring(0, 23) + "\n" + subtitle);
                            subtitle = "";
                        }
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        microphoneLine.stop();
        microphoneLine.close();
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void streamAudio(InputStream audioSource, int bufferSize) {

        new Thread(() -> {
            try {
                byte[] buffer = new byte[bufferSize];
                int bytesRead;
                while (running && (bytesRead = audioSource.read(buffer)) != -1) {
                    send(ByteBuffer.wrap(buffer, 0, bytesRead));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

    }
}