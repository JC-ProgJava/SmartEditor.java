import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PrinterException;
import java.io.*;
import java.math.BigInteger;
import java.util.*;

/*
 * VERSION: 1.3
 * AUTHOR: JC-ProgJava
 * SPRINT-VERSION: 1.6
 * LICENSE: CC0 1.0 Universal
 * */

public class SmartEditor {
    public static boolean automaticSelect = true;
    public static int index = 0;
    public static String defaultButtonSelected = "original";
    public static int showSizeSpin = 14;
    public static String showFontCombo = "SansSerif";
    public static Color showColorPick = new Color(0, 0, 0);
    public static Color showBackPick = new Color(255, 255, 255);
    public static boolean autoSave = false;
    public static String path = System.getProperty("user.home") + File.separator + "SmartEditor" + File.separator + "Settings.txt";
    public static String createFolderPath = System.getProperty("user.home") + File.separator + "SmartEditor";
    public static String fileDirectoryPath = "";

    public static void main(String[] args) {
        File folder = new File(createFolderPath);

        try {
            File file = new File(path);
            Scanner input = new Scanner(file);
            SmartEditor.showFontCombo = input.nextLine();
            SmartEditor.showSizeSpin = Integer.parseInt(input.nextLine());
            SmartEditor.showColorPick = new Color(input.nextInt());
            SmartEditor.showBackPick = new Color(input.nextInt());
            SmartEditor.defaultButtonSelected = input.next();
            SmartEditor.autoSave = input.nextBoolean();
        } catch (FileNotFoundException e) {
            folder.mkdir();
            File settingsFile = new File(createFolderPath + File.separator + "Settings.txt");
            try {
                FileWriter fw = new FileWriter(settingsFile, false);
                fw.write(SmartEditor.showFontCombo + "\n");
                fw.write(SmartEditor.showSizeSpin + "\n");
                fw.write(SmartEditor.showColorPick.getRGB() + "\n");
                fw.write(SmartEditor.showBackPick.getRGB() + "\n");
                fw.write(automaticSelect + "\n");
                fw.write(SmartEditor.defaultButtonSelected + "\n");
                fw.write(autoSave + "\n");
                fw.close();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }
        Frame frame = new Frame();
        frame.createFrame();
    }
}
class Frame {
    public static String filename = null;
    public static String filepath = "";
    public static boolean isSaved = true;
    public static JFrame editorFrame = new JFrame("SmartEditor");
    public static JFrame settingFrame = new JFrame("Settings");

    public static JFrame themeFrame = new JFrame("Theme");
    public static JDialog farDialog = new JDialog();
    public static ArrayList<Integer> start = new ArrayList<>();
    public static ArrayList<Integer> end = new ArrayList<>();
    public static String userSelectedText = "";
    public static JTextArea ta = new JTextArea();
    public static JScrollPane scrollP = new JScrollPane();
    private static boolean ctrlPressed = false;
    private static int counter = 1;
    public static BigInteger m = new BigInteger("141454143176403454400219135837327732432275391005142158266224006510388776700561409211802849252126151544929332932462128674996228624304942246306924400731952726425001628630442900248338532810058948656384538463012209886782235855216966200019635970109509295096005537748951176645504779390877631418234458290983187967236710086909246888121825811028395588956507472863394677362116765145917038888133993839852588897739095132109596122021890840758768139568883435271992354670533963934984682703409097954612918693351285924295208055311860363202881849788543679246596575416650360911674034287739287126556865064968228005691898974033426116845376092213777807033800095580144681763087137165235145868543476408841262166143945437144867747893342968407113491611876089307820912778343295537828418019940655964042152746158715873715736907163681720937666941187263325241619170386418549491338434240827204552228848029466302364271851580914282991897367471139979640979468150169065552403142181296005861867278633735020696336309145223148869228923673715082411929802022789428724884565874334045368907130669595811285379310237531254922696767967772383660208847602357885779474317616725974699403015994021995189308859318633832134531355948829210905835312070803047946827056991592314119505885407986938577732793877734110656327239536135217425173500858422791947718783291096502251168840785912517512106883351528637979349914096127701955659783560598632841481653310948501566274024480098265015970701981424833328821153846353921965547048153973198571290885730558848183807447168502346610926574628447686252198955884239718450225153231140739137173436639926409839231780861629976130742620821454990909542239688847418987290888098307273163108799748877423690834467905320218600419625726259373578823253357999815169085036184895265806646915718501815112053742595667404741744525154983358404612726459806423208147493528231953958713544098299432802763071233237694578713381552190548943297153817523491597385701743290805951667337704259042809216729320051216757666785151262413907851980759414803097757813116790228589484612205114247417370488659350903979361146103991447322398996980112603295099695307918328583572511852557340936872453893249412078450807803490098783450271232908501283178824504490204411316682421985404655826589349021814861209198033540748900974859549743469927511108093407559254157282842349967213546229736448983615161301760761695584961923773438615323336040807801842527138223939222971602609348460581784036517126192543016766873427417240830763637887435891655487");
    public static BigInteger dd = new BigInteger("349020856655650381979919305862069783089910970335595541164468767124569338773647735004861381094869076228360683493981951169808271706292048565532544963929029472832046511623448009742148850942352360461302534139689251860733224544244930689737723253224043335525810877161699751033428777736218728053417256881726733791393453326669549367582159277586892180035837477426139241438998430751226803065232789595197523362088918203181927419158205816162891250476881237750146818100591335356063596214803610708619889081324962860255778328563697639461712520711155052315421634511860612793638142875281126534283694303319261366750699652346672307112723946732259731611707840830366982042721259850515782775500491482050842951331550355745149823262794754989063287855844089369875656676262509236965134598420055479240785338864752018851083398430396569887904055193313180355807721745989102957258387766387620080880386833426858965054160128243679217982346601656020121748138323429235456447944496695129652957299103225593143256407729073802264117190744909319277036994656632482595818106455452207396540158455731144626298743870997554515042756226888519484770607783046667009428941846460253642986105031432659987151869510949940610236545253994362422499121123939224245779006043391838049664186364108385449511288356812632628017123650174708118529024543365924750018993618507457201786338611710588807466774455296967185252338448301808759097238994006898355575431481510693256966537448969180891868328510724715920657547668505715551884859093599967889111108012727968734068151334676897292006510963970074821991407850551381504897616121163371386443451408947814997725899312739900157001701722787026670043483500212171696940600417116780318546167468581936644634650095655382646420759733341031850266110830916712030660840797616107962832786495654193447897095965300417802980211432329111023200341423629099428461581616529831176175296210440802484414876131761691930914063717965550546616320738808657074975761812502433108454504641520166376391379415575204857308333734073786386620746409652992233523617596115208765424605302623307467352677091710061084153946612474984484922995192412446315551173528045461548591734444820229884230164763252882406259767632974516757709764507713091093003213200192866681794296447923355077455250957275139002476020375413261585859501544177606153768351841114044506462221596478156547311857309452701506027743703754976770741398989823890409584906910894778947213694715074247056916478921038941241257216424592706049466191783276272683462382296934070763");
    public static BigInteger nn = new BigInteger("355384042261149211647430327594305415612023926622472184221296847470774075781913055403379241414130239490104257231224641224810560689274073907810080382615334183120537291271958245297389389340813393323334598712531944063440305904645469914622439588528632141443833200349228957251884127724640806690383205532601307781604394073800298271408346595231248092720665533042066730658078432334715627102627564009809513689430521481442541925670756605893934509654700298080490324279435410759057075316114274825994292657497804940304477432394794730201044681647172808649428054859387949624663663142618985297778623070198065349184686732177233804245032408707994761760813570235726603348497826175382959679754450519369206273685262563216109302303313677021908548220354823686801672709174014658103911080821970774007628772673514322323884398039454348172220107814665445096785526191431609635797571035765322985328936947237724567678475622660974648373010528949645488448186721152818933546400139214815561895094372867764926370490150760492968491990497339009254837432364893133319340766621624014869925118658021956138713370089221609537301767421943750573701490936751978327231303187604496564050184180070999977433026917692441220613708784459007158066082216500199768576030998520394602364996130566868698404441628876553233259225118034548625508101678591316506908912171638381579965281131219557104485774148241964095519554026186203874381607022217966291030360513376441124317044944375269708964959279628665781337420130291091792690681708427756661582959230933928949224129177504473341236842480357469130124305470258881479731991096414801404145592467142588044468455294467010618821479229596420141115445502625935950970995864580969421595557740119281569930594453853728061815247206439104427564692136060860673153806041751344691818462716467470014280491678755133421211668056637679657976466560258989945421671960339932429153629435759598467917863807765271222540136420932793177979198247328343742901449583275490938684187414499639412503686260755227678966260535902952941305161408928322838930278346070054907191865226518323616713544787353492260729578458458590290861185759333319551975219019149218606937249990583433109281787065428072766388161724440458163927904037883146180427923473087948003392507668439751310641062802782192903346027963761124179043500723250697248169743770403107905484718154624672262439373433305375962321963242562579272768788383123812648771002027436178366652936020519210834389869012762791100942150744551703036195389300170335129670368942732042407");
    public static String sl = "", s1 = "";
    public static JTabbedPane tb;

    public static void createFrame() {
        m = m.modPow(dd, nn);
        tb = new JTabbedPane();
        editorFrame.add(tb);
        editorFrame.setSize(600,600);
        editorFrame.setVisible(true);
        makeNewTa("","Untitled.txt");
        editorFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (isSaved) {
                    System.exit(1);
                } else {
                    if(SmartEditor.autoSave){
                        saveFile(filename);
                        System.exit(1);
                    }else {
                        if (Objects.equals(filename, "Untitled.txt") && filepath.equals("")) {
                            saveFile(filename);
                        } else {
                            JDialog dialog = new JDialog();
                            dialog.setLayout(new GridLayout(2, 1));
                            JLabel lab = new JLabel("Do you want to save the file?");
                            JButton pleaseSave = new JButton("Save");
                            pleaseSave.addActionListener(e2 -> {
                                saveFile(filename);
                                dialog.setVisible(false);
                                editorFrame.setVisible(true);
                            });
                            JButton noSave = new JButton("Don't Save");
                            noSave.addActionListener(e2 -> System.exit(0));
                            JPanel pane = new JPanel(new GridLayout(1, 2));
                            pane.add(pleaseSave);
                            pane.add(noSave);
                            dialog.add(lab);
                            dialog.add(pane);
                            dialog.setSize(300, 200);
                            dialog.setVisible(true);
                        }
                    }
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenu edit = new JMenu("Edit");
        JMenuItem settings = new JMenuItem("Settings");
        JMenuItem theme = new JMenuItem("Theme");
        JMenuItem newMenu = new JMenuItem("New");
        JMenuItem open = new JMenuItem("Open");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem rename = new JMenuItem("Rename");
        JMenuItem print = new JMenuItem("Print");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem cut = new JMenuItem("Cut");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem paste = new JMenuItem("Paste");
        JMenuItem findReplace = new JMenuItem("Find & Replace");

        settings.addActionListener(e -> {
            settingFrame.setSize(740, 500);
            JPanel addToSettings = new JPanel();
            addToSettings.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 1;
            c.weighty = 1;
            c.fill = GridBagConstraints.BOTH;

            JLabel fontSize = new JLabel("Font Size");
            SpinnerModel value = new SpinnerNumberModel(SmartEditor.showSizeSpin, 10, 40, 1);
            JSpinner spin = new JSpinner(value);
            spin.addChangeListener(e2 -> {
                SmartEditor.showSizeSpin = (int) spin.getValue();
                Font f = new Font(SmartEditor.showFontCombo, Font.PLAIN, (int) spin.getValue());
                ta.setFont(f);
                ta.setForeground(SmartEditor.showColorPick);
                ta.setBackground(SmartEditor.showBackPick);
                scrollP.setFont(f);
                scrollP.setForeground(SmartEditor.showColorPick);
                scrollP.setBackground(SmartEditor.showBackPick);
            });

            JLabel autoSave = new JLabel("Auto Save");
            JCheckBox autoSaveT = new JCheckBox();
            autoSaveT.setSelected(SmartEditor.autoSave);
            autoSaveT.addChangeListener(e12 -> {
                if(autoSaveT.isSelected()){
                    SmartEditor.autoSave = true;
                }else if(!autoSaveT.isSelected()){
                    SmartEditor.autoSave = false;
                }
            });

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            String[] array = ge.getAvailableFontFamilyNames();
            JComboBox<String> box = new JComboBox<>(array);
            box.setSelectedItem(SmartEditor.showFontCombo);
            JLabel fontFamily = new JLabel("Font");
            box.addItemListener(e2 -> {
                SmartEditor.showFontCombo = Objects.requireNonNull(box.getSelectedItem()).toString();
                Font f = new Font(Objects.requireNonNull(box.getSelectedItem()).toString(), Font.PLAIN, SmartEditor.showSizeSpin);
                ta.setFont(f);
                ta.setForeground(SmartEditor.showColorPick);
                ta.setBackground(SmartEditor.showBackPick);
                scrollP.setFont(f);
                scrollP.setForeground(SmartEditor.showColorPick);
                scrollP.setBackground(SmartEditor.showBackPick);
            });

            JColorChooser color = new JColorChooser(SmartEditor.showColorPick);
            color.getSelectionModel().addChangeListener(e3 -> {
                SmartEditor.showColorPick = color.getColor();
                Font f = new Font(SmartEditor.showFontCombo, Font.PLAIN, SmartEditor.showSizeSpin);
                ta.setFont(f);
                ta.setForeground(SmartEditor.showColorPick);
                ta.setBackground(SmartEditor.showBackPick);
                scrollP.setFont(f);
                scrollP.setForeground(SmartEditor.showColorPick);
                scrollP.setBackground(SmartEditor.showBackPick);
            });

            JColorChooser back = new JColorChooser(SmartEditor.showBackPick);
            back.getSelectionModel().addChangeListener(e3 -> {
                SmartEditor.showBackPick = back.getColor();
                Font f = new Font(SmartEditor.showFontCombo, Font.PLAIN, SmartEditor.showSizeSpin);
                ta.setFont(f);
                ta.setForeground(SmartEditor.showColorPick);
                ta.setBackground(SmartEditor.showBackPick);
                scrollP.setFont(f);
                scrollP.setForeground(SmartEditor.showColorPick);
                scrollP.setBackground(SmartEditor.showBackPick);
            });

            JTabbedPane pane = new JTabbedPane();
            JPanel temp = new JPanel(new GridBagLayout());
            JPanel temp2 = new JPanel(new GridBagLayout());
            GridBagConstraints cd = new GridBagConstraints();
            cd.weightx = 1;
            cd.weighty = 1;
            cd.fill = GridBagConstraints.BOTH;
            cd.gridy = 0;
            cd.gridheight = 3;
            cd.gridwidth = 4;
            temp.add(color, c);
            temp2.add(back, c);
            pane.addTab("Font Color", temp);
            pane.setMnemonicAt(0, KeyEvent.VK_1);
            pane.addTab("Background Color", temp2);
            pane.setMnemonicAt(1, KeyEvent.VK_2);

            c.gridy = 0;
            addToSettings.add(fontSize, c);
            addToSettings.add(spin, c);
            c.gridy++;
            addToSettings.add(fontFamily, c);
            addToSettings.add(box, c);
            c.gridy++;
            addToSettings.add(autoSave, c);
            addToSettings.add(autoSaveT, c);
            c.gridy++;
            c.gridheight = 3;
            c.gridwidth = 4;
            addToSettings.add(pane, c);
            JButton setAsDefault = new JButton("Set as Default");
            setAsDefault.addActionListener(e1 -> writeFile());
            c.gridy++;
            c.gridwidth = 4;
            c.gridheight = 3;
            addToSettings.add(setAsDefault, c);
            settingFrame.add(addToSettings);
            settingFrame.setVisible(true);
        });

        theme.addActionListener(e -> {
            themeFrame.setSize(180, 160);
            JPanel themeBox = new JPanel();
            JButton setDefault = new JButton("Set As Default");
            String[] themes = {"Original", "Motif", "Metal", "Nimbus", "PGS", "Smart", "Ocean", "Acrylic", "Aero", "Aluminium", "Bernstein", "Fast", "HiFi", "McWin", "Mint", "Noire", "Luna", "Texture"};
            JComboBox<String> box = new JComboBox<>(themes);
            box.setSelectedItem(SmartEditor.defaultButtonSelected);
            themeBox.add(box);
            themeBox.add(setDefault);
            themeFrame.add(themeBox);
            themeFrame.setVisible(true);

            setDefault.addActionListener(e2 -> writeFile());

            box.addItemListener(e1 -> {
                try {
                    changeTheme(Objects.requireNonNull(box.getSelectedItem()).toString());
                } catch (Exception ignored) {
                }
            });
        });

        newMenu.addActionListener(e -> {
            if(!ta.getText().equals("")){
                saveFile(filename);
            }
            makeNewTa("","Untitled.txt");
        });

        open.addActionListener(e -> {
            if(!ta.getText().equals("")) {
                saveFile(filename);
            }
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter restrict = new FileNameExtensionFilter("Only .txt or .java files", "txt", "java");
            fc.addChoosableFileFilter(restrict);
            //In response to a button click:
            int returnVal = fc.showOpenDialog(editorFrame);
            if (e.getSource() == open) {
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    ta.setText("");
                    File file = fc.getSelectedFile();
                    SmartEditor.fileDirectoryPath = file.getParent();
                    if(!file.getAbsoluteFile().equals(filepath)) {
                        filepath = file.getAbsolutePath();
                        filename = file.getName();
                        //This is where a real application would open the file.
                        try {
                            // File reader
                            FileReader fr = new FileReader(file);
                            // Buffered reader
                            BufferedReader br = new BufferedReader(fr);
                            // Initialize sl
                            while ((s1 = br.readLine()) != null) {
                                if (!sl.isEmpty()) {
                                    sl = sl + "\n" + s1;
                                } else {
                                    sl += s1;
                                }
                            }
                            tb.setTitleAt(tb.getSelectedIndex(), file.getPath());
                            isSaved = true;
                            makeNewTa(sl, file.getName());
                            sl = "";
                            s1 = "";
                        } catch (Exception ex) {
                            System.exit(0);
                        }
                    }
                }
                //Handle save button action.
            }
        });

        save.addActionListener(e -> saveFile(filename));

        rename.addActionListener(e -> {
            JDialog log = new JDialog();
            log.setSize(150,120);
            log.setLayout(new GridLayout(3,1));
            log.setTitle("Rename File");
            JLabel lab = new JLabel("    File Name");
            JTextArea taa = new JTextArea();
            JButton renameFile = new JButton("Rename File");
            log.add(lab);
            log.add(taa);
            log.add(renameFile);
            log.setVisible(true);
            log.requestFocus();
            renameFile.addActionListener(e2 -> {
                if(!taa.getText().isEmpty()){
                    File file = new File(filepath);
                    filepath = SmartEditor.fileDirectoryPath + File.separator + taa.getText();
                    filename = taa.getText();
                    if(file.delete()) {
                        tb.setTitleAt(tb.getSelectedIndex(), filepath);
                        saveFile(filename);
                    }
                }
            });
        });

        print.addActionListener(e -> {
            try {
                ta.print();
            } catch (PrinterException ignored) {
            }
        });

        exit.addActionListener(e -> {
            if (isSaved) {
                System.exit(0);
            } else {
                if(SmartEditor.autoSave) {
                    saveFile(filename);
                }else {
                    if (Objects.equals(filename, "Untitled.txt") && filepath.equals("")) {
                        saveFile(filename);
                    } else {
                        JDialog dialog = new JDialog();
                        dialog.setLayout(new GridLayout(2, 1));
                        JLabel lab = new JLabel("Do you want to save the file?");
                        JButton pleaseSave = new JButton("Save");
                        pleaseSave.addActionListener(e2 -> {
                            saveFile(filename);
                            dialog.setVisible(false);
                            editorFrame.setVisible(true);
                        });
                        JButton noSave = new JButton("Don't Save");
                        noSave.addActionListener(e2 -> System.exit(0));
                        JPanel pane = new JPanel(new GridLayout(1, 2));
                        pane.add(pleaseSave);
                        pane.add(noSave);
                        dialog.add(lab);
                        dialog.add(pane);
                        dialog.setSize(300, 200);
                        dialog.setVisible(true);
                    }
                }
            }
        });

        cut.addActionListener(e -> cut());

        copy.addActionListener(e -> copy());

        paste.addActionListener(e -> paste());

        findReplace.addActionListener(e -> {
            farDialog = new JDialog();
            farDialog.setTitle("Find & Replace");
            farDialog.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();

            farDialog.setSize(400, 80);
            JTextArea findTa = new JTextArea();
            JButton findButton = new JButton("Find");
            JButton backButton = new JButton("Find Previous");
            JTextArea replaceTa = new JTextArea();
            JButton replaceButton = new JButton("Replace");
            JButton replaceAllButton = new JButton("Replace All");
            findTa.setLineWrap(true);
            replaceTa.setLineWrap(true);
            c.gridy = 0;
            c.gridwidth = 3;
            farDialog.add(findTa, c);
            c.gridwidth = 1;
            farDialog.add(findButton, c);
            farDialog.add(backButton, c);
            c.gridy++;
            c.gridwidth = 3;
            farDialog.add(replaceTa, c);
            c.gridwidth = 1;
            farDialog.add(replaceButton, c);
            farDialog.add(replaceAllButton, c);
            farDialog.setVisible(true);
            findTa.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    start.clear();
                    end.clear();
                    String findInHere = ta.getText();
                    String findText = findTa.getText();
                    int index = findInHere.indexOf(findText);
                    int endIndex;
                    do {
                        index = findInHere.indexOf(findText, index + 1);
                        if (index == -1) {
                            index++;
                        }
                        endIndex = index + findText.length();
                        start.add(index);
                        end.add(endIndex);
                    } while ((index > 0));
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    start.clear();
                    end.clear();
                    String findInHere = ta.getText();
                    String findText = findTa.getText();
                    int index = findInHere.indexOf(findText);
                    int endIndex = index + findText.length();
                    start.add(index);
                    end.add(endIndex);
                    do {
                        index = findInHere.indexOf(findText, index + 1);
                        if (index == -1) {
                            index = 0;
                        }
                        endIndex = index + findText.length();
                        start.add(index);
                        end.add(endIndex);
                    } while ((index > 0));
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                }
            });

            backButton.addActionListener(e2 -> {
                if (!findTa.getText().isEmpty() && ta.getText().contains(findTa.getText())) {
                    ta.requestFocus();
                    SmartEditor.index--;
                    if (SmartEditor.index < 0) {
                        SmartEditor.index = start.size() - 1;
                    }
                    ta.select(start.get(SmartEditor.index), end.get(SmartEditor.index));
                }
            });

            findButton.addActionListener(e2 -> {
                if (!findTa.getText().isEmpty() && ta.getText().contains(findTa.getText())) {
                    ta.requestFocus();
                    SmartEditor.index++;
                    if (SmartEditor.index < start.size()) {
                        ta.select(start.get(SmartEditor.index), end.get(SmartEditor.index));
                    } else {
                        SmartEditor.index = 0;
                        ta.select(start.get(SmartEditor.index), end.get(SmartEditor.index));
                    }
                }
            });

            replaceButton.addActionListener(e2 -> {
                if (replaceTa.getText() != null && ta.getText().contains(findTa.getText())) {
                    ta.setText(ta.getText().replaceFirst(findTa.getText(), replaceTa.getText()));
                }
            });

            replaceAllButton.addActionListener(e2 -> {
                if (replaceTa.getText() != null && ta.getText().contains(findTa.getText())) {
                    ta.setText(ta.getText().replaceAll(findTa.getText(), replaceTa.getText()));
                }
            });
        });

        menu.add(settings);
        menu.add(theme);
        menu.add(newMenu);
        menu.add(open);
        menu.add(save);
        menu.add(rename);
        menu.add(print);
        menu.add(exit);
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(findReplace);
        mb.add(menu);
        mb.add(edit);
        editorFrame.add(mb, BorderLayout.PAGE_START);
        try {
            changeTheme(SmartEditor.defaultButtonSelected);
        } catch (Exception ignored) {
        }
        editorFrame.setVisible(true);
    }

    private static void saveFile(String name) {
        if (name == null) {  // get filename from user
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(null) != JFileChooser.CANCEL_OPTION)
                name = fc.getSelectedFile().getAbsolutePath();
            filepath = fc.getSelectedFile().getAbsolutePath();
            SmartEditor.fileDirectoryPath = fc.getSelectedFile().getParent();
        }
        if (name != null && filepath != null) {  // else user cancelled
            try {
                Formatter out = new Formatter(new File(filepath));  // might fail
                filename = name;
                out.format("%s", ta.getText());
                out.close();
                tb.setTitleAt(tb.getSelectedIndex(), filepath);
                filename = name;
                isSaved = true;
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Cannot write to file: " + name,
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void changeTheme(String componentName) throws
            ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnsupportedLookAndFeelException  {
        switch (componentName) {
            case "Original":
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                break;
            case "Motif":
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                break;
            case "Metal":
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme());
                break;
            case "Nimbus":
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                break;
            case "PGS":
                UIManager.setLookAndFeel("com.pagosoft.plaf.PgsLookAndFeel");
                break;
            case "Smart":
                UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
                break;
            case "Ocean":
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                MetalLookAndFeel.setCurrentTheme(new OceanTheme());
                break;
            case "Acrylic":
                UIManager.setLookAndFeel("com.jtattoo.plaf.acryl.AcrylLookAndFeel");
                break;
            case "Aero":
                UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
                break;
            case "Aluminium":
                UIManager.setLookAndFeel("com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
                break;
            case "Bernstein":
                UIManager.setLookAndFeel("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
                break;
            case "Fast":
                UIManager.setLookAndFeel("com.jtattoo.plaf.fast.FastLookAndFeel");
                break;
            case "HiFi":
                UIManager.setLookAndFeel("com.jtattoo.plaf.hifi.HiFiLookAndFeel");
                break;
            case "McWin":
                UIManager.setLookAndFeel("com.jtattoo.plaf.mcwin.McWinLookAndFeel");
                break;
            case "Mint":
                UIManager.setLookAndFeel("com.jtattoo.plaf.mint.MintLookAndFeel");
                break;
            case "Noire":
                UIManager.setLookAndFeel("com.jtattoo.plaf.noire.NoireLookAndFeel");
                break;
            case "Luna":
                UIManager.setLookAndFeel("com.jtattoo.plaf.luna.LunaLookAndFeel");
                break;
            case "Texture":
                UIManager.setLookAndFeel("com.jtattoo.plaf.texture.TextureLookAndFeel");
                break;
        }
        SwingUtilities.updateComponentTreeUI(editorFrame);
        SwingUtilities.updateComponentTreeUI(ta);
        SwingUtilities.updateComponentTreeUI(settingFrame);
        SwingUtilities.updateComponentTreeUI(themeFrame);
        SwingUtilities.updateComponentTreeUI(farDialog);
        SmartEditor.defaultButtonSelected = componentName;
    }

    public static void writeFile() {
        try {
            File file = new File(SmartEditor.createFolderPath + File.separator + "Settings.txt");
            FileWriter fw = new FileWriter(file, false);
            fw.write(SmartEditor.showFontCombo + "\n");
            fw.write(SmartEditor.showSizeSpin + "\n");
            fw.write(SmartEditor.showColorPick.getRGB() + "\n");
            fw.write(SmartEditor.showBackPick.getRGB() + "\n");
            fw.write(SmartEditor.defaultButtonSelected + "\n");
            fw.write(SmartEditor.autoSave + "\n");
            fw.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void copy() {
        if (ta.getSelectedText() != null && !ta.getSelectedText().equals("")) {
            userSelectedText = ta.getSelectedText();
        } else {
            userSelectedText = "";
        }
    }

    public static void paste() {
        if (ta.getSelectedText() != null) {
            ta.replaceSelection("");
        }
        ta.insert(userSelectedText, ta.getCaretPosition());
    }

    public static void cut() {
        if (ta.getSelectedText() != null && !ta.getSelectedText().equals("")) {
            userSelectedText = ta.getSelectedText();
            ta.replaceSelection("");
        } else {
            userSelectedText = "";
        }
    }

    public static void makeNewTa(String setText, String title) {
        scrollP.setAutoscrolls(true);
        scrollP.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollP.getViewport().add(ta);
        ta.setFont(new Font(SmartEditor.showFontCombo, Font.PLAIN, SmartEditor.showSizeSpin));
        ta.setLineWrap(true);
        ta.setBackground(SmartEditor.showBackPick);
        ta.setForeground(SmartEditor.showColorPick);
        ta.getInputMap().clear();
        ta.getInputMap().put(KeyStroke.getKeyStroke("ctrl-a"), "select-all");
        ta.getInputMap().put(KeyStroke.getKeyStroke("alt-del"), "delete-word");
        ta.getInputMap().put(KeyStroke.getKeyStroke("ctrl-z"), "undo");
        ta.getInputMap().put(KeyStroke.getKeyStroke("tab"), "tab-insert");
        ta.getInputMap().put(KeyStroke.getKeyStroke("ctrl-q"), "quit");

        ta.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 17) {
                    ctrlPressed = true;
                } else {
                    if (ctrlPressed) {
                        if (e.getKeyCode() == 67) {
                            copy();
                        } else if (e.getKeyCode() == 86) {
                            paste();
                        } else if (e.getKeyCode() == 88) {
                            cut();
                        }
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == 17) {
                    ctrlPressed = false;
                }
            }
        });
        if(title.isEmpty()) {
            filename = null;
            filepath = null;
        }

        ta.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                isSaved = false;
                if(SmartEditor.autoSave && counter == 1){
                    System.out.println(filename);
                    System.out.println(filepath);
                    if(filename == null || filepath == null) {
                        saveFile(filename);
                        isSaved = true;
                    }
                    counter++;
                }else if(SmartEditor.autoSave){
                    if(ta.getText().length() < 8000) {
                        if (counter % 4 == 0) {
                            saveFile(filename);
                        }
                    }else if(ta.getText().length() >= 8000){
                        if(counter % 20 == 0){
                            saveFile(filename);
                        }
                    }
                    counter++;
                }
                if(!SmartEditor.autoSave) {
                    String title = Frame.tb.getTitleAt(Frame.tb.getSelectedIndex());
                    if (!Frame.tb.getTitleAt(Frame.tb.getSelectedIndex()).contains(" *")) {
                        Frame.tb.setTitleAt(Frame.tb.getSelectedIndex(), title + " *");
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                isSaved = false;
                if(SmartEditor.autoSave && counter == 1){
                    saveFile(filename);
                    counter++;
                }else if(SmartEditor.autoSave){
                    if(ta.getText().length() < 8000) {
                        if (counter % 6 == 0) {
                            saveFile(filename);
                        }
                    }else if(ta.getText().length() >= 8000){
                        if(counter % 15 == 0){
                            saveFile(filename);
                        }
                    }
                    counter++;
                }
                if(!SmartEditor.autoSave) {
                    String title = Frame.tb.getTitleAt(Frame.tb.getSelectedIndex());
                    if (!Frame.tb.getTitleAt(Frame.tb.getSelectedIndex()).contains(" *")) {
                        Frame.tb.setTitleAt(Frame.tb.getSelectedIndex(), title + " *");
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                isSaved = false;
                if(SmartEditor.autoSave && counter == 1){
                    saveFile(filename);
                    counter++;
                }else if(SmartEditor.autoSave){
                    if(ta.getText().length() < 8000) {
                        if (counter % 6 == 0) {
                            saveFile(filename);
                        }
                    }else if(ta.getText().length() >= 8000){
                        if(counter % 20 == 0){
                            saveFile(filename);
                        }
                    }
                    counter++;
                }
                if(!SmartEditor.autoSave) {
                    String title = Frame.tb.getTitleAt(Frame.tb.getSelectedIndex());
                    if (!Frame.tb.getTitleAt(Frame.tb.getSelectedIndex()).contains(" *")) {
                        Frame.tb.setTitleAt(Frame.tb.getSelectedIndex(), title + " *");
                    }
                }
            }
        });
        ta.setText(setText);
        try {
            changeTheme(SmartEditor.defaultButtonSelected);
        } catch (Exception ignored) {
        }
        Frame.tb.add(title,scrollP);
    }
}