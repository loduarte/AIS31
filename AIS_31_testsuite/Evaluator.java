import java.io.*;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.text.DecimalFormat;
import java.lang.*;



public class Evaluator extends JFrame implements ActionListener,ItemListener{
    Tester tester;
    final int elementzahl = 256*65536;
    static boolean geschwaetzig = true;
    static boolean byteformat = true;
    static byte testart = 1;
    static boolean normalertest = true;
    static int fortschrittzahl = 0;
    static int bitbreite = 0;
    static int bitzahl = 0;
    public static int c = 0;
    public static int letzterwert = 0;
    boolean zuwenigdaten = false;
    boolean abbruch = false;
    boolean skip = false;
    boolean dateigewaehlt = false;
    int ultimoelemento = -1;
    FileInputStream datei;
    DataInputStream bitdatei;
    String progname = new String("AIS 31 (V1) Referencia de implementacao - v1.0 ");
    String dateiname = new String("");
    File logfile;
    FileWriter logwriter;
    BufferedWriter logbuffer;
    PrintWriter log;
    private JRadioButton[] rb = new JRadioButton[9];
    private JLabel configlabel1 = new JLabel("Predefinido:");
    private JLabel vorgangslabel4 = new JLabel("");
    private JLabel configtest = new JLabel("TRNG-Klasse:");
    private JLabel configausgabe = new JLabel("Saida:");
    private JLabel configformat = new JLabel("Formato de dados:");
    private JLabel vorgangsname = new JLabel("Progresso:");
    private JLabel configtestart = new JLabel("Teste:");
    private JLabel zahlenbreite = new JLabel("Numeros aleatorios internos:");
    public JTextField dateieingabe = new JTextField("Neste campo de nomes de arquivos em estilo \"/Users/lod/testData\"",40);
    public JTextField bitbreiteeingabe = new JTextField("256",7);
    private JProgressBar fortschritt = new JProgressBar(0,300);
    private JButton dateisuchen = new JButton("arquivo de pesquisa");
    private JButton hilfe = new JButton("Mostrar ajuda");
    private JButton start5 = new JButton("I N I C I A R");
    private JScrollPane textpanel7;
    private JButton vorgangabbruch = new JButton("Abortar");
    private JTextArea textbox = new JTextArea(5,50);
    boolean logfileenabled = true;



    public Evaluator(){
        super("AIS 31 (V1) Implementacao de referencia do STAT. Teste - v1.0 [Kein Job]");
        setBounds(100,100,700,550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        try{
            logfile = new File("AIS 31.log");
            logwriter = new FileWriter(logfile);
            logbuffer = new BufferedWriter(logwriter);
            log = new PrintWriter(logbuffer);
        } catch (IOException e){
            logfileenabled = false;
            kommentar("Falha ao criar AIS 31.log: " + e.toString(),true,0);
        }
        JPanel hauptpanel = new JPanel();
        JPanel dateipanel2 = new JPanel();
        JPanel configpanel3 = new JPanel();
        JPanel vorgangspanel6 = new JPanel();
        rb[0] = new JRadioButton("P1: T1 - T5", true);
        rb[0].setToolTipText("TNRG-Klasse P1, Teste T1 - T5 selecionar");
        rb[1] = new JRadioButton("P2 - especifico", false);
        rb[1].setToolTipText("TNRG-Klasse P2 (testes especificos) selecionar");
        rb[2] = new JRadioButton("Normal", false);
        rb[2].setToolTipText("Ausgabe ohne Details (Testdaten, Debug-Infos, etc ...) selecionar");
        rb[3] = new JRadioButton("Detalhado", true);
        rb[3].setToolTipText("Ausgabe mit Details (Testdaten, Debug-Infos, etc ...) selecionar");
        rb[4] = new JRadioButton("1Byte = 1RNDBit", true);
        rb[4].setToolTipText("In 1 Dateibyte befindet sich genau 1 Zufallsbit");
        rb[5] = new JRadioButton("1Byte = 8RNDBit", false);
        rb[5].setToolTipText("Existem exatamente 8 bits aleatorios em 1 byte de arquivo, o bit mais a esquerda eh o mais significativo");
        rb[6] = new JRadioButton("Teste Normal", true);
        rb[6].setToolTipText("TNRG unterzieht sich normalem Test, keinem Wiederholungstest");
        rb[7] = new JRadioButton("Repeticao", false);
        rb[7].setToolTipText("TNRG wird Wiederholungstest unterzogen, da 1. Teste gescheitert");
        rb[8] = new JRadioButton("P1: T0", false);
        rb[8].setToolTipText("TNRG-Klasse P1, Teste T0 selecionar");
        fortschritt.setStringPainted(true);
        vorgangabbruch.setBackground(Color.red);
        vorgangabbruch.setToolTipText("Durante conjuntos de testes demorados (P0/T1-T5), o teste pode ser abortado com este botao.");
        start5.setToolTipText("Apos inserir os dados, clique aqui para iniciar o teste");
        dateisuchen.addActionListener(this);
        dateisuchen.setEnabled(false);
        dateisuchen.setToolTipText("Den Windows-Dateisuchen-Dialog ???ffnen (NOCH NICHT VOLLST???NDIG IMPLEMENTIERT, bitte Dateinamen von Hand eingeben)");
        start5.setBackground(Color.green);
        start5.addActionListener(this);
        vorgangabbruch.addActionListener(this);
        hilfe.addActionListener(this);
        hilfe.setToolTipText("Mostrar texto/instrucoes de ajuda na janela de registro");
        hilfe.setBackground(Color.yellow);
        for (int i = 0; i < rb.length; i++){
            rb[i].addItemListener(this);
        }
        GridBagLayout gblhaupt = new GridBagLayout();
        GridBagLayout gbl2 = new GridBagLayout();
        GridBagLayout gbl6 = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        dateipanel2.setLayout(gbl2);
        configpanel3.setLayout(new GridLayout(3,5));
        vorgangspanel6.setLayout(gbl6);
        hauptpanel.setLayout(gblhaupt);
        buildConstraints(gbc,0,0,1,1,1000,100);
        gbl2.setConstraints(dateieingabe,gbc);
        dateipanel2.add(dateieingabe);
        buildConstraints(gbc,1,0,2,1,10,100);
        gbl2.setConstraints(dateisuchen,gbc);
        configpanel3.add(rb[8]);
        configpanel3.add(configausgabe);
        configpanel3.add(configformat);
        configpanel3.add(configtestart);
        configpanel3.add(zahlenbreite);
        configpanel3.add(rb[0]);
        configpanel3.add(rb[2]);
        configpanel3.add(rb[4]);
        configpanel3.add(rb[6]);
        bitbreiteeingabe.setToolTipText("Largura dos n??meros aleatorios internos em bits");
        configpanel3.add(bitbreiteeingabe);
        configpanel3.add(rb[1]);
        configpanel3.add(rb[3]);
        configpanel3.add(rb[5]);
        configpanel3.add(rb[7]);
        configpanel3.add(hilfe);
        buildConstraints(gbc,0,0,1,1,10,100);
        gbl6.setConstraints(vorgangsname, gbc);
        vorgangspanel6.add(vorgangsname);
        buildConstraints(gbc,1,0,2,1,70,100);
        gbl6.setConstraints(fortschritt, gbc);
        vorgangspanel6.add(fortschritt);
        buildConstraints(gbc,3,0,1,1,10,100);
        fortschritt.setToolTipText("Durante o teste, um grafico mostra quantos % jah foram testados");
        textbox.setToolTipText("Todas as mensagens do programa sao escritas aqui em ordem cronologica.");
        gbl6.setConstraints(vorgangabbruch, gbc);
        vorgangspanel6.add(vorgangabbruch);
        textpanel7 = new JScrollPane(textbox);
        textbox.append(progname + "\nPrograma iniciado em " + systemdatum() + " as " + systemzeit() + "\n\nABORDAGEM EM TRES ETAPAS PARA PROBLEMAS:\n     1) Segure o botao do mouse sobre o objeto em questao, a ajuda aparecera\n     2) Se o problema nao for resolvido, pressione o botao de ajuda\n     3) Se necessario: Envie consultas tecnicas para zertigung@bsi.bund.de");
        ButtonGroup rbgrpsicherheit = new ButtonGroup();
        ButtonGroup rbgrpausgabe = new ButtonGroup();
        ButtonGroup rbgrpformat = new ButtonGroup();
        ButtonGroup rbgrpwiederholung = new ButtonGroup();
        rbgrpsicherheit.add(rb[0]);
        rbgrpsicherheit.add(rb[1]);
        rbgrpsicherheit.add(rb[8]);
        rbgrpausgabe.add(rb[2]);
        rbgrpausgabe.add(rb[3]);
        rbgrpformat.add(rb[4]);
        rbgrpformat.add(rb[5]);
        rbgrpwiederholung.add(rb[6]);
        rbgrpwiederholung.add(rb[7]);
        buildConstraints(gbc,0,0,1,1,100,5);
        gblhaupt.setConstraints(configlabel1,gbc);
        hauptpanel.add(configlabel1);
        buildConstraints(gbc,0,1,1,1,100,5);
        gblhaupt.setConstraints(dateipanel2,gbc);
        hauptpanel.add(dateipanel2);
        buildConstraints(gbc,0,2,1,1,100,20);
        gblhaupt.setConstraints(configpanel3,gbc);
        hauptpanel.add(configpanel3);
        buildConstraints(gbc,0,3,1,1,100,5);
        gblhaupt.setConstraints(vorgangslabel4,gbc);
        hauptpanel.add(vorgangslabel4);
        buildConstraints(gbc,0,4,1,1,100,200);
        gblhaupt.setConstraints(start5,gbc);
        hauptpanel.add(start5);
        buildConstraints(gbc,0,5,1,1,100,15);
        gblhaupt.setConstraints(vorgangspanel6,gbc);
        hauptpanel.add(vorgangspanel6);
        buildConstraints(gbc,0,6,1,1,100,3000);
        gblhaupt.setConstraints(textpanel7,gbc);
        hauptpanel.add(textpanel7);
        setContentPane(hauptpanel);
        elementeAnpassen(0);
        fortschritt.setValue(fortschrittzahl);
        textbox.setEditable(false);
        setVisible(true);
    }



    public void setzefortschritt(int schritt){
        fortschritt.setValue(schritt);
    }





    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src instanceof JButton) {
            if (src == dateisuchen){
                JFileChooser dateidialog = new JFileChooser();
                dateidialog.setDialogTitle("Datei suchen");
                if (dateidialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    dateieingabe.setText(dateidialog.getSelectedFile().getPath());
                }
            }
            if (src == start5){
                kommentar("\n\nINICIO DO TESTE",true,0);
                tester = new Tester(this);
                tester.start();
            }
            if (src == vorgangabbruch){
                skip = true;
                abbruch = true;
            }
            if (src == hilfe){
                kommentar("Hilfe wird eingeblendet - Log wird anschliessend fortgef???hrt.", true,0);
                textbox.append("\n\n\nHILFE von " + progname +
                "\n\nDieses Programm f???hrt die in der mathematisch-technischen Anlage \nzur AIS 31 spezifizierten statistischen Tests durch. \nDie Hilfetexte sollen die Handhabung des Programms erleichtern. \nF???r das Verst???ndnis der Zusammenh???nge wird auf die mathematisch-technischen \nAnlage zur AIS 31 verwiesen.\n" +
                "\nAnleitung:\n" +
                "   1) Geben Sie die Zufallszahlendatei mit vollst???ndigem Pfad ein.\n" +
                "   2) W???hlen Sie jeweils einen Eintrag aus den Spalten \"Tests\", \"Ausgabe\", \"Datenformat\" und \"Testart\".\n" +
                "   3) Geben Sie die Breite der internen Zufallszahlen in Bit an.\n" +
                "   4) Dr???cken Sie den Start-Button. Abh???ngig von Ihrem Rechner kann das Programm mehrere Minuten ben???tigen.  Der Laufzeitbalken zeigt den Fortschritt an.\n" +
                "\nTest-Suiten:\n" +
                "   P1: T0: Vgl. AIS 31, P1.i.(i) und F. Statistische Tests, Teste T0\n" +
                "   P1: T1-T5: Vgl. AIS 31, P1.i.(ii-ii.c) und F. Statistische Tests, Tests T1-T5\n" +
                "   P2 - spezifisch: P2 Spezifische Tests, Vgl. AIS 31, P2.i(vii.a-vii.e) und F. Statistische Tests, Tests T6-T8; (vii.a) ~ T6a, (vii.b) ~ T6b, (vii.c) ~ T7a, (vii.d) ~ T7b, (vii.e) ~ T8\n" +
                "\nEingabe- / Dateiformat:\n" +
                "   1Byte=1RNDBit: Jedes Byte der Eingabedatei entspricht einem Zufallsbit.\n" +
                "   1Byte=8RNDBit: Jedes Byte der Eingabedatei entspricht 8 Zufallsbits,\n" +
                "   wobei das h???chstwertige Bit als ???ltestes interpretiert wird.\n\n" +
                "Testart:\n" +
                "   Normal = Erste Durchf???hrung des entsprechenden Tests.\n" +
                "   Wiederholung: Zweite Durchf???hrung des entsprechenden Tests.\n\n" +
                "Interne Zufallszahlen:\n" +
                "   Breite der vom Zufallszahlengenerator erzeugten internen\n" +
                "   Zufallszahlen in Bit (vgl. AIS 31, B.1 Definitionen)\n\n" +
                "Tasten:\n" +
                "   Starttaste (Gr???n): Starten Sie die Tests, nachdem Sie alle Einstellungen gew???hlt haben.\n" +
                "   Hilfe-Taste (Gelb): Schreibt den vollst???ndigen Hilfetext ins Textfenster.\n" +
                "\nDiejenigen Zufallszahlen der Eingabedatei, die zur Durchf???hrung einer Testsuite \n(T0, T1-T5 oder P2-testes especificos) nicht ben???tigt werden,werden in eine neue \nDatei geschrieben. Der Dateiname ergibt sich aus der Eingabedatei durch Anh???ngen von \n\"_rest\". Nach Beendigung der Testsuite wird diese Datei defaultm??????ig in die \nEingabezeile eingetragen.\n\n\n"
                );

                kommentar("Hilfe beendet - Log wird fortgef???hrt.", true,0);
            }
        }
    }


    static String systemzeit(){
        String zahlenformat = new String("00");
        DecimalFormat df = new DecimalFormat(zahlenformat);
        GregorianCalendar cal = new GregorianCalendar();
        String zeit = new String(df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE)) + ":" + df.format(cal.get(Calendar.SECOND)));
        return zeit;
    }


    static String systemdatum(){
        GregorianCalendar cal = new GregorianCalendar();
        String zahlenformat = new String("00");
        DecimalFormat df = new DecimalFormat(zahlenformat);
        String datum = new String(df.format(cal.get(Calendar.DAY_OF_MONTH)) + "." + df.format((cal.get(Calendar.MONTH)+1)) + "." + df.format(cal.get(Calendar.YEAR)));
        return datum;
    }


    public void itemStateChanged(ItemEvent evt) {
        GregorianCalendar cal = new GregorianCalendar();
        Object src = evt.getSource();
        int srcnum = 0;
        for(int i=0;i<8;i++){
            if (src == rb[i]){
                srcnum = i;
            }
        }
        switch (srcnum) {
            case 8:
            case 0:
            case 1:
                if(rb[0].isSelected()) {
                    testart = 1;
                }
                if(rb[1].isSelected()) {
                    testart = 2;
                }
                if(rb[8].isSelected()) {
                    testart = 0;
                }
                break;
            case 2:
                geschwaetzig = !(rb[2].isSelected());
                if (geschwaetzig){
                }
                else {
                }
                break;
            case 4:
                byteformat = (rb[4].isSelected());
                if (byteformat){
                }
                else {
                }
                break;
            case 6:
                normalertest = (rb[6].isSelected());
                if (normalertest){
                }
                else{
                }
                break;
        }
    }


    public void elementeAnpassen(int status) {
        switch (status){
            case 0:
                setTitle(progname + " - [Kein Job]");
                for (int i = 0; i<rb.length; i++){
                    rb[i].setEnabled(true);
                }
                configtest.setEnabled(true);
                configtestart.setEnabled(true);
                configlabel1.setEnabled(true);
                configausgabe.setEnabled(true);
                configformat.setEnabled(true);
                vorgangsname.setEnabled(false);
                dateieingabe.setEnabled(true);
                fortschritt.setEnabled(false);
                start5.setEnabled(true);
                vorgangabbruch.setEnabled(false);
                textbox.setEnabled(true);
                zahlenbreite.setEnabled(true);
                bitbreiteeingabe.setEnabled(true);
                hilfe.setEnabled(true);
                break;
            case 1:
                setTitle(progname + " - [Lese Datei]");
                for (int i = 0; i<rb.length; i++){
                    rb[i].setEnabled(false);
                }
                configtest.setEnabled(false);
                configausgabe.setEnabled(false);
                configtestart.setEnabled(false);
                configlabel1.setEnabled(false);
                configformat.setEnabled(false);
                vorgangsname.setEnabled(true);
                dateieingabe.setEnabled(false);
                fortschritt.setEnabled(true);
                dateisuchen.setEnabled(false);
                start5.setEnabled(false);
                vorgangabbruch.setEnabled(true);
                textbox.setEnabled(true);
                bitbreiteeingabe.setEnabled(false);
                zahlenbreite.setEnabled(false);
                hilfe.setEnabled(false);
                break;
            case 2:
                setTitle(progname + " - [Pr???fung l???uft]");
                for (int i = 0; i<rb.length; i++){
                    rb[i].setEnabled(false);
                }
                configtest.setEnabled(false);
                configausgabe.setEnabled(false);
                configformat.setEnabled(false);
                configtestart.setEnabled(false);
                configlabel1.setEnabled(false);
                vorgangsname.setEnabled(true);
                dateieingabe.setEnabled(false);
                fortschritt.setEnabled(true);
                dateisuchen.setEnabled(false);
                start5.setEnabled(false);
                vorgangabbruch.setEnabled(true);
                textbox.setEnabled(true);
                zahlenbreite.setEnabled(false);
                bitbreiteeingabe.setEnabled(false);
                hilfe.setEnabled(false);
                break;
        }
    }



    public void kommentar(String text, boolean wichtig, int ebene) {
        if ((wichtig) | (geschwaetzig)){
            textbox.append("\n[" + systemzeit() + "] ");
            if (ebene > 0){
                for (int i = 0; i< ebene; i++){
                    textbox.append("  ");
                }
            }
            textbox.append(text);
            textbox.setCaretPosition(textbox.getText().length());
            if (logfileenabled){
                log.print("[" + systemzeit() + "] ");
                if (ebene > 0){
                    for (int i = 0; i< ebene; i++){
                        log.print("  ");
                    }
                }
                log.println(text);
            }
        }
    }



    void buildConstraints(GridBagConstraints gbc, int gx, int gy, int gw, int gh, int wx, int wy) {
        gbc.gridx = gx;
        gbc.gridy = gy;
        gbc.gridwidth = gw;
        gbc.gridheight = gh;
        gbc.weightx = wx;
        gbc.weighty = wy;
        gbc.fill = GridBagConstraints.BOTH;
    }



    static void look(){
        try {
            UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e1) {
        }
    }



    public static void main(String[] arguments) {
        try {
            UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e1){
        }
        Evaluator evaluator = new Evaluator();
    }




}

class Tester extends Thread{

    Evaluator evaluator;
    final int elementzahl = 256*65536;
    public byte[] BitFeldA = new byte[256*65536];
    public byte[] BitFeldB = new byte[20000];
    static boolean geschwaetzig;
    static boolean byteformat;
    static byte testart;
    static boolean normalertest;
    static int fortschrittzahlt;
    static int bitbreite;
    static int bitzahl;
    static int c;
    int letzterwert;
    boolean zuwenigdaten = false;
    boolean abbruch = false;
    boolean skip = false;
    boolean dateigewaehlt;
    int ultimoelemento;
    FileInputStream datei;
    DataInputStream bitdatei;
    String progname;


    Tester(Evaluator eva){
        setPriority(NORM_PRIORITY);
        evaluator = eva;
        geschwaetzig = evaluator.geschwaetzig;
        byteformat = evaluator.byteformat;
        testart = evaluator.testart;
        normalertest = evaluator.normalertest;
        bitbreite = evaluator.bitbreite;
        bitzahl = evaluator.bitzahl;
        c = evaluator.c;
        letzterwert = evaluator.letzterwert;
        dateigewaehlt = evaluator.dateigewaehlt;
        ultimoelemento = evaluator.ultimoelemento;
        progname = evaluator.progname;

    }

    public boolean einlesen(byte[] BitFeldA) {

        String dateiname = evaluator.dateieingabe.getText();
        boolean eof = false;
        int anzahl = 0;
        boolean fehler = false;
        if (testart == 0){
            c = teilenaufrunden(48,bitbreite);
            bitzahl = bitbreite * c * 65536;
        }
        if (testart == 1){
            bitzahl = teilenaufrunden(257,(bitbreite + 1)) * ((teilenaufrunden(20000, bitbreite)) * bitbreite + (bitbreite * 20000));
        }
        if (testart == 2){
            bitzahl = 7200000;
        }
        try {
            datei = new FileInputStream(dateiname);
            if (!byteformat){
                int[] tempfeld = new int[teilenaufrunden(bitzahl,8) + 1];
                bitdatei = new DataInputStream(datei);
                evaluator.kommentar("Copiando ByteStream para RAM...",false,2);
                while ((!eof) && (anzahl < (teilenaufrunden(bitzahl,8)))){
                    try {
                        tempfeld[anzahl] = bitdatei.readUnsignedByte();
                    } catch (EOFException e2){
                        eof = true;
                        evaluator.kommentar("Dateifehler: " + e2.toString(), true,2);

                    }
                    anzahl++;
                }
                anzahl = anzahl * 8;
                for (int i = 0; i < BitFeldA.length; i++){
                    BitFeldA[i] = 0;
                }
                evaluator.kommentar("Converter dados de arquivo em ByteStream ...",false,2);
                for (int i = 0; i < (teilenaufrunden(anzahl,8)); i++){
                    if (tempfeld[i] > 127){
                        BitFeldA[(i*8)+0] = 1;
                        tempfeld[i] = tempfeld[i] - 128;
                    }
                    if (tempfeld[i] > 63){
                        BitFeldA[(i*8)+1] = 1;
                        tempfeld[i] = tempfeld[i] - 64;
                    }
                    if (tempfeld[i] > 31){
                        BitFeldA[(i*8)+2] = 1;
                        tempfeld[i] = tempfeld[i] - 32;
                    }
                    if (tempfeld[i] > 15){
                        BitFeldA[(i*8)+3] = 1;
                        tempfeld[i] = tempfeld[i] - 16;
                    }
                    if (tempfeld[i] > 7){
                        BitFeldA[(i*8)+4] = 1;
                        tempfeld[i] = tempfeld[i] - 8;
                    }
                    if (tempfeld[i] > 3){
                        BitFeldA[(i*8)+5] = 1;
                        tempfeld[i] = tempfeld[i] - 4;
                    }
                    if (tempfeld[i] > 1){
                        BitFeldA[(i*8)+6] = 1;
                        tempfeld[i] = tempfeld[i] - 2;
                    }
                    if (tempfeld[i] > 0){
                        BitFeldA[(i*8)+7] = 1;
                    }
                }
            }
            else{
                evaluator.kommentar("Copiar ByteStream para RAM ...",false,2);
                anzahl = datei.read(BitFeldA, 0, bitzahl);
            }
        } catch (IOException e) {
            if (!dateigewaehlt){
                evaluator.kommentar("Erro de arquivo: Nenhum arquivo selecionado!",true,2);
                return false;
            }
            else{
                evaluator.kommentar("Erro de arquivo: " + e.toString(), true,2);
                return false;
            }
        }
        try {
            int filepos = 0;
            evaluator.setzefortschritt(21);
            evaluator.kommentar("Gravando arquivo residual: " + dateiname + "_rest",true,2);
            FileInputStream datei = new FileInputStream(dateiname);
            FileOutputStream datei2 = new FileOutputStream(dateiname + "_rest");
            if (byteformat){
                eof = false;
                while (!eof){
                    filepos++;
                    int elementtemp;
                    elementtemp = datei.read();
                    if (elementtemp == -1){
                        eof = true;
                    } else {
                        if (filepos > teilenaufrunden(bitzahl,8)){
                            datei2.write(elementtemp);
                        }
                    }
                }
            } else {
                eof = false;
                while (!eof){
                    filepos++;
                    int elementtemp;
                    elementtemp = datei.read();
                    if (elementtemp == -1){
                        eof = true;
                    } else {
                        if (filepos > (bitzahl/8)){
                            datei2.write(elementtemp);
                        }
                    }
                }
            }
            datei.close();
            datei2.close();
        } catch (IOException e) {
            evaluator.kommentar("Dateifehler: " + e.toString(), true,2);
        }
        if (anzahl < bitzahl) {
            evaluator.kommentar("Dateifehler: Datei zu klein. Mindestgr??????e " + bitzahl + " Bits. Gr??????e: " + anzahl + " Bits.", true,2);
            return false;
        }
        evaluator.kommentar(anzahl + " Itens copiados para RAM.",false,2);
        if (byteformat){
            evaluator.kommentar("Verificar dados ...",false,2);
            for (int i = 0; i<bitzahl; i++){
                if ((BitFeldA[i] != 1) & (BitFeldA[i] != 0)){
                    evaluator.kommentar("Elemento Nr. " + (i) + " nao eh igual a \"0\" ou \"1\", mas \"" + BitFeldA[i] + "\" - Verificacao de dados reprovada.",true,2);
                    return false;
                }
            }
        }
        return true;
    }



    void neuesfeldb(byte[] BitFeldA, byte[] BitFeldB, int durchlauf){
        int grundelement = 0;

        if (durchlauf % (bitbreite+1) == 0){
            evaluator.kommentar("Teste no Bloco.",false,1);
            for (int i = 0; i < 20000; i++){
                BitFeldB[i] = BitFeldA[i + ultimoelemento + 1];
            }
            ultimoelemento += teilenaufrunden(20000,bitbreite)*bitbreite;
        }
        else{
            grundelement = (durchlauf % (bitbreite + 1)) + ultimoelemento;
            for(int i= 0; i<20000; i++) {
                BitFeldB[i] = BitFeldA[grundelement + (i * bitbreite)];
            }
            if (durchlauf % (bitbreite+1) == bitbreite) {
                ultimoelemento += 20000*bitbreite;
            }

        }
    }

    int teilenaufrunden(int a, int b){
        if ((a % b) == 0){
            return (int)(a/b);
        }
        else {
            return (((int)(a/b)) + 1);
        }
    }


    public void run(){
        if (test()) {
            if (evaluator.testart == 0 | evaluator.testart == 1){
                evaluator.kommentar("Execucao concluida com sucesso, arquivo residual selecionado para proximo teste.",true,0);
            }
            else {
                evaluator.kommentar("Execucao concluida coam sucesso.",true,0);
            }
        }
        else {
            evaluator.kommentar("Os testes n??o foram (completamente) realizados OU FALHARAM.\nAl??m de uma rescis??o do usu??rio, isso pode ter as seguintes causas:\n1) Um teste falhou (formato de arquivo correto selecionado?)\n2) Erro do usu??rio (por exemplo, largura interna do n??mero aleat??rio n??o inserida)\n3) Erros de arquivo (arquivo muito pequeno, n??o existe, etc)\nPor favor, procure no LOG para encontrar o erro!",true,0);
        }
        try{
            evaluator.logbuffer.flush();
        } catch (IOException e){
            evaluator.kommentar("Falha ao armazenar dados no arquivo AIS 31.log: " + e.toString(),true,0);
        }
    }

    boolean test(){
        evaluator.setzefortschritt(0);
        boolean fehler = false;
        String vergleich = new String("Breite in Bit");
        String vergleichnull = new String("");
        if (vergleich.equals(evaluator.bitbreiteeingabe.getText()) || vergleichnull.equals(evaluator.bitbreiteeingabe.getText())){
            evaluator.kommentar("Bitte Bitbreite der internen Zufallszahlen eingeben!",true,0);
            return false;
        }
        bitbreite = Integer.parseInt(evaluator.bitbreiteeingabe.getText());
        if ((bitbreite > 256) || (bitbreite < 1)){
            evaluator.kommentar("Bitbreite der internen Zufallszahlen nicht im zugelassenen Intervall [1;256]",true,0);
            return false;
        }
        if (testart == 0){
            evaluator.kommentar("CONJUNTO DE TESTE:\tP1/T0",true,0);
        }
        if (testart == 1){
            evaluator.kommentar("CONJUNTO DE TESTE:\tP1/T1-T5",true,0);
        }
        if (testart == 2){
            evaluator.kommentar("CONJUNTO DE TESTE:\tP2 (testes especificos)",true,0);
        }
        evaluator.kommentar("NOME DO ARQUIVO:\t" + evaluator.dateieingabe.getText(),true,0);
        if (geschwaetzig){
            evaluator.kommentar("SAIDA DETALHADA:\tLigada.",true,0);
        }else{
            evaluator.kommentar("SAIDA DETALHADA:\tDesligada.",true,0);
        }
        if (byteformat){
            evaluator.kommentar("FORMATO DE DADOS:\t1 byte de arquivo possui 1 Random-Bit.",true,0);
        }else{
            evaluator.kommentar("FORMATO DE DADOS:\t1 byte de arquivo possui 8 Random-Bits.",true,0);
        }
        if (normalertest){
            evaluator.kommentar("TIPO DE TESTE:\tTeste Normal.",true,0);
        }else{
            evaluator.kommentar("TIPO DE TESTE:\tRepeticao.",true,0);
        }
        evaluator.kommentar("RND BITWIDTH:\t" + bitbreite + " bit.",true,0);
        String[] testnamen = new String[12];
        testnamen[0] = "Iniciar teste T0 (teste de disjuncao); Criterio P1.i(i)";
        testnamen[1] = "Iniciar teste T1 (teste monobit); Criterio P1.i(ii)";
        testnamen[2] = "Iniciar teste T2 (teste de poquer); Criterio P1.i(ii)";
        testnamen[3] = "Iniciar teste T3 (Runtest); Criterio P1.i(ii)";
        testnamen[4] = "Iniciar teste T4 (Long Runtest);  Criterio P1.i(ii)";
        testnamen[5] = "Iniciar teste T5 (teste de autoorrelacao);  Criterio P1.i(ii)";
        evaluator.kommentar("O arquivo esta sendo lido.",true,1);
        evaluator.elementeAnpassen(1);
        if (einlesen(BitFeldA)) {
            evaluator.kommentar("O arquivo foi lido.",false,1);
            evaluator.setzefortschritt(42);
        }
        else {
            evaluator.kommentar("A leitura do arquivo falhou.",true,1);
            evaluator.elementeAnpassen(0);
            evaluator.setzefortschritt(0);
            return false;
        }
        evaluator.elementeAnpassen(2);
        if (testart == 0){
            evaluator.kommentar(testnamen[0],true,1);
            if (test0(c,bitbreite)) {
                evaluator.kommentar("Teste T0 aprovado.",false,1);
                evaluator.setzefortschritt(300);
                evaluator.dateieingabe.setText(evaluator.dateieingabe.getText() + "_rest");
            }
            else {
                evaluator.kommentar("Teste T0 (Teste de Disjuncao); O criterio P1.i(i) falhou.",true,1);
                fehler = true;
            }
        }
        if (testart == 1){
            for (int durchlauf = 0; durchlauf < 257; durchlauf++){
                if (evaluator.abbruch){
                    evaluator.kommentar("Auf Wunsch des Users ABGEBROCHEN.",true,0);
                    evaluator.elementeAnpassen(0);
                    evaluator.abbruch = false;
                    return false;
                }
                boolean durchlauffehler = false;
                evaluator.kommentar("Inicio da execucao " + (durchlauf+1) + " de 257.",true,1);
                neuesfeldb(BitFeldA,BitFeldB,durchlauf);
                evaluator.kommentar(testnamen[1],false,2);
                if (test1()) {
                    evaluator.kommentar("Teste T1 aprovado.",false,2);
                }
                else {
                    evaluator.kommentar("Teste T1 (teste monobit);  Criterio P1.i(ii) reprovado.",true,2);
                    durchlauffehler = true;
                }
                evaluator.kommentar(testnamen[2],false,2);
                if (test2()) {
                    evaluator.kommentar("Teste T2 aprovado.",false,2);
                }
                else {
                    evaluator.kommentar("Teste T2 (teste de poker);   Criterio P1.i(ii) reprovado.",true,2);
                    durchlauffehler = true;
                }
                evaluator.kommentar(testnamen[3],false,2);
                if (test3()) {
                    evaluator.kommentar("Teste T3 aprovado.",false,2);
                }
                else {
                    evaluator.kommentar("Teste T3 (Runtest);   Criterio P1.i(ii) reprovado.",true,2);
                    durchlauffehler = true;
                }
                evaluator.kommentar(testnamen[4],false,2);
                if (test4()) {
                    evaluator.kommentar("Teste T4 aprovado.",false,2);
                }
                else {
                    evaluator.kommentar("Teste T4 (Long Runtest);   Criterio P1.i(ii) reprovado.",true,2);
                    durchlauffehler = true;
                }
                evaluator.kommentar(testnamen[5],false,2);
                if (test5()) {
                    evaluator.kommentar("Teste T5 aprovado.",false,2);
                }
                else {
                    evaluator.kommentar("Teste T5 (Autoorrelacao); Criterio P1.i(ii) reprovado.",true,2);
                    durchlauffehler = true;
                }
                if (durchlauffehler){
                    evaluator.kommentar("Ciclo/Passagem " + (durchlauf+1) + " reprovado.", true, 1);
                    fehler = true;
                }
                else {
                    evaluator.kommentar("Ciclo/Passagem " + (durchlauf+1) + " aprovado.", false, 1);
                }
                evaluator.setzefortschritt(durchlauf + 42);
            }
            evaluator.setzefortschritt(300);
            evaluator.dateieingabe.setText(evaluator.dateieingabe.getText() + "_rest");
        }
        if (testart == 2){
            evaluator.kommentar("O procedimento de teste T6a para verifica????o de P2.i)(vii.a) foi iniciado..", false, 1);
            if (test6a()){
                evaluator.kommentar("Procedimento de teste T6a aprovado.", false, 1);
            }
            else {
                evaluator.kommentar("Procedimento de teste T6a para verifica????o de P2.i)(vii.a) reprovado.",true,1);
                fehler = true;
            }
            if (zuwenigdaten) {
                evaluator.kommentar("Poucos dados de teste - s??rie de teste abortada (o programa l?? no m??ximo 7200000 bits aleat??rios).",true,1);
                return false;
            }
            evaluator.setzefortschritt(100);
            evaluator.kommentar("Procedimento de teste T6b para verifica????o de P2.i)(vii.b) iniciado.", false, 1);
            if (test6b()){
                evaluator.kommentar("Procedimento de teste T6b aprovado.", false, 1);
            }
            else {
                evaluator.kommentar("Procedimento de teste T6b para verifica????o de P2.i)(vii.b) reprovado.",true,1);
                fehler = true;
            }
            if (zuwenigdaten) {
                evaluator.kommentar("Poucos dados de teste - s??rie de teste abortada (o programa l?? no m??ximo 7200000 bits aleat??rios).",true,1);
                return false;
            }
            evaluator.setzefortschritt(150);
            evaluator.kommentar("Procedimento de teste T7a para verifica????o de  P2.i)(vii.c) iniciado.", false, 1);
            if (test7a()){
                evaluator.kommentar("Procedimento de teste T7a aprovado.", false, 1);
            }
            else {
                evaluator.kommentar("Procedimento de teste T7a para verifica????o de P2.i)(vii.c) reprovado.",true,1);
                fehler = true;
            }
            if (zuwenigdaten) {
                evaluator.kommentar("Poucos dados de teste - s??rie de teste abortada (o programa l?? no m??ximo 7200000 bits aleat??rios).",true,1);
                return false;
            }
            evaluator.setzefortschritt(200);
            evaluator.kommentar("Procedimento de teste T7b para verifica????o de P2.i)(vii.d) iniciado.", false, 1);
            if (test7b()){
                evaluator.kommentar("Procedimento de teste T7b aprovado.", false, 1);
            }
            else {
                evaluator.kommentar("Procedimento de teste T7b para verifica????o de P2.i)(vii.d) reprovado.",true,1);
                fehler = true;
            }
            if (zuwenigdaten) {
                evaluator.kommentar("Poucos dados de teste - s??rie de teste abortada (o programa l?? no m??ximo 7200000 bits aleat??rios).",true,1);
                return false;
            }
            evaluator.setzefortschritt(250);
            evaluator.kommentar("Teste T8 para verifica????o de P2.i)(vii.e) iniciado.", false, 1);
            if (test8()){
                evaluator.kommentar("Teste T8 aprovado.", false, 1);
            }
            else {
                evaluator.kommentar("Teste T8 para verifica????o de P2.i)(vii.e) reprovado.",true,1);
                fehler = true;
            }
            evaluator.setzefortschritt(300);
        }
        evaluator.elementeAnpassen(0);
        return !fehler;
    }
    boolean test0(int c, int bitbreite){
        int i,j,einsen;
        boolean ok;
        long u0= (long)0;
        long u1= (long)1;
        long[] WFeld = new long[65536];
        for(i=0; i<65536; i++){
            WFeld[i]=u0;
            for(j=0; j<48; j++) WFeld[i]+=(u1<<j)*BitFeldA[(c*bitbreite)*i+j];
        }
        Arrays.sort(WFeld);
        ok=true;
        for(i=1; i<65536; i++) {
            if (WFeld[i-1]==WFeld[i]) {
                ok=false;
            }
        }
        return ok;
    }



    boolean test1(){
        int i, einsen, mg=20000, ptest=0;
        boolean ok;
        einsen=0;
        for(i=0; i<mg; i++) {
            einsen+=BitFeldB[i];
        }
        if ((einsen>9654) && (einsen<10346)) ok=true;
        else ok=false;
        evaluator.kommentar("Numero um: " + einsen, false,3);
        evaluator.kommentar("Faixa permitida: [9655; 10345]", false,2);
        return ok;
    }



    boolean test2(){
        int i,j,index,ptest;
        int[] Hfg = new int[16];
        boolean ok;
        double testgroesse;

        for(i=0; i<16; i++) Hfg[i]=0;
        for(i=0; i<5000; i++) {
            index=0;
            for(j=0; j<4; j++) index+=(1<<j)*BitFeldB[4*i+j];
            Hfg[index]++;
        }
        testgroesse=0.0;
        for(i=0; i<16; i++) testgroesse+=Hfg[i]*Hfg[i];
        testgroesse=testgroesse*(16.0/5000.0)-5000.0;
        if ((testgroesse>1.03) && (testgroesse<57.4)) ok=true;
        else ok=false;
        if (geschwaetzig){
            evaluator.kommentar("Tamanho de teste = " + testgroesse, false,3);
        }
        return ok;
    }



    boolean test3(){
        int i,j,run,ptest=0;
        int[] Run0Feld = new int[7];
        int[] Run1Feld = new int[7];
        boolean ok;
        int UGrenze[]={0,2267,1079,502,223,90,90};
        int OGrenze[]={0,2733,1421,748,402,223,223};
        for(i=0; i<7; i++) Run0Feld[i]=Run1Feld[i]=0;
        run=1;
        for(i=1; i<20000; i++) {
            if (BitFeldB[i-1]==BitFeldB[i]) run++;
            else {
                if (run>6) run=6;
                if (BitFeldB[i-1]==1)
                    Run1Feld[run]++;
                else Run0Feld[run]++;
                run=1;
            }
        }
        if (run>6) run=6;
        if (BitFeldB[i-1]==1)
            Run1Feld[run]++;
        else Run0Feld[run]++;
        ok=true;
        for(i=1; i<=6; i++){
            if ( (Run0Feld[i]>=UGrenze[i]) && (Run0Feld[i]<=OGrenze[i]) ){
                evaluator.kommentar("0-Runs[" + i + "] = " + Run0Feld[i] + "; Intervalo Permitido: [" + UGrenze[i] + "; " + OGrenze[i] + "]",false,3);
            }
            else {
                ok=false;
                evaluator.kommentar("ERRO: 0-Runs[" + i + "] = " + Run0Feld[i] + "; Intervalo Permitido: [" + UGrenze[i] + "; " + OGrenze[i] + "]",true,3);
            }
            if ( (Run1Feld[i]>=UGrenze[i]) && (Run1Feld[i]<=OGrenze[i]) ){
                evaluator.kommentar("1-Runs[" + i + "] = " + Run1Feld[i] + "; Intervalo Permitido: [" + UGrenze[i] + "; " + OGrenze[i] + "]",false,3);
            }
            else {
                ok=false;
                evaluator.kommentar("ERRO: 1-Runs[" + i + "] = " + Run1Feld[i] + "; Intervalo Permitido: [" + UGrenze[i] + "; " + OGrenze[i] + "]",true,3);
            }
        }
        return ok;
    }



    boolean test4(){
        int i,run,ptest=0;
        boolean ok;

        run=1;ok=true;

        for(i=1; i<20000; i++) {
            if (BitFeldB[i-1]==BitFeldB[i]) {
                run++;
                if (run>=34) {
                    ok=false;
                    evaluator.kommentar("Long Run ocorreu (valor: " + BitFeldB[i] + "). Posicao do bit: " + (i-33) + ".",true,2);
                }
            }
            else run=1;
        }
        return ok;
    }



    boolean test5(){
        int i,j,k,tau,Z_tau,max,maxindex,ptest=0;
        boolean ok;
        int[] ShiftFeld = new int[5000];
        int[] MaxKorrFeld = new int[5000];

        for(tau=1; tau<=5000; tau++) {
            Z_tau=0;
            for(i=0; i<5000; i++) Z_tau+=(BitFeldB[i]^BitFeldB[i+tau]);
            ShiftFeld[tau-1]=Z_tau;
        }

        max=0;

        for(tau=0; tau<5000; tau++) {
            if (Math.abs(ShiftFeld[tau]-2500)>max){
                max=Math.abs(ShiftFeld[tau]-2500);
            }
        }

        j=0;
        for(tau=0; tau<5000; tau++) {
            if (Math.abs(ShiftFeld[tau]-2500)==max){
                MaxKorrFeld[j]=tau;
                j++;
            }
        }

        evaluator.kommentar("Desvio m??ximo de Z_tau de 2500: " + max,false,3);
        evaluator.kommentar("Realizado para turnos (shifts): ",false,3);
        for(k=0; k<j; k++){
            evaluator.kommentar("Shift: " + (MaxKorrFeld[k]+1),false,4);
        }
        tau=MaxKorrFeld[0];
        Z_tau=0;
        for(i=10000; i<15000; i++){
            Z_tau+=(BitFeldB[i]^BitFeldB[i+tau+1]);
        }
        tau++;
        evaluator.kommentar("Teste de autocorrelacao novamente com Shift: " + tau + " nos bits 10.000 a 14.999",false,3);
        evaluator.kommentar("Z_" + tau + " = " + Z_tau,false,3);
        if (( Z_tau > 2326) && ( Z_tau< 2674))
            ok=true;
        else
            ok=false;
        return ok;
    }


    boolean test6a(){
        boolean ok;
        double[] prob = new double[1];
        double absdiff = 0.0;
        int groesse=100000;
        int einsen=0;
        for(int i=0; i<groesse ; i++) einsen+=BitFeldA[i];
        prob[0]=(double) einsen / (double) groesse;
        absdiff=Math.abs(prob[0]-0.5);
        if ((absdiff<0.025)) ok = true;
        else ok = false;
        letzterwert = groesse;
        evaluator.kommentar("|P(1) - 0.5| = " + absdiff,false,2);
        evaluator.kommentar("Ultimo Elemento: " + letzterwert,false,2);
        return ok;
    }


    boolean test6b(){
        int groesse=100000;
        int[] counter = {0,0};
        int einsen[] = {0,0};
        int voll[] = {0,0};
        int i=0;
        double[] prob = new double[2];
        double absdiff = 0.0;
        while ((voll[0]+voll[1]<2) && ((letzterwert+2*i+1)<bitzahl)){
            if(voll[BitFeldA[letzterwert+2*i]] == 1);
            else {
                counter[BitFeldA[letzterwert+2*i]]+=1;
                if (counter[BitFeldA[letzterwert+2*i]]==groesse) voll[BitFeldA[letzterwert+2*i]]=1;
                einsen[BitFeldA[letzterwert+2*i]]+=BitFeldA[letzterwert+2*i+1];
            }
            i++;
        }
        letzterwert += 2*i;
        if (voll[0]+voll[1]<2){
            evaluator.kommentar("Inputdatei zu klein.... Criterio P2.i)(vii.b) konnte nicht geprueft werden.",true,2);
            zuwenigdaten = true;
            return false;
        }
        else {

            for(i=0; i<2; i++) prob[i]=(double)einsen[i]/groesse;
            absdiff=Math.abs(prob[0]-prob[1]);
            evaluator.kommentar("p(01) = " + prob[0],false,2);
            evaluator.kommentar("p(11) = " + prob[1],false,2);
            evaluator.kommentar("|p_(01) - p_(11)| = " + absdiff,false,2);
            evaluator.kommentar("Ultimo Elemento: " + letzterwert,false,2);
            if (absdiff<0.02) return true;
            else return false;
        }
    }

    boolean test7a(){
        int groesse=100000;
        int hilf;
        int[] voll = {0,0,0,0};
        int[] counter = {0,0,0,0};
        int[] einsen = {0,0,0,0};
        int[] nullen = {0,0,0,0};
        double sum = 0;
        int i=0;
        while ((voll[0]+voll[1]+voll[2]+voll[3]<4) && ((letzterwert+3*i)<bitzahl)){
            hilf=2*BitFeldA[letzterwert+3*i+1]+BitFeldA[letzterwert+3*i];
            if(voll[hilf] == 1);
            else {
                counter[hilf]+=1;
                if (counter[hilf]==groesse) voll[hilf]=1;
                einsen[hilf]+=BitFeldA[letzterwert+3*i+2];
            }
            i++;
        }
        letzterwert += 3*i;
        if (voll[0]+voll[1]+voll[2]+voll[3]<4){
            evaluator.kommentar("Inputdatei zu klein.... Criterio P2.i)(vii.c) konnte nicht geprueft werden",true,2);
            zuwenigdaten = true;
            return false;
        }
        else{
            for(i=0; i<4; i++) nullen[i]=groesse-einsen[i];
            boolean ok = true;
            for(i=0; i<2; i++) {
                sum=((double)((nullen[2*i]-nullen[2*i+1])*(nullen[2*i]-nullen[2*i+1]))/(nullen[2*i]+nullen[2*i+1])
                + (double)((einsen[2*i]-einsen[2*i+1])*(einsen[2*i]-einsen[2*i+1]))/(einsen[2*i]+einsen[2*i+1]));
                if(sum>15.13) ok = false;
                evaluator.kommentar("Tamanho de Teste [" + i + "] = " + sum,false,2);
            }
            evaluator.kommentar("Ultimo Elemento: " + letzterwert,false,2);
            return ok;
        }
    }


    boolean test7b(){
        boolean ok;
        int hilf;
        int[] voll = {0,0,0,0,0,0,0,0};
        int[] counter = {0,0,0,0,0,0,0,0};
        int[] einsen = {0,0,0,0,0,0,0,0};
        int[] nullen = {0,0,0,0,0,0,0,0};
        double sum = 0;
        int groesse=100000;
        int i=0;
        while ((voll[0]+voll[1]+voll[2]+voll[3]+voll[4]+voll[5]+voll[6]+voll[7]<8) && ((letzterwert+4*i)<bitzahl)){
            hilf=4*BitFeldA[letzterwert+4*i+2]+2*BitFeldA[letzterwert+4*i+1]+BitFeldA[letzterwert+4*i];
            if(voll[hilf] == 1);
            else {
                counter[hilf]+=1;
                if (counter[hilf]==groesse) voll[hilf]=1;
                einsen[hilf]+=BitFeldA[letzterwert+4*i+3];
            }
            i++;
        }
        letzterwert += 4*i;
        if (voll[0]+voll[1]+voll[2]+voll[3]+voll[4]+voll[5]+voll[6]+voll[7]<8){
            evaluator.kommentar("Arquivo de entrada muito pequeno... Criterio P2.i)(vii.d) nao pode ser verificado",true,2);
            zuwenigdaten = true;
            ok = false;
        }
        else{
            for(i=0; i<8; i++) nullen[i]=groesse-einsen[i];
            ok=true;
            for(i=0; i<4; i++) {
                sum=(double)((nullen[2*i]-nullen[2*i+1])*(nullen[2*i]-nullen[2*i+1]))/(nullen[2*i]+nullen[2*i+1])
                + (double)((einsen[2*i]-einsen[2*i+1])*(einsen[2*i]-einsen[2*i+1]))/(einsen[2*i]+einsen[2*i+1]);
                if(sum>15.13) ok=false;
                evaluator.kommentar("Tamanho de teste [" + i + "] = " + sum,false,2);
            }
        }
        evaluator.kommentar("Ultimo Elemento: " + letzterwert,false,2);
        return ok;
    }



    boolean test8(){

        final int L = 8;
        final int Q = 2560;
        final int K = 256000;
        int hilf = 0;
        int counter = 0;
        int[] letzteposition = new int[256];
        for (int i = 0; i<256; i++){
            letzteposition[i] = -1;
        }
        int[] W = new int[Q+K];
        double TG;
        double[] G = new double[K+Q+1];
        int abstand;

        G[1]=0.0;
        for(int i=1; i<=K+Q-1; i++) G[i+1]=G[i]+1.0/(i);
        for(int i=1; i<=K+Q; i++) G[i]/=Math.log(2.0);
        for (int i = 0; i<Q; i++){
            hilf = 0;
            for (int j = 0; j<8; j++){
                hilf+= BitFeldA[letzterwert+8*i+j]<<j;
            }
            letzteposition[hilf] = i;
        }
        TG=0.0;
        for(int i=Q; i<K+Q; i++){
            hilf = 0;
            for (int j = 0; j<8; j++){
                hilf+= BitFeldA[letzterwert+8*i+j]<<j;
            }
            abstand = i - letzteposition[hilf];
            letzteposition[hilf] = i;
            TG+=G[abstand];
        }
        TG/=(double)K;
        evaluator.kommentar("Tamanho de teste  = " + TG,false,2);
        letzterwert += 8*(2560+256000);
        evaluator.kommentar("Ultimo Elemento: " + letzterwert,false,2);
        if (TG>7.976) return true;
        else return false;
    }
}
