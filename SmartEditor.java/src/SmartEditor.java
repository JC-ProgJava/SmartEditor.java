import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.print.PrinterException;
import java.io.*;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/*
 * VERSION: 1.7
 * AUTHOR: JC-ProgJava
 * SPRINT-VERSION: 1.8
 * LICENSE: CC0 1.0 Universal
 * */

public class SmartEditor{
    public static boolean automaticSelect = true;
    public static int index = 0;
    public static String defaultButtonSelected = "Nimbus";
    public static int showSizeSpin = 14;
    public static String showFontCombo = "Noto Sans";
    public static Color showColorPick = new Color(0, 0, 0);
    public static Color showBackPick = new Color(255, 255, 255);
    public static boolean autoSave = false;
    public static boolean saveOnClose = false;
    public static boolean remindUpdate = true;
    public static String[] openRecentsFilePath = {"","","","","","","","","",""};
    public static String editorAppPath;

    static {
        try {
            editorAppPath = new File(SmartEditor.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException ignored) {
        }
    }

    public static String path = System.getProperty("user.home") + File.separator + "SmartEditor" + File.separator + "Settings.txt";
    public static String createFolderPath = System.getProperty("user.home") + File.separator + "SmartEditor";
    public static String fileDirectoryPath = "";
    public static String version = "1.7";

    public static void main(String[] args) throws Exception {
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
            SmartEditor.saveOnClose = input.nextBoolean();
            SmartEditor.remindUpdate = input.nextBoolean();
            int countIndex = 0;
            while(input.hasNextLine()) {
                SmartEditor.openRecentsFilePath[countIndex] = input.nextLine();
                countIndex++;
            }
        } catch (Exception e) {
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
                fw.write(saveOnClose + "\n");
                fw.write(remindUpdate + "\n");
                fw.close();
            } catch (IOException x) {
                x.printStackTrace();
            }
        }

        File pathApp = new File(createFolderPath + File.separator + "path.txt");
        FileWriter fww = new FileWriter(pathApp, false);
        fww.write(SmartEditor.editorAppPath + "\n");
        fww.close();

        Frame frame = new Frame();
        frame.createFrame();
        checkForUpdates("auto");
    }
    public static void checkForUpdates(String autoManual) throws IOException {
        URL versionURl = new URL("https://raw.githubusercontent.com/JC-ProgJava/SmartEditor.java/master/docs/version.txt");
        Scanner input = new Scanner(versionURl.openStream());
        while(input.hasNext()) {
            String line = input.nextLine();
            if (Double.parseDouble(line) > Double.parseDouble(version) && remindUpdate) {
                JDialog log = new JDialog();
                log.setSize(380,80);
                log.setTitle("Version Update");
                JLabel lab = new JLabel("Version " + line + " of SmartEditor is available, do you want to update?");
                JButton dontRemind = new JButton("Don't Remind Me");
                JButton cancel = new JButton("Cancel");
                JButton updatePlease = new JButton("Update");
                JPanel pane = new JPanel();
                pane.add(lab);
                pane.add(dontRemind);
                pane.add(cancel);
                pane.add(updatePlease);
                log.add(pane);
                log.setVisible(true);
                dontRemind.addActionListener(e -> {
                    SmartEditor.remindUpdate = false;
                    Frame.writeFile();
                    log.setVisible(false);
                    log.dispose();
                });

                cancel.addActionListener(e -> {
                    log.setVisible(false);
                    log.dispose();
                });

                updatePlease.addActionListener(e -> {
                    if(!Frame.ta.getText().isEmpty()) {
                        File file = new File(createFolderPath + File.separator + "temp.txt");
                        try {
                            FileWriter fw = new FileWriter(file, false);
                            if(!Frame.filepath.isEmpty()) {
                                fw.write(Frame.filepath + "\n");
                            }
                            fw.write(Frame.ta.getText() + "\n");
                            fw.close();
                        } catch (IOException ignored) {
                        }
                    }
                    try {
                        InputStream inputStream = new URL("https://github.com/JC-ProgJava/SmartEditor.java/blob/master/update.jar?raw=true").openStream();
                        Files.copy(inputStream, Paths.get(createFolderPath + File.separator + "update.jar"), StandardCopyOption.REPLACE_EXISTING);
                        Process proc = Runtime.getRuntime().exec("java -jar " + createFolderPath + File.separator + "update.jar");
                    } catch (IOException ex) {
                        System.exit(-1);
                    }
                    System.exit(1);
                });
            }else if(Double.parseDouble(line) <= Double.parseDouble(version) && autoManual.equals("manual")){
                JDialog log = new JDialog();
                log.setSize(300,80);
                JLabel lab = new JLabel("SmartEditor version " + version + " is the latest available.");
                JButton but = new JButton("OK");
                but.addActionListener(e -> {
                    log.dispose();
                });
                JPanel pane = new JPanel();
                pane.add(lab);
                pane.add(but);
                log.add(pane);
                log.setVisible(true);
                log.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            }
        }
    }
}

class Frame {
    public static String filename = null;
    public static String saveText = "";
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

    public static void createFrame() throws Exception, Error{
        m = m.modPow(dd, nn);
        tb = new JTabbedPane();
        editorFrame.add(tb);
        editorFrame.setSize(600,600);
        editorFrame.setVisible(true);
        File files = new File(SmartEditor.createFolderPath + File.separator + "temp.txt");
        if(!files.exists()) {
            makeNewTa("", "Untitled.txt");
        }else{
            String path = "Untitled.txt *";
            Scanner fr = new Scanner(files);
            String x = "";
            while(fr.hasNextLine()){
                String append = fr.nextLine();
                Path p = Paths.get(append);
                if(p.isAbsolute()){
                    path = p.toString();
                }else {
                    x += append + "\n";
                }
            }

            makeNewTa(x, path);
            if(path.equals("Untitled.txt *")){
                isSaved = false;
            }
            Files.delete(Paths.get(files.getAbsolutePath()));
        }
        editorFrame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                if (isSaved) {
                    System.exit(1);
                } else {
                    if(SmartEditor.autoSave || SmartEditor.saveOnClose){
                        saveFile(filename);
                        System.exit(1);
                    }else {
                        /*
                        if (Objects.equals(filename, "Untitled.txt") && filepath.equals("") && !isSaved) {
                            saveFile(filename);
                        } else {

                         */
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
                        //}
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
        JMenu openRecent = new JMenu("Open Recent");
        updateList(openRecent);
        JMenuItem save = new JMenuItem("Save");
        JMenuItem rename = new JMenuItem("Rename");
        JMenuItem forceCheck = new JMenuItem("Check for Updates");
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

            JLabel saveOnClose = new JLabel("Save On Close");
            JCheckBox saveOnCloseT = new JCheckBox();
            saveOnCloseT.setSelected(SmartEditor.saveOnClose);
            saveOnCloseT.addChangeListener(e12 -> {
                if(saveOnCloseT.isSelected()){
                    SmartEditor.saveOnClose = true;
                }else if(!autoSaveT.isSelected()){
                    SmartEditor.saveOnClose = false;
                }
            });

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            try {

                InputStream stream = SmartEditor.class.getResourceAsStream("NotoSans" + File.separator + "NotoSans-Light.ttf");
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
                stream = SmartEditor.class.getResourceAsStream("NotoSans" + File.separator + "NotoSans-Regular.ttf");
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
                stream = SmartEditor.class.getResourceAsStream("NotoSans" + File.separator + "NotoSans-Thin.ttf");
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
                stream = SmartEditor.class.getResourceAsStream("NotoSerif" + File.separator + "NotoSerif-Light.ttf");
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
                stream = SmartEditor.class.getResourceAsStream("NotoSerif" + File.separator + "NotoSerif-Regular.ttf");
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
                stream = SmartEditor.class.getResourceAsStream("NotoSerif" + File.separator + "NotoSerif-Thin.ttf");
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
            }catch(Exception ex){
                ex.printStackTrace();
            }

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
            addToSettings.add(saveOnClose, c);
            addToSettings.add(saveOnCloseT, c);
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
                        boolean isEmpty = false;
                        boolean filledBlank = false;
                        for(int x = 0; x < SmartEditor.openRecentsFilePath.length - 1; x++){
                            if(SmartEditor.openRecentsFilePath[x].equals(filepath)){
                                break;
                            }else if(SmartEditor.openRecentsFilePath[x].isEmpty() && !filledBlank){
                                SmartEditor.openRecentsFilePath[x] = filepath;
                                filledBlank = true;
                            }else{
                                isEmpty = true;
                            }
                        }

                        if(!isEmpty){
                            int position = SmartEditor.openRecentsFilePath.length - 1;
                            for (int i = (position - 1); i >= 0; i--) {
                                SmartEditor.openRecentsFilePath[i+1] = SmartEditor.openRecentsFilePath[i];
                            }
                            SmartEditor.openRecentsFilePath[0] = filepath;
                        }
                        writeFile();
                        updateList(openRecent);
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
                            saveText = sl;
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
                if(SmartEditor.autoSave || SmartEditor.saveOnClose) {
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

            if(ta.getSelectedText() != null){
                findTa.setText(ta.getSelectedText());
            }

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
                start.clear();
                end.clear();
                if(!findTa.getText().isEmpty()) {
                    String findInHere = ta.getText();
                    String findText = findTa.getText();
                    int index = findInHere.indexOf(findText);
                    int endIndex = index + findText.length();
                    start.add(index);
                    end.add(endIndex);
                    do {
                        index = findInHere.indexOf(findText, index + 1);
                        if (index == -1) {
                            break;
                        }
                        endIndex = index + findText.length();
                        start.add(index);
                        end.add(endIndex);
                    } while ((index > 0));
                    Collections.sort(start);
                    Collections.sort(end);
                }

                if (!findTa.getText().isEmpty() && ta.getText().contains(findTa.getText())) {
                    ta.requestFocus();
                    if (SmartEditor.index < start.size()) {
                        ta.select(start.get(SmartEditor.index), end.get(SmartEditor.index));
                        SmartEditor.index++;
                    }else{
                        SmartEditor.index = 0;
                        ta.select(start.get(SmartEditor.index), end.get(SmartEditor.index));
                        SmartEditor.index++;
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

        forceCheck.addActionListener(e -> {
            try {
                SmartEditor.remindUpdate = true;
                writeFile();
                SmartEditor.checkForUpdates("manual");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        menu.add(settings);
        menu.add(theme);
        menu.add(newMenu);
        menu.add(open);
        menu.add(openRecent);
        menu.add(save);
        menu.add(rename);
        menu.add(print);
        menu.add(forceCheck);
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
                saveText = ta.getText();
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

    public static void updateList(JMenu openRecent){
        openRecent.removeAll();
        int size = 0;
        for(String x : SmartEditor.openRecentsFilePath){
            if(!x.isEmpty()){
                size++;
            }
        }
        String[] suitable = new String[size];
        size = 0;
        for(String x : SmartEditor.openRecentsFilePath){
            if(!x.isEmpty()){
                suitable[size] = x;
                size++;
            }
        }
        JList lister = new JList<>(suitable);
        openRecent.add(lister);

        lister.addListSelectionListener(e -> {
            if(!ta.getText().equals("")) {
                saveFile(filename);
            }
            ta.setText("");
            File file = new File(lister.getSelectedValue().toString());
            SmartEditor.fileDirectoryPath = file.getParent();
            if(!file.getAbsoluteFile().equals(filepath)) {
                filepath = file.getAbsolutePath();
                boolean isEmpty = false;
                boolean filledBlank = false;
                for(int x = 0; x < SmartEditor.openRecentsFilePath.length - 1; x++){
                    if(SmartEditor.openRecentsFilePath[x].equals(filepath)){
                        isEmpty = true;
                        break;
                    }else if(SmartEditor.openRecentsFilePath[x].isEmpty() && !filledBlank){
                        SmartEditor.openRecentsFilePath[x] = filepath;
                        filledBlank = true;
                    }else{
                        isEmpty = true;
                    }
                }

                if(!isEmpty){
                    int position = SmartEditor.openRecentsFilePath.length - 1;
                    for (int i = (position - 1); i >= 0; i--) {
                        SmartEditor.openRecentsFilePath[i+1] = SmartEditor.openRecentsFilePath[i];
                    }
                    SmartEditor.openRecentsFilePath[0] = filepath;
                }
                writeFile();
                updateList(openRecent);
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
                    saveText = sl;
                    makeNewTa(sl, file.getName());
                    sl = "";
                    s1 = "";
                } catch (Exception ex) {
                    System.exit(0);
                }
            }
        });
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
            fw.write(SmartEditor.saveOnClose + "\n");
            fw.write(SmartEditor.remindUpdate + "\n");
            for(String x : SmartEditor.openRecentsFilePath){
                if(!x.isEmpty()){
                    fw.write(x + "\n");
                }
            }
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
        ta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.isPopupTrigger() && ta.getSelectedText() != null && !ta.getSelectedText().isEmpty() && ta.getSelectedText().contains(" ")){
                    int caretPosition = ta.getCaretPosition();
                    JPopupMenu jpm = new JPopupMenu();
                    String[] insertPhoneme = {"Insert Phoneme Onto Text"};
                    JList phoneme = new JList<>(insertPhoneme);
                    phoneme.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
                    phoneme.setFocusable(true);
                    String phonemeStri = getStringPhoneme(ta.getSelectedText());
                    phoneme.addListSelectionListener(e2 -> {
                        String insert2 = "("+ phonemeStri + ")" + ta.getSelectedText();
                        ta.replaceSelection("");
                        try {
                            ta.getDocument().insertString( ta.getCaretPosition(),insert2,null);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                        jpm.setVisible(false);
                    });
                    JLabel hoverPhoneme = new JLabel(phonemeStri);
                    jpm.add(hoverPhoneme);
                    jpm.add(phoneme);
                    jpm.show(e.getComponent(), e.getX(), e.getY());
                }else if(e.isPopupTrigger() && !ta.getSelectedText().contains(" ")){
                    JPopupMenu jpm = new JPopupMenu();
                    jpm.setFocusable(true);
                    String[] insertPhoneme = {"Insert Phoneme Onto Text"};
                    JList phoneme = new JList<>(insertPhoneme);
                    phoneme.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
                    phoneme.setFocusable(true);
                    String phonemeStri = getStringPhoneme(ta.getSelectedText().toLowerCase());
                    JLabel hoverPhoneme = new JLabel(phonemeStri);
                    ArrayList<String> wordList = new ArrayList<>();
                    try {
                        wordList = getBestChoiceWord(ta.getSelectedText().toLowerCase());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Object[] x = wordList.stream().filter(s -> s.startsWith(ta.getSelectedText().substring(0,1))).limit(20).toArray();
                    JList listComp = new JList<>(x);
                    listComp.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
                    listComp.setFocusable(true);

                    listComp.addListSelectionListener(e2 -> {
                        try {
                            String xy = listComp.getSelectedValue().toString();
                            xy = xy.split(" ")[0];
                            ta.replaceSelection("");
                            ta.getDocument().insertString(ta.getCaretPosition(), xy, null);
                            jpm.setVisible(false);
                        } catch (BadLocationException ignored) {
                        }
                    });

                    phoneme.addListSelectionListener(e2 -> {
                        String insert2 = "("+ phonemeStri + ")" + ta.getSelectedText();
                        System.out.println(insert2);
                        ta.replaceSelection("");
                        try {
                            ta.getDocument().insertString( ta.getCaretPosition(),insert2,null);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                        jpm.setVisible(false);
                    });
                    jpm.add(listComp);
                    jpm.add(hoverPhoneme);
                    jpm.add(phoneme);
                    jpm.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            @Override
            public void mousePressed(MouseEvent me) {
                if(me.isPopupTrigger() && ta.getSelectedText() != null && !ta.getSelectedText().isEmpty() && ta.getSelectedText().contains(" ")){
                    int caretPosition = ta.getCaretPosition();
                    JPopupMenu jpm = new JPopupMenu();
                    String[] insertPhoneme = {"Insert Phoneme Onto Text"};
                    JList phoneme = new JList<>(insertPhoneme);
                    phoneme.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
                    phoneme.setFocusable(true);
                    String phonemeStri = getStringPhoneme(ta.getSelectedText());
                    phoneme.addListSelectionListener(e2 -> {
                        String insert2 = "("+ phonemeStri + ")" + ta.getSelectedText();
                        System.out.println(insert2);
                        ta.replaceSelection("");
                        try {
                            ta.getDocument().insertString( ta.getCaretPosition(),insert2,null);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                        jpm.setVisible(false);
                    });
                    JLabel hoverPhoneme = new JLabel(phonemeStri);
                    jpm.add(hoverPhoneme);
                    jpm.add(phoneme);
                    jpm.show(me.getComponent(), me.getX(), me.getY());
                }else if(me.isPopupTrigger() && !ta.getSelectedText().contains(" ")){
                    JPopupMenu jpm = new JPopupMenu();
                    jpm.setFocusable(true);
                    String[] insertPhoneme = {"Insert Phoneme Onto Text"};
                    JList phoneme = new JList<>(insertPhoneme);
                    phoneme.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
                    phoneme.setFocusable(true);
                    String phonemeStri = getStringPhoneme(ta.getSelectedText().toLowerCase());
                    JLabel hoverPhoneme = new JLabel(phonemeStri);
                    ArrayList<String> wordList = new ArrayList<>();
                    try {
                        wordList = getBestChoiceWord(ta.getSelectedText().toLowerCase());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    Object[] x = wordList.stream().filter(s -> s.startsWith(ta.getSelectedText().substring(0,1))).limit(20).toArray();
                    JList listComp = new JList<>(x);
                    listComp.setBorder(BorderFactory.createEmptyBorder(0, 2, 5, 2));
                    listComp.setFocusable(true);

                    listComp.addListSelectionListener(e2 -> {
                        try {
                            String xy = listComp.getSelectedValue().toString();
                            xy = xy.split(" ")[0];
                            ta.replaceSelection("");
                            ta.getDocument().insertString(ta.getCaretPosition(), xy, null);
                            jpm.setVisible(false);
                        } catch (BadLocationException ignored) {
                        }
                    });

                    phoneme.addListSelectionListener(e2 -> {
                        String insert2 = "("+ phonemeStri + ")" + ta.getSelectedText();
                        System.out.println(insert2);
                        ta.replaceSelection("");
                        try {
                            ta.getDocument().insertString( ta.getCaretPosition(),insert2,null);
                        } catch (BadLocationException ex) {
                            ex.printStackTrace();
                        }
                        jpm.setVisible(false);
                    });
                    jpm.add(listComp);
                    jpm.add(hoverPhoneme);
                    jpm.add(phoneme);
                    jpm.show(me.getComponent(), me.getX(), me.getY());
                }
            }
        });

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

        ta.setText(setText);
        ta.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if(!saveText.equals(ta.getText())) {
                    isSaved = false;
                    if (SmartEditor.autoSave && counter == 1) {
                        if (filename == null || filepath == null) {
                            saveFile(filename);
                        }
                        counter++;
                    } else if (SmartEditor.autoSave) {
                        if (ta.getText().length() < 8000) {
                            if (counter % 4 == 0) {
                                saveFile(filename);
                            }
                        } else if (ta.getText().length() >= 8000) {
                            if (counter % 20 == 0) {
                                saveFile(filename);
                            }
                        }
                        counter++;
                    }
                    if (!SmartEditor.autoSave) {
                        String title = Frame.tb.getTitleAt(Frame.tb.getSelectedIndex());
                        if (!Frame.tb.getTitleAt(Frame.tb.getSelectedIndex()).contains(" *")) {
                            Frame.tb.setTitleAt(Frame.tb.getSelectedIndex(), title + " *");
                        }
                    }
                }else {
                    isSaved = true;
                    String title = Frame.tb.getTitleAt(Frame.tb.getSelectedIndex());
                    title = title.substring(0, title.length() - 2);
                    if (Frame.tb.getTitleAt(Frame.tb.getSelectedIndex()).contains(" *")) {
                        Frame.tb.setTitleAt(Frame.tb.getSelectedIndex(), title);
                    }
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                if(!saveText.equals(ta.getText())) {
                    isSaved = false;
                    if (SmartEditor.autoSave && counter == 1) {
                        if (filename == null || filepath == null) {
                            saveFile(filename);
                        }
                        counter++;
                    } else if (SmartEditor.autoSave) {
                        if (ta.getText().length() < 8000) {
                            if (counter % 4 == 0) {
                                saveFile(filename);
                            }
                        } else if (ta.getText().length() >= 8000) {
                            if (counter % 20 == 0) {
                                saveFile(filename);
                            }
                        }
                        counter++;
                    }
                    if (!SmartEditor.autoSave) {
                        String title = Frame.tb.getTitleAt(Frame.tb.getSelectedIndex());
                        if (!Frame.tb.getTitleAt(Frame.tb.getSelectedIndex()).contains(" *")) {
                            Frame.tb.setTitleAt(Frame.tb.getSelectedIndex(), title + " *");
                        }
                    }
                }else {
                    isSaved = true;
                    String title = Frame.tb.getTitleAt(Frame.tb.getSelectedIndex());
                    title = title.substring(0, title.length() - 2);
                    if (Frame.tb.getTitleAt(Frame.tb.getSelectedIndex()).contains(" *")) {
                        Frame.tb.setTitleAt(Frame.tb.getSelectedIndex(), title);
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                if(!saveText.equals(ta.getText())) {
                    isSaved = false;
                    if (SmartEditor.autoSave && counter == 1) {
                        if (filename == null || filepath == null) {
                            saveFile(filename);
                        }
                        counter++;
                    } else if (SmartEditor.autoSave) {
                        if (ta.getText().length() < 8000) {
                            if (counter % 4 == 0) {
                                saveFile(filename);
                            }
                        } else if (ta.getText().length() >= 8000) {
                            if (counter % 20 == 0) {
                                saveFile(filename);
                            }
                        }
                        counter++;
                    }
                    if (!SmartEditor.autoSave) {
                        String title = Frame.tb.getTitleAt(Frame.tb.getSelectedIndex());
                        if (!Frame.tb.getTitleAt(Frame.tb.getSelectedIndex()).contains(" *")) {
                            Frame.tb.setTitleAt(Frame.tb.getSelectedIndex(), title + " *");
                        }
                    }
                }else {
                    isSaved = true;
                    String title = Frame.tb.getTitleAt(Frame.tb.getSelectedIndex());
                    title = title.substring(0, title.length() - 2);
                    if (Frame.tb.getTitleAt(Frame.tb.getSelectedIndex()).contains(" *")) {
                        Frame.tb.setTitleAt(Frame.tb.getSelectedIndex(), title);
                    }
                }
            }
        });
        try {
            changeTheme(SmartEditor.defaultButtonSelected);
        } catch (Exception ignored) {
        }
        Frame.tb.add(title,scrollP);
    }

    public static String getStringPhoneme(String taText){
        String[][] phoneme = {
                {"a", "ah-"}, {"b", "bu-"}, {"c", "k-", "ss-"},
                {"d", "de-"}, {"e", "ee-"}, {"f", "ff-"},
                {"g", "ge-"}, {"h", "hh-"}, {"i", "ii-"},
                {"j", "ju-"}, {"k", "ka-"}, {"l","le-"},
                {"m", "me-"}, {"n", "ne-"}, {"o","ow-"},
                {"p", "peh-"}, {"q", "qu-"}, {"r", "reh-"},
                {"s", "ss-"}, {"t", "tuh-"}, {"u", "uh-"},
                {"v", "vv-"}, {"w", "wuh-"}, {"x", "cxh-"},
                {"y", "e-", "yeh-"}, {"z", "zz-"}
        };
        char[] alphabet = {'a','b','c','d','e','f',
                'g','h','i','j','k','l','m','n','o','p',
                'q','r','s','t','u','v','w','x','y','z'};
        char[] taChar = taText.toCharArray();
        String returned = "";
        int indexer = 0;
        int indexer2 = 0;
        for(char x : taChar){
            for(char y : alphabet) {
                if(y == x && x == 'c'){
                    if(taChar.length >= indexer + 2){
                        if(taChar[indexer + 1] == 'e' || taChar[indexer + 1] == 'i' || taChar[indexer + 1] == 'y'){
                            returned += "ss-";
                        }else{
                            returned += "k-";
                        }
                    }
                }else if(y == x && x == 'y'){
                    if(indexer >= 1){
                        if(taChar[indexer - 1] == 'l'){
                            returned += "e-";
                        }else{
                            returned += "yeh-";
                        }
                    }else{
                        returned += "yeh-";
                    }
                }else if (y == x) {
                    returned += phoneme[indexer2][1];
                }
                indexer2++;
            }
            indexer2 = 0;
            indexer++;
        }
        returned = returned.substring(0, returned.length() - 1);
        return returned;
    }

    public static ArrayList<String> getBestChoiceWord(String word) throws URISyntaxException {
        int correct = 0;
        ArrayList<String> listed = new ArrayList<>();
        BufferedReader readers = new BufferedReader(new InputStreamReader(SmartEditor.class.getResourceAsStream("words.txt")));
        Scanner reader = null;
        reader = new Scanner(readers);
        while(reader.hasNext()){
            double percent;
            String compareToThis = reader.nextLine();
            char[] s1 = word.toCharArray();
            char[] s2 = compareToThis.toCharArray();
            int maxlen = Math.min(s1.length, s2.length);
            for (int index = 0; index < maxlen; index++) {
                String x = String.valueOf(s1[index]);
                String y = String.valueOf(s2[index]);
                if (x.equals(y)) {
                    correct++;
                }
            }
            double length = Math.max(s1.length, s2.length);
            percent = correct / length;
            percent *= 100;
            boolean perfect = false;
            if (percent > 80 && compareToThis.charAt(0) == word.charAt(0)) {
                if(String.valueOf(percent).equals("100.00")){
                    perfect = true;
                }
                String addtoit = compareToThis + " : " + String.format("%.2f", percent) + "% similar.";
                listed.add(addtoit);
            }
            if(compareToThis.contains(word) && !perfect && word.length() * 2 > compareToThis.length()){
                String addtoit = compareToThis + " : 80.00% similar.";
                listed.add(addtoit);
            }
            correct = 0;
        }

        for(String x : listed){
            if(x.contains("100.00% similar.")){
                listed.clear();
                listed.add(x);
                break;
            }
        }
        return listed;
    }
}