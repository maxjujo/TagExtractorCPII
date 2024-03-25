import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class TagExtractorGUI extends JFrame {
    private JTextArea tagTextArea;
    private JFileChooser fileChooser;
    private File stopWordFile;
    private File selectedFile;

    public TagExtractorGUI() {
        setTitle("Tag Extractor");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        tagTextArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(tagTextArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton openButton = new JButton("Open File");
        JButton stopWordButton = new JButton("Select Stop Word File");
        JButton extractButton = new JButton("Extract Tags");
        JButton saveButton = new JButton("Save Tags");

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        stopWordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectStopWordFile();
            }
        });

        extractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                extractTags();
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveTags();
            }
        });

        buttonPanel.add(openButton);
        buttonPanel.add(stopWordButton);
        buttonPanel.add(extractButton);
        buttonPanel.add(saveButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void openFile() {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose a File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.addChoosableFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();
            tagTextArea.append("Selected File: " + selectedFile.getName() + "\n");
        }
    }

    private void selectStopWordFile() {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Choose Stop Word File");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files", "txt");
        fileChooser.addChoosableFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            stopWordFile = fileChooser.getSelectedFile();
            tagTextArea.append("Stop Word File: " + stopWordFile.getName() + "\n");
        }
    }

    private void extractTags() {
        if (selectedFile == null || stopWordFile == null) {
            JOptionPane.showMessageDialog(this, "Please select a file and a stop word file.");
            return;
        }

        Map<String, Integer> wordFrequencyMap = new TreeMap<>();
        Set<String> stopWords = loadStopWords(stopWordFile);

        try (Scanner scanner = new Scanner(selectedFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().toLowerCase();
                String[] words = line.split("[^a-zA-Z']+");

                for (String word : words) {
                    if (!word.isEmpty() && !stopWords.contains(word)) {
                        wordFrequencyMap.put(word, wordFrequencyMap.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        tagTextArea.append("\nTags and their Frequencies:\n");
        for (Map.Entry<String, Integer> entry : wordFrequencyMap.entrySet()) {
            tagTextArea.append(entry.getKey() + ": " + entry.getValue() + "\n");
        }
    }

    private Set<String> loadStopWords(File stopWordFile) {
        Set<String> stopWords = new TreeSet<>();
        try (Scanner scanner = new Scanner(stopWordFile)) {
            while (scanner.hasNextLine()) {
                String stopWord = scanner.nextLine().toLowerCase();
                stopWords.add(stopWord);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stopWords;
    }

    private void saveTags() {
        if (tagTextArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No tags to save.");
            return;
        }

        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Tags to File");
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(fileToSave)) {
                writer.println(tagTextArea.getText());
                JOptionPane.showMessageDialog(this, "Tags saved successfully.");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new TagExtractorGUI().setVisible(true);
            }
        });
    }
}
