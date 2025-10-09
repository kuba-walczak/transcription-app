import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;

public class Main {

    static Controller controller;

    public static void main(String[] args) {

        JFrame frame = new JFrame("vMix Transcription");
        generateUI(frame);

        AudioFormat audioFormat = new AudioFormat(16000, 16, 1, true, false);
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        for (Mixer.Info input : AudioSystem.getMixerInfo()) {

            Mixer mixer = AudioSystem.getMixer(input);
            if (mixer.isLineSupported(dataLineInfo)) {
                controller.microphoneBox.addItem(input);
            }

        }

    }

    public static void generateUI(JFrame frame) {

        frame.setSize(1165, 690);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(null);
        frame.setResizable(false);

        JButton modeSwitchButton = new JButton();
        modeSwitchButton.setFont(modeSwitchButton.getFont().deriveFont(24f));
        modeSwitchButton.setText("vMix");
        modeSwitchButton.setBounds(0, 0, 1165, 50);
        //modeSwitchButton.setEnabled(false);
        //modeSwitchButton.setBackground(Color.LIGHT_GRAY);

        JLabel vmixLabel = new JLabel();
        vmixLabel.setText("vMix Key");
        vmixLabel.setFont(vmixLabel.getFont().deriveFont(24f));
        vmixLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vmixLabel.setBounds(50, 100, 500, 50);

        JLabel vmixExampleLabel = new JLabel();
        vmixExampleLabel.setText("Example: http://10.0.0.20:8088");
        vmixExampleLabel.setFont(vmixExampleLabel.getFont().deriveFont(14f));
        vmixExampleLabel.setBounds(50, 180, 500, 50);

        JTextField vmixTextField = new JTextField();
        vmixTextField.setFont(vmixTextField.getFont().deriveFont(24f));
        vmixTextField.setBounds(50, 150, 500, 50);

        JLabel smLabel = new JLabel();
        smLabel.setText("Speechmatics Key");
        smLabel.setFont(smLabel.getFont().deriveFont(24f));
        smLabel.setHorizontalAlignment(SwingConstants.CENTER);
        smLabel.setBounds(50, 225, 500, 50);

        JLabel smExampleLabel = new JLabel();
        smExampleLabel.setText("Example: QjUlsDRweLATNBMeYUWttFrSeGTHzfJv");
        smExampleLabel.setFont(smExampleLabel.getFont().deriveFont(14f));
        smExampleLabel.setBounds(50, 305, 500, 50);

        JTextField smTextField = new JTextField();
        smTextField.setFont(smTextField.getFont().deriveFont(24f));
        smTextField.setBounds(50, 275, 500, 50);

        JButton connectButton = new JButton();
        connectButton.setFont(connectButton.getFont().deriveFont(24f));
        connectButton.setText("Connect");
        connectButton.setBounds(650, 475, 125, 125);

        JButton logsButton = new JButton();
        logsButton.setText("Logi (Brak Filtra)");
        logsButton.setFont(logsButton.getFont().deriveFont(24f));
        logsButton.setBounds(600, 100, 500, 50);

        JTextArea logsRawTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(logsRawTextArea);
        logsRawTextArea.setFont(logsRawTextArea.getFont().deriveFont(14f));
        scrollPane.setBounds(600, 150, 500, 300);
        logsRawTextArea.setEditable(false);
        logsRawTextArea.setFocusable(false);

        JLabel audioLabel = new JLabel();
        audioLabel.setText("Audio");
        audioLabel.setFont(audioLabel.getFont().deriveFont(24f));
        audioLabel.setHorizontalAlignment(SwingConstants.CENTER);
        audioLabel.setBounds(50, 350, 500, 50);

        JComboBox<Mixer.Info> microphoneBox = new JComboBox<>();
        microphoneBox.setFont(microphoneBox.getFont().deriveFont(14f));
        microphoneBox.setBounds(50, 400, 500, 50);
        microphoneBox.setEditable(false);
        microphoneBox.setFocusable(false);

        JLabel bufferSizeLabel = new JLabel();
        bufferSizeLabel.setText("Audo packet size (bits)");
        bufferSizeLabel.setFont(bufferSizeLabel.getFont().deriveFont(18f));
        bufferSizeLabel.setBounds(175, 475, 500, 50);

        JTextField bufferSizeTextField = new JTextField();
        bufferSizeTextField.setFont(bufferSizeTextField.getFont().deriveFont(24f));
        bufferSizeTextField.setHorizontalAlignment(SwingConstants.CENTER);
        bufferSizeTextField.setBounds(50, 475, 100, 50);

        JLabel maxDelayLabel = new JLabel();
        maxDelayLabel.setText("Audio processing delay (min. 0.7, max. 4.0 sec)");
        maxDelayLabel.setFont(maxDelayLabel.getFont().deriveFont(18f));
        maxDelayLabel.setBounds(125, 550, 500, 50);

        JTextField maxDelayTextField = new JTextField();
        maxDelayTextField.setFont(maxDelayTextField.getFont().deriveFont(24f));
        maxDelayTextField.setHorizontalAlignment(SwingConstants.CENTER);
        maxDelayTextField.setBounds(50, 550, 50, 50);

        JLabel rowLengthLabel = new JLabel();
        rowLengthLabel.setText("Line length (letters)");
        rowLengthLabel.setFont(rowLengthLabel.getFont().deriveFont(18f));
        rowLengthLabel.setBounds(900, 475, 500, 50);

        JTextField rowLengthTextField = new JTextField();
        rowLengthTextField.setFont(rowLengthTextField.getFont().deriveFont(24f));
        rowLengthTextField.setHorizontalAlignment(SwingConstants.CENTER);
        rowLengthTextField.setBounds(825, 475, 50, 50);

        JButton subtitleTypeButton = new JButton();
        subtitleTypeButton.setText("Subtitles: Line by line");
        subtitleTypeButton.setFont(subtitleTypeButton.getFont().deriveFont(18f));
        subtitleTypeButton.setBounds(825, 550, 250, 50);

        //vmixTextField.setText("");
        //smTextField.setText("");
        rowLengthTextField.setText("80");
        bufferSizeTextField.setText("8192");
        maxDelayTextField.setText("0.7");

        frame.add(modeSwitchButton);
        frame.add(vmixLabel);
        frame.add(vmixExampleLabel);
        frame.add(smLabel);
        frame.add(smExampleLabel);
        frame.add(vmixTextField);
        frame.add(smTextField);
        frame.add(connectButton);
        frame.add(logsButton);
        frame.add(scrollPane);
        frame.add(audioLabel);
        frame.add(microphoneBox);
        frame.add(bufferSizeLabel);
        frame.add(bufferSizeTextField);
        frame.add(maxDelayLabel);
        frame.add(maxDelayTextField);
        frame.add(rowLengthLabel);
        frame.add(rowLengthTextField);
        frame.add(subtitleTypeButton);

        frame.setVisible(true);

        controller = new Controller(modeSwitchButton, vmixLabel, vmixExampleLabel, vmixTextField, smTextField, connectButton, logsButton, logsRawTextArea, scrollPane, microphoneBox, rowLengthLabel, rowLengthTextField, subtitleTypeButton, bufferSizeTextField, maxDelayTextField);

    }

}