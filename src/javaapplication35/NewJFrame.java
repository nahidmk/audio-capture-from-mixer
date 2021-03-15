package javaapplication35;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;


public class NewJFrame extends javax.swing.JFrame {

    ArrayList<AudInputLine> lines = new ArrayList<>();
    TargetDataLine inputline;
    File audoutput;
    boolean start = false;
    AudioFormat format;
    Mixer.Info[] mixerInfo;
    AudioInputStream ais;
    String filename;
    AudioFileFormat.Type fileformat;
    String[] exts = {"wav", "au", "aiff", "aifc", "snd"};
    private javax.swing.JButton btn_refresh;
    private javax.swing.JButton btn_start;
    private javax.swing.JButton btn_stop;
    private javax.swing.JButton btn_play;
    private javax.swing.JComboBox cmb_bits;
    private javax.swing.JComboBox cmb_file_format;
    private javax.swing.JComboBox cmb_monoORStereo;
    private javax.swing.JComboBox cmb_sample;
    private javax.swing.JComboBox cmb_targetdatalines;
    //The recording thread. Set apart from EDT
    Thread startRec = new Thread() {
        public void run() {
            while (true) {
                while (!start) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                record();
            }
        }
    };
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    public NewJFrame() {

        initComponents();
        LocalDateTime localDateTime = LocalDateTime.now();
        Random random = new Random();
        filename =localDateTime.getDayOfMonth() + "_" + localDateTime.getMonth() + "_" + localDateTime.getHour() + "_" +
                localDateTime.getMinute() + "_" + localDateTime.getSecond() + "_" + localDateTime.getYear() + "_" + random.nextInt(10000);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        RefreshInputs();
        startRec.start();
    }

    public static void main(String[] args) throws Exception {
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Windows".equals(info.getName())) {
                UIManager.setLookAndFeel(info.getClassName());
                break;
            }
        }
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewJFrame().setVisible(true);
            }
        });
    }

    //__________ Refresh Input sources_____________
    public void RefreshInputs() {
        lines.clear();
        mixerInfo = AudioSystem.getMixerInfo();
        Line.Info[] targlines;
//getting all TargetLines from all available mixers
        for (Mixer.Info m : mixerInfo) {
            targlines = AudioSystem.getMixer(m).getTargetLineInfo();
            for (Line.Info ln : targlines) {
                AudInputLine tail = new AudInputLine();
                tail.lineInfo = ln;
                tail.mixer = AudioSystem.getMixer(m);
                tail.name = tail.mixer.getMixerInfo().toString();
                lines.add(tail);
//                System.out.println("Name: "+tail.name);
//                System.out.println("mixer: "+tail.mixer);
//                System.out.println("line info: "+tail.lineInfo);
            }
        }
//removing TargetLines that do not support any AudioFormat
        for (int i = 0; i < lines.size(); i++) {
            try {
                if (((DataLine.Info) lines.get(i).lineInfo).getFormats().length < 1) {
                    lines.remove(i);
                    i -= 1;
                }
            } catch (Exception exx) {
                lines.remove(i);
                i -= 1;
            }
        }
        cmb_targetdatalines.removeAllItems();
        for (AudInputLine dinf : lines) {
//            System.out.println(dinf);
            cmb_targetdatalines.addItem(dinf);
        }
    }

    //__________ Refresh audioformats_____________
    public void RefreshAudioFormats() {
        int[] bits = {24, 16, 8};
        float[] sampling = {8000, 11025, 16000, 22050, 44100, 48000, 96000, 192000};
        AudInputLine taud = ((AudInputLine) cmb_targetdatalines.getSelectedItem());

//populating samplerates combobox
        cmb_sample.removeAllItems();
        for (int i = 0; i < sampling.length; i++) {
            AudioFormat aftemp = new AudioFormat(sampling[i], 8, 1, false, true);
            if (taud.lineInfo instanceof DataLine.Info && ((DataLine.Info) taud.lineInfo).isFormatSupported(aftemp) == true) {
                cmb_sample.addItem(Float.toString(sampling[i]));
                if (sampling[i] == 44100 || sampling[i] == 48000)
                    cmb_sample.setSelectedIndex(i);
            }
        }

//populating sampleBItSize combobox
        cmb_bits.removeAllItems();
        for (int i = 0; i < bits.length; i++) {
            AudioFormat aftemp = new AudioFormat(8000, bits[i], 1, !(bits[i] == 8), true);
            if (taud.lineInfo instanceof DataLine.Info && ((DataLine.Info) taud.lineInfo).isFormatSupported(aftemp) == true)
                cmb_bits.addItem(Integer.toString(bits[i]));
        }

//Populating Channels combobox (mono/stereo)
        AudioFormat aftemp = new AudioFormat(8000, 8, 2, false, true);
        cmb_monoORStereo.removeAllItems();
        if (taud.lineInfo instanceof DataLine.Info && ((DataLine.Info) taud.lineInfo).isFormatSupported(aftemp) == true)
            cmb_monoORStereo.addItem("Stereo");
        cmb_monoORStereo.addItem("Mono");
    }

    //______________Record________________
    public void record() {
        try {
            start = false;
            inputline.open(format);
            inputline.start();
            ais = new AudioInputStream(inputline);
            AudioSystem.write(ais, fileformat, audoutput);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
            buttonsEnable(true);
        }

    }

    //method to handle enabling and disabling UI element when start/stop button are pressed
    public void buttonsEnable(boolean f) {
        cmb_targetdatalines.setEnabled(f);
        cmb_bits.setEnabled(f);
        cmb_file_format.setEnabled(f);
        cmb_monoORStereo.setEnabled(f);
        cmb_sample.setEnabled(f);
        btn_stop.setEnabled(!f);
        btn_start.setEnabled(f);
        btn_play.setEnabled(f);
    }

//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btn_stop = new javax.swing.JButton();
        btn_start = new javax.swing.JButton();
        btn_play = new javax.swing.JButton();
        cmb_targetdatalines = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel6 = new javax.swing.JLabel();
        cmb_file_format = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        btn_refresh = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        cmb_sample = new javax.swing.JComboBox();
        cmb_bits = new javax.swing.JComboBox();
        cmb_monoORStereo = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setAlwaysOnTop(true);

        btn_stop.setText("Stop");
        btn_stop.setEnabled(false);
        btn_stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_stopActionPerformed(evt);
            }
        });

        btn_play.setText("Play");
        btn_play.setEnabled(false);
        btn_play.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_PlayActionPerformed(evt);
            }
        });

        btn_start.setText("Start");
        btn_start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_startActionPerformed(evt);
            }
        });

        cmb_targetdatalines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmb_targetdatalinesActionPerformed(evt);
            }
        });

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Monospaced", 0, 11)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel6.setText("Sample Size in bits");

        cmb_file_format.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"WAVE", "AU", "AIFF", "AIFF-C", "SND"}));

        jLabel7.setText("Select Input Source");

        btn_refresh.setText("Refresh Inputs");
        btn_refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_refreshActionPerformed(evt);
            }
        });

        jLabel2.setText("Sample Rate");

        jLabel1.setText("Mono/Stereo");

        jLabel3.setText("File Format");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(cmb_targetdatalines, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 403, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btn_refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jLabel7)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(cmb_sample, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(cmb_bits, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(btn_start, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGap(10, 10, 10)
                                                                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(btn_stop, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                        .addGap(10, 10, 10)
                                                                                        .addComponent(cmb_monoORStereo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(btn_play, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)

                                                                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 124, Short.MAX_VALUE)
                                                                        .addComponent(cmb_file_format, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmb_targetdatalines, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(btn_refresh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(cmb_sample, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cmb_bits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cmb_monoORStereo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(cmb_file_format, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btn_stop, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                                        .addComponent(btn_play, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE)
                                        .addComponent(btn_start, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(6, 6, 6))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_stopActionPerformed
        inputline.stop();
        inputline.close();
        buttonsEnable(true);
    }//GEN-LAST:event_btn_stopActionPerformed

    private void btn_PlayActionPerformed(java.awt.event.ActionEvent evt) {
        Path parentFolder = Paths.get("Recorder");

        Optional<File> mostRecentFileOrFolder =
                Arrays
                        .stream(Objects.requireNonNull(parentFolder.toFile().listFiles()))
                        .max(
                                (f1, f2) -> Long.compare(f1.lastModified(),
                                        f2.lastModified()));

        if (mostRecentFileOrFolder.isPresent()) {
            File mostRecent = mostRecentFileOrFolder.get();
            play(mostRecent.getPath());
        } else {
            System.out.println("folder is empty!");
        }
    }

    private void play(String filename) {
        try {
            File path = new File(filename);
            if(path.exists())
            {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(path);
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            }
        } catch(Exception ex) {
            System.out.println("Error with playing sound.");
            ex.printStackTrace();
        }
    }

    private void btn_startActionPerformed(java.awt.event.ActionEvent evt) {

        audoutput = new File("Recorder/"+filename + "." + exts[cmb_file_format.getSelectedIndex()]);
        switch (cmb_file_format.getSelectedIndex()) {
            case 0:
                fileformat = AudioFileFormat.Type.WAVE;
                break;
            case 1:
                fileformat = AudioFileFormat.Type.AU;
                break;
            case 2:
                fileformat = AudioFileFormat.Type.AIFF;
                break;
            case 3:
                fileformat = AudioFileFormat.Type.AIFC;
                break;
            case 4:
                fileformat = AudioFileFormat.Type.SND;
                break;
        }
        AudInputLine tau = (AudInputLine) cmb_targetdatalines.getSelectedItem();
        format = new AudioFormat(Float.parseFloat((String) cmb_sample.getSelectedItem()), Integer.parseInt((String) cmb_bits.getSelectedItem()), (cmb_monoORStereo.getSelectedIndex() + 1) % 2 + 1, !(Integer.parseInt((String) cmb_bits.getSelectedItem()) == 8), true);
        AudInputLine taud = ((AudInputLine) cmb_targetdatalines.getSelectedItem());
        if (taud.lineInfo instanceof DataLine.Info && ((DataLine.Info) taud.lineInfo).isFormatSupported(format) == false) {
            format = new AudioFormat(Float.parseFloat((String) cmb_sample.getSelectedItem()), Integer.parseInt((String) cmb_bits.getSelectedItem()), (cmb_monoORStereo.getSelectedIndex() + 1) % 2 + 1, !(Integer.parseInt((String) cmb_bits.getSelectedItem()) == 8), false);
            System.out.println("hello i am there");
        }
        try {
//            System.out.println("format : "+format);
            System.out.println("mixer info: "+tau.mixer.getMixerInfo());
            inputline = AudioSystem.getTargetDataLine(format, tau.mixer.getMixerInfo());
        } catch (LineUnavailableException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
        buttonsEnable(false);
        start = true;
    }

    private void cmb_targetdatalinesActionPerformed(java.awt.event.ActionEvent evt) {
        if (cmb_targetdatalines.getItemCount() > 0) {
            AudInputLine tau = (AudInputLine) cmb_targetdatalines.getSelectedItem();
            jTextArea1.setText(tau.mixer.getMixerInfo().toString() + ".\n" + tau.lineInfo.toString());
            RefreshAudioFormats();
        }
    }

    private void btn_refreshActionPerformed(java.awt.event.ActionEvent evt) {
        cmb_targetdatalines.removeAllItems();
        RefreshInputs();
    }

}

class AudInputLine {
    public Mixer mixer;
    public Line.Info lineInfo;
    public String name;

    public String toString() {
        return name;
    }
}
