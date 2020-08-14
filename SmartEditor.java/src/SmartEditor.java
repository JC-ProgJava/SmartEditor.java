import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;
import javax.swing.text.*;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

/*
 * VERSION: 1.8
 * AUTHOR: JC-ProgJava
 * SPRINT-VERSION: 1.9
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
    public static String version = "1.8";

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

    public static void checkForUpdates(String autoManual) {
        URL versionURl = null;
        try {
            versionURl = new URL("https://raw.githubusercontent.com/JC-ProgJava/SmartEditor.java/master/docs/version.txt");
        } catch (MalformedURLException ignored) {
        }
        Scanner input = null;
        try {
            input = new Scanner(versionURl.openStream());
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
                    });

                    cancel.addActionListener(e -> {
                        log.setVisible(false);
                    });

                    updatePlease.addActionListener(e -> {
                        updateStartProc();
                    });
                }else if(Double.parseDouble(line) <= Double.parseDouble(version) && autoManual.equals("manual")){
                    JDialog log = new JDialog();
                    log.setSize(300,80);
                    JLabel lab = new JLabel("SmartEditor version " + version + " is the latest available.");
                    JButton but = new JButton("OK");
                    but.addActionListener(e -> {
                        log.setVisible(false);
                    });
                    JPanel pane = new JPanel();
                    pane.add(lab);
                    pane.add(but);
                    log.add(pane);
                    log.setVisible(true);
                    log.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
                }
            }
        } catch (IOException | NullPointerException e) {
            if(autoManual.equals("manual")) {
                JDialog d = new JDialog();
                d.setSize(390, 60);
                d.setTitle("Not Connected Error");
                JTextArea labe = new JTextArea("Due to no internet connection, you cannot update to a newer version of SmartEditor.");
                labe.setEditable(false);
                labe.setLineWrap(true);
                d.add(new JPanel().add(labe));
                d.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        d.setVisible(false);
                    }
                });
                d.setVisible(true);
            }
        }
    }

    public static void changePG(JProgressBar pg){
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    while(pg.getValue() < pg.getMaximum()){
                        pg.setValue(pg.getValue() + 1);
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ignored) {
                        }
                    }
                }
            });
        } catch (Exception ignored){
        }
    }

    public static void updateStartProc(){
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception ignored){
        }
        Random rand = new Random();
        JFrame fram = new JFrame("SmartEditor Launcher");
        JProgressBar pg = new JProgressBar(0, 100);
        JLabel labb = new JLabel("Progress");
        JPanel panel = new JPanel();
        panel.add(labb);
        panel.add(pg);
        fram.add(panel);
        labb.setToolTipText("Update In Progress");
        fram.setSize(230, 60);
        fram.setVisible(true);
        changePG(pg);
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
    public static JMenu openRecent = null;
    public static ArrayList<Integer> start = new ArrayList<>();
    public static ArrayList<Integer> end = new ArrayList<>();
    public static String userSelectedText = "";
    public static JTextArea ta = new JTextArea();
    public static JScrollPane scrollP = new JScrollPane();
    private static boolean ctrlPressed = false;
    private static int counter = 1;
    protected static UndoManager undo = new UndoManager();
    public static UndoAction undoAction = null;
    public static RedoAction redoAction = null;
    public static BigInteger m = new BigInteger("141454143176403454400219135837327732432275391005142158266224006510388776700561409211802849252126151544929332932462128674996228624304942246306924400731952726425001628630442900248338532810058948656384538463012209886782235855216966200019635970109509295096005537748951176645504779390877631418234458290983187967236710086909246888121825811028395588956507472863394677362116765145917038888133993839852588897739095132109596122021890840758768139568883435271992354670533963934984682703409097954612918693351285924295208055311860363202881849788543679246596575416650360911674034287739287126556865064968228005691898974033426116845376092213777807033800095580144681763087137165235145868543476408841262166143945437144867747893342968407113491611876089307820912778343295537828418019940655964042152746158715873715736907163681720937666941187263325241619170386418549491338434240827204552228848029466302364271851580914282991897367471139979640979468150169065552403142181296005861867278633735020696336309145223148869228923673715082411929802022789428724884565874334045368907130669595811285379310237531254922696767967772383660208847602357885779474317616725974699403015994021995189308859318633832134531355948829210905835312070803047946827056991592314119505885407986938577732793877734110656327239536135217425173500858422791947718783291096502251168840785912517512106883351528637979349914096127701955659783560598632841481653310948501566274024480098265015970701981424833328821153846353921965547048153973198571290885730558848183807447168502346610926574628447686252198955884239718450225153231140739137173436639926409839231780861629976130742620821454990909542239688847418987290888098307273163108799748877423690834467905320218600419625726259373578823253357999815169085036184895265806646915718501815112053742595667404741744525154983358404612726459806423208147493528231953958713544098299432802763071233237694578713381552190548943297153817523491597385701743290805951667337704259042809216729320051216757666785151262413907851980759414803097757813116790228589484612205114247417370488659350903979361146103991447322398996980112603295099695307918328583572511852557340936872453893249412078450807803490098783450271232908501283178824504490204411316682421985404655826589349021814861209198033540748900974859549743469927511108093407559254157282842349967213546229736448983615161301760761695584961923773438615323336040807801842527138223939222971602609348460581784036517126192543016766873427417240830763637887435891655487");
    public static BigInteger dd = new BigInteger("349020856655650381979919305862069783089910970335595541164468767124569338773647735004861381094869076228360683493981951169808271706292048565532544963929029472832046511623448009742148850942352360461302534139689251860733224544244930689737723253224043335525810877161699751033428777736218728053417256881726733791393453326669549367582159277586892180035837477426139241438998430751226803065232789595197523362088918203181927419158205816162891250476881237750146818100591335356063596214803610708619889081324962860255778328563697639461712520711155052315421634511860612793638142875281126534283694303319261366750699652346672307112723946732259731611707840830366982042721259850515782775500491482050842951331550355745149823262794754989063287855844089369875656676262509236965134598420055479240785338864752018851083398430396569887904055193313180355807721745989102957258387766387620080880386833426858965054160128243679217982346601656020121748138323429235456447944496695129652957299103225593143256407729073802264117190744909319277036994656632482595818106455452207396540158455731144626298743870997554515042756226888519484770607783046667009428941846460253642986105031432659987151869510949940610236545253994362422499121123939224245779006043391838049664186364108385449511288356812632628017123650174708118529024543365924750018993618507457201786338611710588807466774455296967185252338448301808759097238994006898355575431481510693256966537448969180891868328510724715920657547668505715551884859093599967889111108012727968734068151334676897292006510963970074821991407850551381504897616121163371386443451408947814997725899312739900157001701722787026670043483500212171696940600417116780318546167468581936644634650095655382646420759733341031850266110830916712030660840797616107962832786495654193447897095965300417802980211432329111023200341423629099428461581616529831176175296210440802484414876131761691930914063717965550546616320738808657074975761812502433108454504641520166376391379415575204857308333734073786386620746409652992233523617596115208765424605302623307467352677091710061084153946612474984484922995192412446315551173528045461548591734444820229884230164763252882406259767632974516757709764507713091093003213200192866681794296447923355077455250957275139002476020375413261585859501544177606153768351841114044506462221596478156547311857309452701506027743703754976770741398989823890409584906910894778947213694715074247056916478921038941241257216424592706049466191783276272683462382296934070763");
    public static BigInteger nn = new BigInteger("355384042261149211647430327594305415612023926622472184221296847470774075781913055403379241414130239490104257231224641224810560689274073907810080382615334183120537291271958245297389389340813393323334598712531944063440305904645469914622439588528632141443833200349228957251884127724640806690383205532601307781604394073800298271408346595231248092720665533042066730658078432334715627102627564009809513689430521481442541925670756605893934509654700298080490324279435410759057075316114274825994292657497804940304477432394794730201044681647172808649428054859387949624663663142618985297778623070198065349184686732177233804245032408707994761760813570235726603348497826175382959679754450519369206273685262563216109302303313677021908548220354823686801672709174014658103911080821970774007628772673514322323884398039454348172220107814665445096785526191431609635797571035765322985328936947237724567678475622660974648373010528949645488448186721152818933546400139214815561895094372867764926370490150760492968491990497339009254837432364893133319340766621624014869925118658021956138713370089221609537301767421943750573701490936751978327231303187604496564050184180070999977433026917692441220613708784459007158066082216500199768576030998520394602364996130566868698404441628876553233259225118034548625508101678591316506908912171638381579965281131219557104485774148241964095519554026186203874381607022217966291030360513376441124317044944375269708964959279628665781337420130291091792690681708427756661582959230933928949224129177504473341236842480357469130124305470258881479731991096414801404145592467142588044468455294467010618821479229596420141115445502625935950970995864580969421595557740119281569930594453853728061815247206439104427564692136060860673153806041751344691818462716467470014280491678755133421211668056637679657976466560258989945421671960339932429153629435759598467917863807765271222540136420932793177979198247328343742901449583275490938684187414499639412503686260755227678966260535902952941305161408928322838930278346070054907191865226518323616713544787353492260729578458458590290861185759333319551975219019149218606937249990583433109281787065428072766388161724440458163927904037883146180427923473087948003392507668439751310641062802782192903346027963761124179043500723250697248169743770403107905484718154624672262439373433305375962321963242562579272768788383123812648771002027436178366652936020519210834389869012762791100942150744551703036195389300170335129670368942732042407");
    public static String htmlText = "<!DOCTYPE HTML><html lang=\"en\"><head><meta charset=\"utf-8\"><title>SmartEditor</title><style>body{text-align: center; }:root {--main-font-color: black;--main-back-color: white;}nav{opacity: .9;text-align: left;font-family: sans-serif;font-size: 15px;font-weight: 200;padding: 20px;background-color: var(--main-back-color);top: 0;position: fixed;z-index: 5;width: 100%;transition: var(--main-back-color), 0.5s !important;}h1{text-align: center;font-family: sans-serif;font-size: 40px;font-weight: 200;color: var(----main-font-color);}h2{font-family: sans-serif;font-size: 30px;font-weight: 200;color: var(----main-font-color);}h3{font-family: sans-serif;font-size: 20px;font-weight: 200;color: var(----main-font-color);}h4 {font-family: sans-serif;font-size: 15px;font-weight: 200;color: var(----main-font-color);}h5 {font-family: sans-serif;font-size: 10px;font-weight: 200;color: var(----main-font-color);}a {margin-left: 15px;margin-right: 15px;margin-top: 10px;margin-bottom: 10px;text-align: center;text-decoration: none;transition: color 0.7s, var(--main-back-color) 0.5s, var(--main-font-color) 0.7s;color: var(----main-font-color);}a:hover{color: rgb(150,100,100);}.dark-mode{\t\t\t\t--main-font-color: white !important;\t\t\t\t--main-back-color: black !important;\t\t\t}\t\t\tbody{\t\t\t\tbackground: var(--main-back-color);\t\t\t\tcolor: var(--main-font-color);\t\t\t\ttransition: var(--main-font-color), 0.7s, var(--main-back-color), 0.5s !important;\t\t\t}div{margin-left: 3em;}button{\t\t\t\twidth: 20px;\t\t\t\theight: 20px;\t\t\t\tbackground-position: center;\t\t\t\tbackground-size: 20px;\t\t\t\toutline: none;\t\t\t\tdecoration: none;\t\t\t\tborder: none;\t\t\t}footer{width: 100%;}.underline{text-decoration: underline;}table{text-align: center;margin-left: auto;margin-right: auto;border-bottom: 40px;}td{margin-left: 10px;margin-right: 10px;}tr{grid-row-gap: 20px;}</style></head><body><br><br><br><h1 id=\"Heading\">SmartEditor, a lightweight yet powerful texteditor.</h1><h2 style=\"text-align: center; font-size: 30px;\">Built using <em>Java</em></h2><div><h2 id=\"Features\">SmartEditor Features</h2><table><tr><h3 >Settings for font, color and autosave functions</h3></tr><tr><h3 >Customize background color and font color</h3></tr><tr><h3 >Autosave functions for better productivity</h3></tr><tr><h3 >A powerful find & replace for enhanced editing</h3></tr><tr><h3 >15+ Themes for a better interface</h3></tr><tr><h3 >Rename Files On-The-Fly</h3></tr><tr><h3 >Small, Light-weight<br>Only 5 MB for <a style=\"margin: unset;\"href=\"#Download\">JAR</a></h3></tr><tr><h3 >Default settings are saved</h3></tr></table></div><div><h2 id=\"Download\">Download</h2><table><tr><th></th><th><h3>MacOS</h3></th><th><h3>Windows</h3></th></tr><tr><td><h3>Jar</h3></td><td><h3><a class=\"underline\" href=\"https://github.com/JC-ProgJava/SmartEditor.java/blob/master/SmartEditor.jar\">Download</a></h3></td><td><h3><a class=\"underline\" href=\"https://github.com/JC-ProgJava/SmartEditor.java/blob/master/SmartEditor.jar\">Download</a></h3></td></tr></table></div><div><h2 id=\"Contact\">Contact</h2><h3 >Please send your feedback through <a class=\"underline\" style=\"margin-left: 0;\" href=\"mailto:juch5796@gmail.com\">email</a></h3><h3 ><a target=\"_blank\" class=\"underline\" style=\"margin-left: 0;\" href=\"https://github.com/JC-ProgJava/SmartEditor.java\">Source Code</a></h3><h3 ><a target=\"_blank\" class=\"underline\" style=\"margin-left: 0;\" href=\"https://github.com/JC-ProgJava/SmartEditor.java/issues\">Bug Database</a></h3></div><footer ><h4>Under CC0 v1.0 Universal License by JC-ProgJava 2020</h4></footer></body></html>";
    public static String changelogBackUp = "Version 1.8 14082020:\n" +
            "\n" +
            "    New In This Version:\n" +
            "        New:\n" +
            "            - Improved Check For Updates function UI.\n" +
            "            - Added Changelog to menubar\n" +
            "            - Added About to menubar\n" +
            "            - Shifted/reorganised settings and menuitems.\n" +
            "            - Improved text completion suggestions and accuracy.\n" +
            "            - Resized settings pane so that button text is fully visible.\n" +
            "            - Tool tip text for assistance on settings.\n" +
            "            - Default font now Noto Sans Regular.\n" +
            "            - Added undo and redo functions to menu and key bindings.\n" +
            "                - binded to:\n" +
            "                    Undo: Ctrl-Z\n" +
            "                    Redo: Ctrl-R\n" +
            "\n" +
            "Version 1.7 23072020:\n" +
            "\n" +
            "    New In This Version:\n" +
            "        New:\n" +
            "            - Insert phoneme function fully functional.\n" +
            "            - Cleaned up archive and reduced file to 5.27MB\n" +
            "\n" +
            "        - Fixes (6)\n" +
            "            - 00021 -> Issue where update feature reminds user\n" +
            "                       to update even when they are on the current version.\n" +
            "            - 00022 -> Fixed issue where user may exit file they were\n" +
            "                       working on without save dialog showing.\n" +
            "            - 00023 -> Fixed issue where text completion is not\n" +
            "                       available for jar running SmartEditor.\n" +
            "            - #13 -> Open Recents.\n" +
            "                     Open recent function ->\n" +
            "                        Save filepaths to settings.txt\n" +
            "                        in user default directory.\n" +
            "            - #14 -> Pronunciation Guide\n" +
            "                     Pronunciation guide(double click, or phonetic guide text)\n" +
            "                     English\n" +
            "                     -> Put Phonemes for each word.\n" +
            "            - #16 -> Phoneme Function SPRINT\n" +
            "                      - words with 'y' or 'c' show multiple times on\n" +
            "                        phoneme dialog.\n" +
            "                      - The \"insert phoneme function\" does not work.\n" +
            "\n" +
            "\n" +
            "Version 1.6 19072020:\n" +
            "\n" +
            "    New In This Version:\n" +
            "        New:\n" +
            "            - Removed compatibility with MacOS Application due to delay in\n" +
            "              fixing issues.\n" +
            "            - Text Completion BETA OUT.\n" +
            "            - Phoneme renderer English version out.\n" +
            "\n" +
            "        - Fixes (3)\n" +
            "            - 00018 -> Update.jar now is fully compatible with Java Archive\n" +
            "                       version. Fixed issue where launcher wouldn't launch\n" +
            "                       jar file.\n" +
            "            - 00019 -> SmartEditor archive executable now can save currently\n" +
            "                       worked on docs and reopen after user updates with\n" +
            "                       update feature.\n" +
            "            - 00020 -> Fixed issue where unsaved text from before clicking\n" +
            "                       the update button will not render because of order\n" +
            "                       frame was setup in.\n" +
            "\n" +
            "\n" +
            "Version 1.5 13072020:\n" +
            "\n" +
            "    New In This Version:\n" +
            "        New:\n" +
            "            - Bugs are all imported into database on Github Repository.\n" +
            "              All bug reference numbers will now match accordingly.\n" +
            "              Undocumented bugs will be expressed in five digits(e.g., 00014)\n" +
            "            - Chinese Simplified and Traditional support with Arial Unicode MS.\n" +
            "            - Multilingual font support with Noto Sans & Noto Serif.\n" +
            "            - Default font is now Noto Sans.\n" +
            "            - Nimbus LAF is now the default theme.\n" +
            "            - Text Completion ALPHA OUT.\n" +
            "        - Fixes (8)\n" +
            "            - #4 -> Improve saving function symbol.\n" +
            "            - #6 -> Check For Updates Function\n" +
            "                    - After clicking \"Don't Remind Me\", you can still update by\n" +
            "                      finding \"Check for updates\" in the menu.\n" +
            "            - #7 -> New Option to Save on Close in settings.\n" +
            "            - #10 -> File Converter\n" +
            "            - #11 -> Text Completion ALPHA OUT.\n" +
            "            - 00015 -> Application path doesn't update in text file\n" +
            "                       -> Key to auto update feature.\n" +
            "            - 00016 -> Windows does not have a way to access popup menu.\n" +
            "                       Possibly because action performed is for linux.\n" +
            "                       Check on \"@Override\" annotation marked on dialog\n" +
            "                       (ta.getDocument().addMouseListener.)\n" +
            "            - 00017 -> Save dialog shows even though title shows\n" +
            "                       is saved(type hello and delete, then close)\n" +
            "\n" +
            "\n" +
            "Version 1.4 09072020:\n" +
            "\n" +
            "    New In This Version:\n" +
            "        - New\n" +
            "        - Find Selection option now available.\n" +
            "        - Fixes (4)\n" +
            "            - 00011 -> Fixed Find and Replace issue\n" +
            "                       - search always selects length of find word at the\n" +
            "                         top of the page\n" +
            "            - 00012 -> Fixed Find and Replace dialog\n" +
            "                       to not freeze on deleting a character\n" +
            "            - 00013 -> Fixed \"Find and Replace starts not on first\n" +
            "                       occurrence\" issue\n" +
            "            - 00014 -> Fixed \"User changes document but find &\n" +
            "                       replace doesn't update.\" issue\n" +
            "\n" +
            "\n" +
            "Version 1.3 07072020:\n" +
            "\n" +
            "    - Bugs Identified:\n" +
            "        Themes:\n" +
            "            - PGS theme cut, copy and paste functions duplicate copy text\n" +
            "              because PGS has custom paste functions\n" +
            "        Components:\n" +
            "            TextArea:\n" +
            "                - Emoji icons do not work\n" +
            "            TabbedPane:\n" +
            "                - Add shortcuts for navigation(tabbed panes key bindings)\n" +
            "                - Tabbed panes do not open multiple, always only one. (Sprint: 1.5)\n" +
            "                - Saving asterisk shown when unsaved shows file is unsaved\n" +
            "                  when user types and deletes or deletes and types same character\n" +
            "                  (does not recognize that file didn't really change\n" +
            "                  when user typed and deleted words)\n" +
            "        Find & Replace:\n" +
            "            - Find and replace starts not on first occurrence\n" +
            "            - Find and Replace issue\n" +
            "                - File search \"**********\" directs to top of page\n" +
            "                - after searching once, replacing the text freezes program.\n" +
            "\n" +
            "    - New Features Coming:\n" +
            "        - Method to \"Check for updates\" and update by downloading newer version.\n" +
            "        - Choose whether text should be saved in plain text or rich text\n" +
            "          and how to implement(show and edit) in the window.\n" +
            "        - Option to choose whether file saves automatically before\n" +
            "          program closes.\n" +
            "        - Project structure/path display\n" +
            "        - Tabbed panes for different files. (make sure user can access tabs\n" +
            "          using key bindings command+tab-number)\n" +
            "        - PDF creator.\n" +
            "        - Text completion ~ @textCompletionConcept.java\n" +
            "            - On selecting(double clicking text and two finger clicking)\n" +
            "              check for spelling mistakes in word\n" +
            "            - Highlight/underline words misspelled with checker\n" +
            "              every 5 character changes.\n" +
            "        - Make executable file(.dmg, .exe) for multi-platform use\n" +
            "          without JDK, etc.\n" +
            "        - Run Java and .txt(java) files\n" +
            "            - With compile\n" +
            "            - Without compile\n" +
            "            - Create folder with compile and source\n" +
            "                - Delete previous version\n" +
            "\n" +
            "    New In This Version:\n" +
            "    - Find and Replace\n" +
            "        - New \"Find Previous\" function added.\n" +
            "    - Rename file feature added to menu (Menu -> Rename).\n" +
            "    - Text completion Scheduled\n" +
            "        - Alpha (Sprint: 2.0)\n" +
            "        - Beta (Sprint: 2.4)\n" +
            "        - Implemented (Sprint: 3.0)\n" +
            "    - Fixes (7)\n" +
            "        - 00004 -> Fixed issue where file opened has a blank first line.\n" +
            "        - 00005 -> Fixed Find and Replace so selection does not repeat\n" +
            "                   on single occurrence.\n" +
            "        - 00006 -> Fixed file doesn't save when user opens another\n" +
            "                   file in same tab.\n" +
            "        - 00007 -> Setting metal P.L.A.F. as default may give\n" +
            "                   ocean theme instead of default-metal.\n" +
            "        - 00008 -> File is erased when user reopens\n" +
            "                   same file(unsaved).\n" +
            "        - 00009 -> Issue where user needs to save file(shows file dialog) when opening\n" +
            "                   from a blank file.\n" +
            "        - 00010 -> Issue where user saves file after unsaved prompt and\n" +
            "                   doesn't see frame(setVisible is false).\n" +
            "\n" +
            "\n" +
            "Version 1.2 04072020:\n" +
            "    - Introducing tabbed panes\n" +
            "        - for multiple windows (Sprint: 1.5)\n" +
            "        - Key bindings support (Sprint: 1.5)\n" +
            "    - Auto-save setting is available\n" +
            "    - MacOS Application available.\n" +
            "\n" +
            "Version 1.1 02072020:\n" +
            "    - Find and Replace function fully functional.\n" +
            "        - Option to replace all added.\n" +
            "        - Fixed issue where one occurrence is skipped.\n" +
            "    - Cut, copy and paste functions are now fully functional.\n" +
            "\n" +
            "Version 1.0 23062020:\n" +
            "    - Combined all of the following.\n" +
            "\n" +
            "Version 0.6 20062020:\n" +
            "    - Bug fixes and patches\n" +
            "    - Theme now can be set as default.\n" +
            "        - Themes instantly take effect when radio button clicked.\n" +
            "        - Themes are shown in a JList format as there are 19 themes.\n" +
            "        - More themes, below shown:\n" +
            "            - Metal (2)\n" +
            "                - Metal\n" +
            "                - Ocean\n" +
            "            - JTattoo (12)\n" +
            "                - Acryl\n" +
            "                - Aero\n" +
            "                - Aluminium\n" +
            "                - Bernstein\n" +
            "                - Fast\n" +
            "                - HiFi\n" +
            "                - McWin\n" +
            "                - Mint\n" +
            "                - Noire\n" +
            "                - Smart\n" +
            "                - Luna (similar to Smart)\n" +
            "                - Texture\n" +
            "            - Original (system)\n" +
            "            - Nimbus\n" +
            "            - Motif\n" +
            "            - PGS (PagoSoft)\n" +
            "\n" +
            "Version 0.5 18062020:\n" +
            "    - Cut, copy and paste functions are also available on the menu bar from \"Edit\".\n" +
            "    - Reduced Java Archive size to 2.65MB\n" +
            "    - Bug fixes and patches.\n" +
            "\n" +
            "Version 0.4 14062020:\n" +
            "    - Added support for different characters and languages\n" +
            "        - Chinese, English\n" +
            "        - All unicode characters\n" +
            "    - Improved order of menu items for ease of use\n" +
            "    - Key bindings\n" +
            "        - Ctrl-A -> Select All\n" +
            "        - Alt-Del -> Delete word\n" +
            "        - Ctrl-X -> Cut\n" +
            "        - Ctrl-V -> Paste\n" +
            "        - Ctrl-C -> Copy\n" +
            "        - Ctrl-Q -> Quit\n" +
            "        - Ctrl-Z -> Undo\n" +
            "        - Tab -> Insert Tab\n" +
            "\n" +
            "Version 0.3 14062020:\n" +
            "    - Save and open settings added and fully functional.\n" +
            "    - Resized settings frame to fit \"set as default\" button.\n" +
            "    - Added scroll bar to frame for better navigation.\n" +
            "    - Fixes (3):\n" +
            "        - 00001 -> Exiting frame is laggy and slow, throws NullPointerException\n" +
            "        - 00002 -> File path issue where jar execution throws Exception\n" +
            "        - 00003 -> Default text file not savable in jar\n" +
            "                - Folder in users' default directory will be created named\n" +
            "                  \"SmartEditor\" with default settings file in it.\n" +
            "\n" +
            "Version 0.2 13062020:\n" +
            "    - \"Set as Default\" for font family, size and background, foreground colors.\n" +
            "    - Implemented change listeners to see instant effect of changing font, etc.\n" +
            "    - Implemented color palette for choosing colors from Swatches, RGB, CMYK, et al.\n" +
            "    - Renamed frame to say \"Untitled.txt\".\n" +
            "\n" +
            "Version 0.1 13062020:\n" +
            "    - Frame set-up\n" +
            "    - JMenuBar with settings, save and open settings.\n" +
            "    - Overall structure of frame Complete\n";
    public static String sl = "", s1 = "";
    public static JTabbedPane tb;
    public static DefaultCaret caret = null;

    public static void createFrame() throws Exception, Error{
        caret = (DefaultCaret) ta.getCaret();
        caret.setBlinkRate(3);
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
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
        JMenu smarteditorMenu = new JMenu("SmartEditor");
        JMenu menu = new JMenu("Menu");
        JMenu edit = new JMenu("Edit");
        JMenuItem settings = new JMenuItem("Settings");
        JMenuItem theme = new JMenuItem("Theme");
        JMenuItem newMenu = new JMenuItem("New");
        JMenuItem open = new JMenuItem("Open");
        openRecent = new JMenu("Open Recent");
        updateList(openRecent);
        JMenuItem save = new JMenuItem("Save");
        JMenuItem rename = new JMenuItem("Rename");
        JMenuItem print = new JMenuItem("Print");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem cut = new JMenuItem("Cut");
        JMenuItem copy = new JMenuItem("Copy");
        JMenuItem paste = new JMenuItem("Paste");
        JMenuItem undoMenuItem = new JMenuItem("Undo");
        JMenuItem redoMenuItem = new JMenuItem("Redo");
        undoAction = new UndoAction();
        redoAction = new RedoAction();
        JMenuItem findReplace = new JMenuItem("Find & Replace");
        JMenuItem about = new JMenuItem("About");
        JMenuItem changeLog = new JMenuItem("What's New");
        JMenuItem forceCheck = new JMenuItem("Check for Updates");


        settings.addActionListener(e -> {
            settingFrame.setSize(780, 500);
            JPanel addToSettings = new JPanel();
            addToSettings.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.weightx = 1;
            c.weighty = 1;
            c.fill = GridBagConstraints.BOTH;

            JLabel fontSize = new JLabel("Font Size");
            fontSize.setToolTipText("Font Size: How big text will render on the screen.");
            SpinnerModel value = new SpinnerNumberModel(SmartEditor.showSizeSpin, 10, 40, 1);
            JSpinner spin = new JSpinner(value);
            spin.setToolTipText("Font Size: How big text will render on the screen.");
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
            autoSave.setToolTipText("Auto Save: Save writing as you type.");
            JCheckBox autoSaveT = new JCheckBox();
            autoSaveT.setToolTipText("Auto Save: Save writing as you type.");
            autoSaveT.setSelected(SmartEditor.autoSave);
            autoSaveT.addChangeListener(e12 -> {
                if(autoSaveT.isSelected()){
                    SmartEditor.autoSave = true;
                }else if(!autoSaveT.isSelected()){
                    SmartEditor.autoSave = false;
                }
            });

            JLabel saveOnClose = new JLabel("Save On Close");
            saveOnClose.setToolTipText("Save On Close: Save all writing on exit of SmartEditor.");
            JCheckBox saveOnCloseT = new JCheckBox();
            saveOnCloseT.setToolTipText("Save On Close: Save all writing on exit of SmartEditor.");
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
                stream = SmartEditor.class.getResourceAsStream("Oxygen" + File.separator + "Oxygen-Light.ttf");
                ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, stream));
            }catch(Exception ex){
                ex.printStackTrace();
            }

            String[] array = ge.getAvailableFontFamilyNames();
            JComboBox<String> box = new JComboBox<>(array);
            box.setSelectedItem(SmartEditor.showFontCombo);
            JLabel fontFamily = new JLabel("Font");
            fontFamily.setToolTipText("Font: Choose how your text looks like with different fonts.");
            box.setToolTipText("Font: Choose how your text looks like with different fonts.");
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
            color.setToolTipText("Font Color: Choose the color of your text.");
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
            back.setToolTipText("Background Color: Choose the color of the background of your text.");
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
            setAsDefault.setToolTipText("Set As Default: All settings will be saved after closing this application");
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

        about.addActionListener(e -> {
            JDialog logger = new JDialog();
            logger.setTitle("About SmartEditor");
            logger.setSize(700,550);
            JTextPane ta = new JTextPane();
            ta.setContentType("text/html");
            ta.setText(Frame.htmlText);
            ta.setEditable(false);

            ta.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent event) {
                    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        String url = event.getURL().toString();
                        try {
                            Desktop.getDesktop().browse(URI.create(url));
                        } catch (IOException ignored) {
                        }
                    }
                }
            });

            logger.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    logger.setVisible(false);
                }
            });
            logger.getContentPane().add(new JScrollPane(ta));
            logger.setVisible(true);
        });

        changeLog.addActionListener(e -> {
            String line = "";
            try{
                URL versionURl = new URL("https://raw.githubusercontent.com/JC-ProgJava/SmartEditor.java/master/docs/changelog.txt");
                Scanner input = new Scanner(versionURl.openStream());
                while(input.hasNext()) {
                    line += input.nextLine() + "\n";
                }
            }catch(IOException ignored){

            }finally {
                JTextArea ta = new JTextArea(line.isEmpty() ? Frame.changelogBackUp : line);
                ta.setEditable(false);
                JScrollPane scroll = new JScrollPane(ta);
                JDialog log = new JDialog();
                log.setTitle("Changelog");
                log.setSize(430,300);
                log.add(scroll);
                log.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosed(WindowEvent e) {
                        log.setVisible(false);
                    }
                });
                log.setVisible(true);
            }
        });

        forceCheck.addActionListener(e -> {
            SmartEditor.remindUpdate = true;
            writeFile();
            SmartEditor.checkForUpdates("manual");
        });

        undoMenuItem.addActionListener(e -> {
            undoMenuItem.setEnabled(undoAction.actionPerformed());
        });

        redoMenuItem.addActionListener(e -> {
            redoMenuItem.setEnabled(redoAction.actionPerformed());
        });

        smarteditorMenu.add(about);
        smarteditorMenu.add(changeLog);
        smarteditorMenu.add(forceCheck);
        menu.add(settings);
        menu.add(theme);
        menu.add(newMenu);
        menu.add(open);
        menu.add(openRecent);
        menu.add(save);
        menu.add(rename);
        menu.add(print);
        menu.add(exit);
        edit.add(cut);
        edit.add(copy);
        edit.add(paste);
        edit.add(undoMenuItem);
        edit.add(redoMenuItem);
        edit.add(findReplace);
        mb.add(smarteditorMenu);
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
            File file = new File(lister.getSelectedValue().toString());
            if(!file.getAbsoluteFile().equals(filepath) && file.exists()) {
                ta.setText("");
                SmartEditor.fileDirectoryPath = file.getParent();
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
        ta.setFont(new Font("Sans Serif", Font.PLAIN, SmartEditor.showSizeSpin));
        ta.setFont(new Font(SmartEditor.showFontCombo, Font.PLAIN, SmartEditor.showSizeSpin));
        ta.setLineWrap(true);
        ta.setBackground(SmartEditor.showBackPick);
        ta.setForeground(SmartEditor.showColorPick);

        ta.getDocument().addUndoableEditListener(new UndoHandler());

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

        ta.addKeyListener(new KeyAdapter() {
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
                        }else if(e.getKeyCode() == 90){
                            Frame.undoAction.actionPerformed();
                        }else if(e.getKeyCode() == 82){
                            Frame.redoAction.actionPerformed();
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
            if (percent >= 80 && compareToThis.charAt(0) == word.charAt(0)) {
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

class UndoHandler implements UndoableEditListener {
    public void undoableEditHappened(UndoableEditEvent e) {
        Frame.undo.addEdit(e.getEdit());
    }
}

class UndoAction extends JMenuItem{
    public static boolean actionPerformed() {
        try{
            Frame.undo.undo();
        } catch (CannotUndoException ignored) {
        }
        return Frame.undo.canUndo();
    }
}

class RedoAction extends JMenuItem {
    public static boolean actionPerformed() {
        try {
            Frame.undo.redo();
        } catch (CannotRedoException ignored) {
        }
        return Frame.undo.canRedo();
    }
}
