import java.io.*;
import java.util.*;
import java.text.*;
import java.util.Date;
import java.util.Arrays;

public class Q3Log {

/*

  This is a LogFile Parser for Quake 3.  It produces HTML output files containing stats for players.
  It currently supports the following mods:
    Rocket Arena 3
    Headhunters 3
    Urban Terror
  although it isnt tricky to add support for other mods.
  
  You are free to distribute this file as long as you don't charge anything for it.  If you want to put it on a 
  magazine CD/DVD then e-mail me at givememoney@wilf.co.uk and we can talk.
  You are also free to modify this file as much as you want, but if you distribute a modified copy please include
  this at the top of the file and be aware it remains the property of me, so no selling modified version please.

  Feel free to e-mail me with comments/suggestions/whatever at q3logger@wilf.co.uk
  
  Copyright Stuart Butcher (stu@wilf.co.uk) 2002
    
*/

// The next few lines are creating the tool objects needed by Q3Log
private LogTools myTools = new LogTools();
private LogParser myParser;
private LogTotals myTotals;

// The program version
public static final String PROGVER = new String("2.3"); 
// The programs name
public static final String PROGNAME = new String("Q3Log"); 
// The programs description
public static final String PROGDESC = new String("Quake 3 Arena Stats Generator"); 
// Full program info
public static final String PROGFULL = new String(PROGNAME + " v" + PROGVER + " - " + PROGDESC); 
// Program homepage
public static final String PROGURL = new String("http://www.wilf.co.uk/"); 
public static final String WEAPONLINK = new String(LogTools.WEAPONLINK);
// The following arrays are defined for mod types, and contain the weapons used in that mod
public static final String[] TXTQ3WEAPONS = {
  "Unknown - 0",
  "Shotgun",
  "Gauntlet",
  "Machine Gun",
  "Grenade Launcher",
  "Grenade Launcher (Explosion)",
  "Rocket Launcher",
  "Rocket Launcher (Explosion)",
  "Plasma Gun",
  "Plasma Gun (Heat)",
  "Railgun",
  "Lightning Gun",
  "BFG",
  "BFG (Heat)",
  "Unknown - 14",
  "Unknown - 15",
  "Lava",
  "Crushing",
  "Telefrag",
  "Falling",
  "Unknown - 20",
  "Unknown - 21",
  "Trigger Death",
  "Unknown - 23",
  "Unknown - 24",
  "Unknown - 25",
  "Unknown - 26",
  "Unknown - 27",
  "Unknown - 28",
  "Unknown - 29",
  "Unknown - 30",
};
public static final String[] TXTQ3WEAPONSLINK = {
  "Unknown - 0",
  "Shotgun",
  "Gauntlet",
  "Machine Gun",
  "Grenade Launcher",
  WEAPONLINK + "4",
  "Rocket Launcher",
  WEAPONLINK + "6",
  "Plasma Gun",
  WEAPONLINK + "8",
  "Railgun",
  "Lightning Gun",
  "BFG",
  WEAPONLINK + "12",
  "Unknown - 14",
  "Unknown - 15",
  "Suicide",
  WEAPONLINK + "16",
  "Telefrag",
  WEAPONLINK + "16",
  "Unknown - 20",
  "Unknown - 21",
  WEAPONLINK + "16",
  "Unknown - 23",
  "Unknown - 24",
  "Unknown - 25",
  "Unknown - 26",
  "Unknown - 27",
  "Unknown - 28",
  "Unknown - 29",
  "Unknown - 30",
};
public static final String[] TXTHH3WEAPONS = {
  "Unknown - 0",
  "Shotgun",
  "Gauntlet",
  "Machine Gun",
  "Grenade Launcher",
  "Grenade Launcher (Explosion)",
  "Rocket Launcher",
  "Rocket Launcher (Explosion)",
  "Razor Gun",
  "Razor Gun (Head shot)",
  "Railgun",
  "Lightning Gun",
  "BFG",
  "BFG (Heat)",
  "Unknown - 14",
  "Unknown - 15",
  "Lava",
  "Crushing",
  "Telefrag",
  "Falling",
  "Unknown - 20",
  "Unknown - 21",
  "Trigger Death",
  "Unknown - 23",
  "Unknown - 24",
  "Unknown - 25",
  "Unknown - 26",
  "Unknown - 27",
  "Unknown - 28",
  "Unknown - 29",
  "Unknown - 30",
}; 
public static final String[] TXTHH3WEAPONSLINK = {
  "Unknown - 0",
  "Shotgun",
  "Gauntlet",
  "Machine Gun",
  "Grenade Launcher",
  WEAPONLINK + "4",
  "Rocket Launcher",
  WEAPONLINK + "6",
  "Razor Gun",
  WEAPONLINK + "8",
  "Railgun",
  "Lightning Gun",
  "BFG",
  WEAPONLINK + "12",
  "Unknown - 14",
  "Unknown - 15",
  "Suicide",
  WEAPONLINK + "16",
  "Telefrag",
  WEAPONLINK + "16",
  "Unknown - 20",
  "Unknown - 21",
  WEAPONLINK + "16",
  "Unknown - 23",
  "Unknown - 24",
  "Unknown - 25",
  "Unknown - 26",
  "Unknown - 27",
  "Unknown - 28",
  "Unknown - 29",
  "Unknown - 30",
}; 
public static final String[] TXTUT3WEAPONS = {
  "Unknown - 0",
  "Drowned",
  "Unknown - 2",
  "Unknown - 3",
  "Unknown - 4",
  "Unknown - 5",
  "Falling",
  "Unknown - 7",
  "Unknown - 8",
  "Trigger Hurt",
  "Change Team",
  "Unknown - 11",
  "Knife",
  "Knife (Thrown)",
  "Beretta",
  "Dessert Eagle",
  "SPAS",
  "UMP45",
  "MP5K",
  "M4",
  "G36",
  "PSG1",
  "HK69",
  "Bleeding",
  "Unknown - 24",
  "Grenade (High Energy)",
  "Unknown - 26",
  "Unknown - 27",
  "SR8",
  "Unknown - 29",
  "Unknown - 30",
};
public static final String[] TXTUT3WEAPONSLINK = {
  "Unknown - 0",
  "Drowned",
  "Unknown - 2",
  "Unknown - 3",
  "Unknown - 4",
  "Unknown - 5",
  "Suicide",
  "Unknown - 7",
  "Unknown - 8",
  WEAPONLINK + "6",
  "Change Team",
  "Unknown - 11",
  "Knife",
  WEAPONLINK,
  "Beretta",
  "Dessert Eagle",
  "SPAS",
  "UMP45",
  "MP5K",
  "M4",
  "G36",
  "PSG1",
  "HK69",
  "Bleeding",
  "Unknown - 24",
  WEAPONLINK,
  "Unknown - 26",
  "Unknown - 27",
  "SR8",
  "Unknown - 29",
  "Unknown - 30",
};
public static final int INDENT = 7; // Indent in Logfile (usually 7)

// Command line argument strings
public static final String ARGHELP = new String("HELP"); // Help argument
public static final String ARGPARSE = new String("PARSE"); // Parse argument
public static final String ARGOUTPUT = new String("OUTPUT"); // Output argument
public static final String ARGBOTH = new String("BOTH"); // Both argument

// The game types allowed
public static final String TYPESTANDARD = new String("STANDARD"); // Standard Type
public static final String TYPEARENA = new String("ARENA"); // Arena Type
public static final String TYPEHH3 = new String("HH3"); // Headhunters Type
public static final String TYPEUT3 = new String("UT3"); // Urban Terror Type

// Proper text for the game types
public static final String TEXTSTANDARD = new String("Standard"); // Standard Text
public static final String TEXTARENA = new String("Rocket Arena"); // Arena Text
public static final String TEXTHH3 = new String("Headhunters"); // Headhunters Text
public static final String TEXTUT3 = new String("Urban Terror"); // Urban Terror Text

// Static strings for the tags
public static final String OPENTAG = new String("<Q3LOG "); // Start of template tag
public static final String FAKEOPENTAG = new String("<Q3L0G "); // Fake start of template tag
public static final String CLOSETAG = new String(">"); // End of template tag
public static final String LINENAME = "NAME"; // Label for the name of the tag
public static final String LINETYPE = "TYPE"; // Label for the type of the tag
public static final String LINEVALUE = "VALUE"; // Label for the type of the tag

// Tag types
public static final String TAGVALUE = "VALUE"; // Type of Tag that is replace by a value
public static final String TAGSECTION = "SECTION"; // Type of Tag that is replaced by another file
public static final String TAGSYSTEM = "SYSTEM"; // Type of Tag that is replaced by a system value
public static final String TAGPASS = "PASS"; // Type of Tag that is replaced by a value that can include the passed value
public static final String TAGSORT = "TITLE"; // Type of Tag that is replaced by a title read from the config file

// The following are tag replacements values for internal values, used in the template files
public static final String TAGDEATHS = new String("DEATHS");
public static final String TAGEFFICIENCY = new String("EFFICIENCY");
public static final String TAGFRAGS = new String("FRAGS");
public static final String TAGUSERS = new String("USERS");
public static final String TAGKILLRATIO = new String("KILLRATIO");
public static final String TAGKILLS = new String("KILLS");
public static final String TAGMEMBER = new String("MEMBER");
public static final String TAGOPONENT = new String("OPONENT");
public static final String TAGSUICIDES = new String("SUICIDES");
public static final String TAGRANK = new String("RANK");
public static final String TAGABOVE = new String("ABOVE");
public static final String TAGBELOW = new String("BELOW");
public static final String TAGTITLE = new String("TITLE");
public static final String TAGTOTALS = new String("TOTALS");
public static final String TAGUSER = new String("USER");
public static final String TAGUSERLINK = new String("USER_LINK");
public static final String TAGWEAPON = new String("WEAPON");
public static final String TAGWEAPONS = new String("WEAPONS");
public static final String TAGURLUP = new String("UP");
public static final String TAGURLDOWN = new String("DOWN");
public static final String TAGLINKUP = new String("LINKUP");
public static final String TAGCLOSE = new String("CLOSE");
public static final String TAGPERCENT = new String("PERCENT");

// System tag properties
private static final String SYSDATE = "DATE"; // Value asking for Date
private static final String SYSTIME = "TIME"; // Value asking for Time
private static final String SYSPROGNAME = "PROGNAME"; // Value asking for our name
private static final String SYSPROGVER = "PROGVER"; // Value asking for our version
private static final String SYSPROGDESC = "PROGDESC"; // Value asking for our description
private static final String SYSPROGFULL = "PROGFULL"; // Value asking for all the above 3 put together
private static final String SYSPROGURL = "PROGURL"; // Value asking for our homepage
private static final String SYSCUTOFF = "CUTOFF"; // Value asking for our Cutoff point
private static final String SYSWEAPONS = "TOPWEAPONS"; // Value asking for our number of top weapons shown

private static final String REPVALUE = "Q3LOGREP"; // Used when replacing a Pass tag in the templates

// Template file static filenames
public static final String STATICIM = new String("indiv_main.htm_"); // File name for Main Individual page
public static final String STATICIOBELOW = new String("indiv_oponents_below.htm_"); // File name for Oponents Individual page (below average)
public static final String STATICIOABOVE = new String("indiv_oponents_above.htm_"); // File name for Oponents Individual page (above average)
public static final String STATICIW = new String("indiv_weapons.htm_"); // File name for Weapons Individual page
public static final String STATICIOT = new String("indiv_oponents_total.htm_"); // File name for Oponents Totals Individual page
public static final String STATICIWT = new String("indiv_weapons_total.htm_"); // File name for Weapons Totals Individual page
public static final String STATICTABLE = new String("table_main.htm_"); // File name for Main page
public static final String STATICTABLEWEAPONS = new String("table_weapons.htm_"); // File name for Weapons summary on main page
public static final String STATICROWABOVE = new String("table_row_above.htm_"); // File name for Main Table Row page (above average)
public static final String STATICROWBELOW = new String("table_row_below.htm_"); // File name for Main Table Row page (below average)
public static final String STATICTOTAL = new String("table_row_total.htm_"); // File name for Table Total Row page

// Property file static strings
public static final String PROPSTATIC = new String("Templates"); // Static property name (for conf file)
public static final String PROPTARGET = new String("Target"); // Target property name (for conf file)
public static final String PROPCUTOFF = new String("Cutoff"); // Cutoff property name (for conf file)
public static final String PROPTOPWEAPONS = new String("TopWeapons"); // How many of the top weapons to list (for the conf file)
public static final String PROPSORT = new String("Sort"); // Default field to sort on (for conf file)
public static final String PROPDIRECT = new String("Direct"); // Default direction to sort (for conf file)
public static final String PROPALLSORT = new String("AllSort"); // What to sort  (for conf file)
public static final String PROPSORTUP = new String("SortUp"); // Img to output for sorting up (for conf file)
public static final String PROPSORTDOWN = new String("SortDown"); // Img to output for sorting down (for conf file)
public static final String PROPTITLE0 = new String("Title0"); // The text for Kills (for conf file)
public static final String PROPTITLE1 = new String("Title1"); // The text for Deaths (for conf file)
public static final String PROPTITLE2 = new String("Title2"); // The text for Efficiency (for conf file)
public static final String PROPTITLE3 = new String("Title3"); // The text for Suicides (for conf file)
public static final String PROPTITLE4 = new String("Title4"); // The text for Rank (for conf file)
public static final String PROPTITLE5 = new String("Title5"); // The text for Ratio (for conf file)
public static final String PROPTITLEBEFORE = new String("SortTitleBefore"); // The text that goes before the sorted title (for conf file)
public static final String PROPTITLEAFTER = new String("SortTitleAfter"); // The text that goes after the sorted title (for conf file)
public static final String PROPIGNORE = new String("Ignore"); // List of users to ignore (for conf file)
public static final String PROPIGNORESPLIT = new String("Split"); // The string used to split up the userlist for Ignore (for conf file)
public static final String PROPLINK = new String("LinkWeapons"); // Wether to use the linked weapons lists (for conf file)
public static final String PROPCOLOURS = new String("Colours"); // Output user colours (for conf file)
public static final String PROPBLACK = new String("Black"); // Colour for fun names (for conf file)
public static final String PROPRED = new String("Red"); // Colour for fun names (for conf file)
public static final String PROPGREEN = new String("Green"); // Colour for fun names (for conf file)
public static final String PROPYELLOW = new String("Yellow"); // Colour for fun names (for conf file)
public static final String PROPBLUE = new String("Blue"); // Colour for fun names (for conf file)
public static final String PROPCYAN = new String("Cyan"); // Colour for fun names (for conf file)
public static final String PROPMAGENTA = new String("Magenta"); // Colour for fun names (for conf file)
public static final String PROPWHITE = new String("White"); // Colour for fun names (for conf file)

private static final String PATHSEP = new String("@"); // What seperates the paths in the command line arguments
private static final String LINESEP = LogTools.LINESEP; // The line seperator for this system
private static final String TXTSUICIDE = LogParser.TXTSUICIDE; // Used to tell when a kill is actually a suicide (also if Killer = Killed)

// The next set of Strings hold the text to go before, during and after the up and down images and links
private static String IMGBEFORE = new String("<IMG BORDER=0 SRC=\"");
private static String IMGAFTER = new String("\">");
private static String HREFBEFORE = new String("<A HREF=\"");
private static String HREFMIDDLE = new String("\">");
private static String HREFAFTER = new String("</A>");

// The following vectors hold the template file lines
private Vector vIndivMain = new Vector();
private Vector vIndivOponBelow = new Vector();
private Vector vIndivOponAbove = new Vector();
private Vector vIndivWeap = new Vector();
private Vector vIndivTOpo = new Vector();
private Vector vIndivTWea = new Vector();
private Vector vTableMain = new Vector();
private Vector vTableWeapons = new Vector();
private Vector vTableRowsAbove = new Vector();
private Vector vTableRowsBelow = new Vector();
private Vector vTableTRow = new Vector();

private Hashtable htUsers = null; // Holds a list of users and their details
private Hashtable htKills = new Hashtable(); // Holds all the kills in the logfile
private Properties prProp = new Properties(); // Properties read from the config file

private String sTarget = new String(); // The target for all user page links
private String sStatic = new String(); // The dir containing the template files
private String sTotals = new String(); // The string containing the totals line
private String sOutDir = new String(); // The output directory
private String sServer = new String(); // The server name
private String sAdd = new String(); // Additional link info
private String sTitle = new String(); // The title of the mainpage
private String sSortUp = new String(); // The Img representing sort up
private String sSortDown = new String(); // The Img representing sort down
private String sTitleBefore = new String(); // The text to go before the sorted title
private String sTitleAfter = new String(); // The text to go after the sorted title
private String sIgnore = new String(); // The ignore list of usernames
private String sIgnoreSplit = new String(); // The split character for the ignore list
private String[] sTitles = new String[6]; // The titles
private String[] aColours = new String[8]; // The colours for fun names
private String sType = new String(); // The type of this run
private String sWeaponsList[]; // The weapons list

// The following Vectors hold the lines to be replaced in the template files
private Vector vWeapons = new Vector();
private Vector vTOponent = new Vector();
private Vector vRowsAbove = new Vector();
private Vector vRowsBelow = new Vector();
private Vector vMainWeapons = new Vector();

// The following Hashtables hold values to use to replace things in the template files
private Hashtable htMainUser = new Hashtable();
private Hashtable htHold = new Hashtable();
private Hashtable htHoldInd = new Hashtable();
private Hashtable htTags = new Hashtable();
private Hashtable htHoldTotals = new Hashtable();
private Hashtable htSort = new Hashtable();
private Hashtable htSortInd = new Hashtable();
private Hashtable htFun = new Hashtable();

// The following int's hold value from the conf file
private int iCutOff = 0;
private int iSort = 0;
private int iAllSort = 0;
private int iTopWeapons = 0;

// The direction to sort in (from the conf file)
private boolean bDirect = false;
// Output user colours (from the conf file)
private boolean bColours = true;
// Use the linked weapons lists (from the conf file)
private boolean bLinked = false;

  //  Constructer for the class
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public Q3Log() {
    // We dont need to do anything here, but might want to in the future...
  } 
  
  
  //  Returns the type for the game type passed in (default is Standard)
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private String getType(String sParam) {
  String sType = new String(TEXTSTANDARD);
    if (sParam.equalsIgnoreCase(TYPEARENA)) { sType = TEXTARENA; } 
    else if (sParam.equalsIgnoreCase(TYPEHH3)) { sType = TEXTHH3; } 
    else if (sParam.equalsIgnoreCase(TYPEUT3)) { sType = TEXTUT3; }
    return sType;
  }
  
  //  Returns the weapon list for the game type passed in (default is Standard)
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void getWeaponsList(String sParam) {
    sWeaponsList = TXTQ3WEAPONS;
    if (bLinked) { sWeaponsList = TXTQ3WEAPONSLINK; }
    if (sParam.equalsIgnoreCase(TYPEARENA)) { 
      if (bLinked) { sWeaponsList = TXTQ3WEAPONSLINK; }
      else { sWeaponsList = TXTQ3WEAPONS; }
    } else if (sParam.equalsIgnoreCase(TYPEHH3)) { 
      if (bLinked) { sWeaponsList = TXTHH3WEAPONSLINK; }
      else { sWeaponsList = TXTHH3WEAPONS; }
    } else if (sParam.equalsIgnoreCase(TYPEUT3)) { 
      if (bLinked) { sWeaponsList = TXTUT3WEAPONSLINK; }
      else { sWeaponsList = TXTUT3WEAPONS; }
    }
  }

  //  Returns true if the passed in type is valid or else returns false
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private boolean checkType(String sType) {
  boolean bReturn = false;
    if (sType.equalsIgnoreCase(TYPESTANDARD)) { bReturn = true; }
    else if (sType.equalsIgnoreCase(TYPEARENA)) { bReturn = true; }
    else if (sType.equalsIgnoreCase(TYPEHH3)) { bReturn = true; }
    else if (sType.equalsIgnoreCase(TYPEUT3)) { bReturn = true; }
    return bReturn;
  }

  //  Parses the passed in log file and deletes it once it has finished if requested
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private boolean addLog(String sIn,String sOut,boolean bRead,boolean bDelete) throws FileNotFoundException,IOException {
  File fInput = new File(sIn);
  boolean bReturn = false;
    myParser.execute(sOut,fInput.getPath(),bRead);
    if (bDelete) { bReturn = fInput.delete(); }
    else { bReturn = true; }
    return bReturn;
  }
  
  //  Reads the config file into a properties object
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private boolean readProperties(String sProp) throws FileNotFoundException,IOException,IllegalArgumentException {
  File fProp = new File(sProp);
  boolean bReturn = false;
    if ((fProp.exists()) && (fProp.isFile())) {
      prProp.load(new FileInputStream(fProp));
      bReturn = true;
      if (myTools.checkString((String)prProp.get(PROPSTATIC))) { sStatic = (String)prProp.get(PROPSTATIC); }
      else { throw new IllegalArgumentException(PROPSTATIC + " needs to be set in the config file"); }
      sTarget = prProp.getProperty(PROPTARGET,"_blank");
      iCutOff = new Integer(prProp.getProperty(PROPCUTOFF,"0")).intValue();
      iTopWeapons = new Integer(prProp.getProperty(PROPTOPWEAPONS,"0")).intValue();
      iSort = new Integer(prProp.getProperty(PROPSORT,"2")).intValue();
      bDirect = new Boolean(prProp.getProperty(PROPDIRECT,"true")).booleanValue();
      iAllSort = new Integer(prProp.getProperty(PROPALLSORT,"0")).intValue();
      if (myTools.checkString((String)prProp.get(PROPSORTUP))) { sSortUp = IMGBEFORE + (String)prProp.get(PROPSORTUP) + IMGAFTER; }
      else { sSortUp = new String(); }
      if (myTools.checkString((String)prProp.get(PROPSORTDOWN))) { sSortDown = IMGBEFORE + (String)prProp.get(PROPSORTDOWN) + IMGAFTER; }
      else { sSortDown = new String(); }
      sTitles[0] = prProp.getProperty(PROPTITLE0,new String());
      sTitles[1] = prProp.getProperty(PROPTITLE1,new String());
      sTitles[2] = prProp.getProperty(PROPTITLE2,new String());
      sTitles[3] = prProp.getProperty(PROPTITLE3,new String());
      sTitles[4] = prProp.getProperty(PROPTITLE4,new String());
      sTitles[5] = prProp.getProperty(PROPTITLE5,new String());
      sTitleBefore = prProp.getProperty(PROPTITLEBEFORE,new String());
      sTitleAfter = prProp.getProperty(PROPTITLEAFTER,new String());
      sIgnore = prProp.getProperty(PROPIGNORE,new String());
      sIgnoreSplit = prProp.getProperty(PROPIGNORESPLIT,";");
      bLinked = new Boolean(prProp.getProperty(PROPLINK,"false")).booleanValue();
      bColours = new Boolean(prProp.getProperty(PROPCOLOURS,"true")).booleanValue();
      aColours[0] = prProp.getProperty(PROPBLACK,"Black");
      aColours[1] = prProp.getProperty(PROPRED,"Red");
      aColours[2] = prProp.getProperty(PROPGREEN,"Green");
      aColours[3] = prProp.getProperty(PROPYELLOW,"Yellow");
      aColours[4] = prProp.getProperty(PROPBLUE,"Blue");
      aColours[5] = prProp.getProperty(PROPCYAN,"Cyan");
      aColours[6] = prProp.getProperty(PROPMAGENTA,"Magenta");
      aColours[7] = prProp.getProperty(PROPWHITE,"White");
    }
    return bReturn; 
  }

  //  Reads in a log file, clearing out the kills hashtable first if requested
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private boolean getLog(String sFile,boolean bClear) throws FileNotFoundException,IOException {
  boolean bReturn = false;
  String[] aIgnore;
    if (myTools.checkString(sIgnore)) {
      aIgnore = myTools.split(sIgnore,sIgnoreSplit);
      myParser.setIgnore(aIgnore);
    }
    htKills = myParser.readLog(sFile,bClear);
    htUsers = myParser.getUsers();
    htFun = myParser.getFun();
    bReturn = true;
    return bReturn;
  }

  //  Reads the static template files into their vectors
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  private void readStatic() throws IllegalArgumentException,FileNotFoundException,IOException {
  File fIn;
    sStatic = myTools.checkDir(sStatic);
    fIn = new File(sStatic + STATICIM);
    vIndivMain = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICIOBELOW);
    vIndivOponBelow = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICIOABOVE);
    vIndivOponAbove = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICIW);
    vIndivWeap = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICIWT);
    vIndivTWea = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICIOT);
    vIndivTOpo = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICTABLE);
    vTableMain = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICTABLEWEAPONS);
    vTableWeapons = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICROWABOVE);
    vTableRowsAbove = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICROWBELOW);
    vTableRowsBelow = myTools.readFile(fIn);
    fIn = new File(sStatic + STATICTOTAL);
    vTableTRow = myTools.readFile(fIn);
  }

  //  Returns the parameter value from the current tag
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  private String getParam(String sTag,String sParam,boolean bUpperCase) { 
  int iSta = 0;
  String sReturn = new String();
    if (sTag.indexOf(sParam) > -1) {
      iSta = sTag.indexOf('"',sTag.indexOf(sParam)) + 1;
      sReturn = sTag.substring(iSta,sTag.indexOf('"',iSta)); 
      if (bUpperCase) { sReturn = sReturn.toUpperCase(); }
    }
    return sReturn;
  }
  private String getParam(String sTag,String sParam) { return getParam(sTag,sParam,true); }

  //  Returns any system value asked for that has been setup
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  private String getSysVal(String sName) {
  String sReturn = new String();
  DateFormat defaultDate;
    if (sName.equals(SYSTIME)) {
      defaultDate = DateFormat.getTimeInstance(DateFormat.SHORT);
      sReturn = defaultDate.format(new Date());
    } else if (sName.equals(SYSDATE)) {
      defaultDate = DateFormat.getDateInstance();
      sReturn = defaultDate.format(new Date());
    } else if (sName.equals(SYSPROGNAME)) {
      sReturn = PROGNAME;
    } else if (sName.equals(SYSPROGVER)) {
      sReturn = PROGVER;
    } else if (sName.equals(SYSPROGDESC)) {
      sReturn = PROGDESC;
    } else if (sName.equals(SYSPROGFULL)) {
      sReturn = PROGFULL;
    } else if (sName.equals(SYSPROGURL)) {
      sReturn = PROGURL;
    } else if (sName.equals(SYSCUTOFF)) {
      sReturn = new Integer(iCutOff).toString();
    } else if (sName.equals(SYSWEAPONS)) {
      sReturn = new Integer(iTopWeapons).toString();
    }
    return sReturn;
  }

  //  Swaps out the tags for their values in the passed in vector, using values from the passed in hashtable
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  private String swapTags(Vector vInput,Hashtable htTags,int iBase) {
  String sLine = new String();
  Vector vReturn = new Vector();
  String sTag = new String();
  String sName = new String();
  String sType = new String();
  String sValue = new String();
  Vector vHold = new Vector();
  int iTitle = 0;
  int iOffset = 0;int iSta = 0;int iEnd = 0;int iLen = 0;int iBeg = 0;int iFin = 0;int iNum = 0; // Position holders
    for (Iterator itLocal = vInput.iterator(); itLocal.hasNext();) {
      sLine = (String)itLocal.next();
      while (sLine.indexOf(OPENTAG) > -1) {
        iSta = sLine.indexOf(OPENTAG);
        iEnd = sLine.indexOf(CLOSETAG,iSta);
        sTag = sLine.substring(iSta,iEnd + 1);
        sName = getParam(sTag,LINENAME);
        sType = getParam(sTag,LINETYPE);
        if ((sType.equals(TAGVALUE)) || (sType.equals(TAGSECTION))) {
          if (htTags.get(sName) != null) { sValue = (String)htTags.get(sName); }
          else { sValue = new String(); }
        } else if (sType.equals(TAGPASS)) {
          if (htTags.get(sName) != null) { sValue = (String)htTags.get(sName); }
          else { sValue = new String(); }
          sValue = myTools.replaceAll(sValue,REPVALUE,getParam(sTag,LINEVALUE,false));
        } else if (sType.equals(TAGSORT)) {
          iTitle = new Integer(sName).intValue();
          if ((iTitle > -1) && (iTitle < sTitles.length)) {
            if (iTitle == iBase) {
              sValue = sTitleBefore + sTitles[iTitle] + sTitleAfter;
            } else {
              sValue = sTitles[iTitle];
            }
          } else {
            sValue = new String();
          }
        } else if (sType.equals(TAGSYSTEM)) {
          sValue = getSysVal(sName);
        } else {
          sValue = myTools.replaceAll(sTag,OPENTAG,FAKEOPENTAG);
        }
        sLine = myTools.replaceAll(sLine,sTag,sValue);
      }
      sLine = myTools.replaceAll(sLine,FAKEOPENTAG,OPENTAG);
      vReturn.add(sLine);
    }
    return myTools.getString(vReturn);
  }
  private String swapTags(Vector vInput,Hashtable htTags) { return swapTags(vInput,htTags,sTitles.length); }

  //  Builds up the main user details from the many hashtables
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void buildUsers() throws IOException,IllegalArgumentException  {
  int iKills = 0;
  int iKill = 0;
  int iDeaths = 0;
  int iWeapon = 0;
  int iPercent = 0;
  Float[] fSortInd = new Float[3];
  Hashtable htPeople = new Hashtable();
  Hashtable htHold = new Hashtable();
  Integer iOponent = new Integer(0);
  Integer iUser = new Integer(0);
  String sWeapon = new String();
  String sOponent = new String();
  String sUser = new String();
  String sFun = new String();
  LogTotals myWeapTotals;
  LogTotals myIndTotals;
  
    myTotals = myParser.getGrandTotal();
    
    for (Enumeration eKills = htKills.keys();eKills.hasMoreElements();) {
      htHoldInd = new Hashtable();
      iUser = (Integer)eKills.nextElement();
      sUser = (String)htUsers.get(iUser);
      htHold = (Hashtable)htKills.get(iUser);
      
      myIndTotals = (LogTotals)htHold.get(LogParser.TXTTOTALS);
      htPeople = (Hashtable)htHold.get(LogParser.TXTOPONENTS);

      for (Enumeration ePeople = htPeople.keys();ePeople.hasMoreElements();) {
        
        iOponent = (Integer)ePeople.nextElement();
        sOponent = (String)htUsers.get(iOponent);
        myWeapTotals = (LogTotals)htPeople.get(iOponent);

        vWeapons = new Vector();
        iKills = myWeapTotals.getFrags();
        vWeapons = getWeapons(myWeapTotals,myWeapTotals.getFrags(),100,vIndivWeap);
        iDeaths = myWeapTotals.getActualDeaths();
        iPercent = LogTotals.workEfficiency(iKills,iDeaths,0);

        htTags = new Hashtable();
        sFun = sOponent;
        if ((bColours) && (myTools.checkString((String)htFun.get(sUser)))) { 
          sFun = myTools.stripChars((String)htFun.get(sOponent),aColours); 
        }

        htTags.put(TAGOPONENT,sFun);
        htTags.put(TAGKILLS,new Integer(iKills).toString());
        htTags.put(TAGDEATHS,new Integer(iDeaths).toString());
        htTags.put(TAGEFFICIENCY,new Integer(iPercent).toString());
        htTags.put(TAGWEAPONS,myTools.getString(vWeapons));

        htHoldInd.put(sOponent,htTags);
        fSortInd = new Float[3];
        fSortInd[LogTotals.KILLS] = new Float(iKills);
        fSortInd[LogTotals.DEATHS] = new Float(iDeaths);
        fSortInd[LogTotals.EFFICIENCY] = new Float(iPercent);
        htSortInd.put(sOponent,fSortInd);
      }

      htMainUser.put(sUser,myIndTotals);

      vWeapons = getWeapons(myIndTotals,myIndTotals.getFrags(),100,vIndivTWea);
      
      htTags = new Hashtable();
      htTags.put(TAGKILLS,new Integer(myIndTotals.getKills()).toString());
      htTags.put(TAGDEATHS,new Integer(myIndTotals.getDeaths()).toString());
      htTags.put(TAGEFFICIENCY,new Integer(myIndTotals.workEfficiency(myIndTotals.getActualKills(),myIndTotals.getActualDeaths(),0)).toString());
      htTags.put(TAGWEAPONS,myTools.getString(vWeapons));
      
      vTOponent = new Vector();
      vTOponent.add(swapTags(vIndivTOpo,htTags));
      htHoldTotals.put(sUser,myTools.getString(vTOponent));
      sortIndiv(sUser,myIndTotals);
      htSortInd = new Hashtable();
    }
  }

  //  Builds the main user table and writes it out
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void buildTable() throws IOException,IllegalArgumentException  {
  Float[] fHold = new Float[6];
  LogTotals ltUser = new LogTotals(1);
  Float[] fSort = new Float[6];
  int iKills = 0;
  int iDeaths = 0;
  int iSuicides = 0;
  String sLink = new String();
  String sUser = new String();
  String sMiddle = sAdd + "/" + sServer;
  String sAddOn = new String("_2D");
  String sFun = new String();
    for (Enumeration eUsers = htMainUser.keys();eUsers.hasMoreElements();) {
      sUser = (String)eUsers.nextElement();
      ltUser = (LogTotals)htMainUser.get(sUser);
      
      if (iAllSort < 2) { sAddOn = new String(); }
      sLink = "<A HREF=\"" + sMiddle + "_" + sUser + sAddOn + ".html\" TARGET=\"" + sTarget + "\">";
      
      iKills = ltUser.getActualKills();
      iDeaths = ltUser.getActualDeaths();
      iSuicides = ltUser.getActualSuicides();
      
      htTags = new Hashtable();
      htTags.put(TAGUSERLINK,sLink);
      sFun = sUser;
      if (bColours) { sFun = myTools.stripChars((String)htFun.get(sUser),aColours); }
      htTags.put(TAGUSER,sFun);
      htTags.put(TAGKILLS,new Integer(iKills).toString());
      htTags.put(TAGDEATHS,new Integer(iDeaths).toString());
      htTags.put(TAGSUICIDES,new Integer(iSuicides).toString());
      htTags.put(TAGKILLRATIO,ltUser.getKillRatio());
      htTags.put(TAGRANK,ltUser.getRank());
      htTags.put(TAGEFFICIENCY,ltUser.getEfficiency());
      
      htHold.put(sUser,htTags);
      
      if (iKills + iDeaths + iSuicides >= iCutOff) {
        
        myTotals.addKill(iKills);
        myTotals.addDeath(iDeaths);
        myTotals.addSuicide(iSuicides);
        myTotals.addUser(sUser,true);
        
        fSort = new Float[6];
        fSort[LogTotals.KILLS] = new Float(iKills);
        fSort[LogTotals.DEATHS] = new Float(iDeaths);
        fSort[LogTotals.SUICIDES] = new Float(iSuicides);
        fSort[LogTotals.RANK] = new Float(ltUser.getNum(LogTotals.RANK));
        fSort[LogTotals.EFFICIENCY] = new Float(ltUser.getNum(LogTotals.EFFICIENCY));
        fSort[LogTotals.RATIO] = new Float(ltUser.getNum(LogTotals.RATIO));
        htSort.put(sUser,fSort);

      } else {
        myTotals.addUser(sUser,false);
      }
    }
  }

  //  Sorts the users and writes out the users individual files
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void sortUsers() throws IllegalArgumentException,IOException  {
  Vector vSorted;
  String sDir;
  String sPass = new String();
  String sFile = new String();
  Vector vHold[] = new Vector[2];
  boolean bForwards;
  int iStart;int iEnd;
  int iBegin;int iFinish;
    if (iAllSort > 0) { iStart = 0; iEnd = 2; iBegin = 0; iFinish = 6; bForwards = true; sDir = "D"; } 
    else { iStart = 0; iEnd = 1; iBegin = iSort; iFinish = iSort + 1; bForwards = bDirect; sDir = ""; }
    vMainWeapons = getWeapons(myTotals,myTotals.getFrags(),iTopWeapons,vTableWeapons);
    for (int iLoop = iStart;iLoop < iEnd;iLoop++) {
      for (int iBase = iBegin;iBase < iFinish;iBase++) {
        vSorted = myTools.sortHash(htSort,iBase,bForwards);
        vHold = addCycle(vSorted,htSort,htHold,myTotals,vTableRowsAbove,vTableRowsBelow,iBase);
        if (!bForwards) { vRowsAbove = vHold[1]; vRowsBelow = vHold[0]; } 
        else { vRowsAbove = vHold[0]; vRowsBelow = vHold[1]; }
        if (!sDir.equals("")) { sPass = "_" + iBase + sDir; }
        sFile = sServer + "_" + sAdd;
        writeMain(sFile,sPass,iBase);
        vSorted = new Vector();
      }
      bForwards = !bForwards;
      sDir = "U";
    }
  }

  //  Gets the weapons in order and puts them into the returned vector
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   27th September 2002
  //
  //
  private Vector getWeapons(LogTotals myTotals,int iFrags,int iMaximum,Vector vIn) {
  Vector vReturn = new Vector();
  Hashtable htHold = myTotals.getWeapons();
  String[] aWeapons = (String[])htHold.get(LogTotals.WEAPONS);
  int[] aFrags = (int[])htHold.get(LogTotals.FRAGS);
  float fFrags = new Float(iFrags).floatValue();
  String sPercent;
  int iTop;
    if (aWeapons.length > iMaximum) { iTop = iMaximum; }
    else { iTop = aWeapons.length; }
    for (int iLoop = 0;iLoop < iTop;iLoop++) {
      sPercent = myTools.decimalPlaces(new Float((100 / fFrags) * aFrags[iLoop]).toString());
      htHold = new Hashtable();
      htHold.put(TAGMEMBER,new Integer(iLoop + 1).toString());
      htHold.put(TAGWEAPON,aWeapons[iLoop]);
      htHold.put(TAGKILLS,new Integer(aFrags[iLoop]).toString());
      htHold.put(TAGPERCENT,sPercent);
      vReturn.add(swapTags(vIn,htHold));
    }
    return vReturn;
  }

  //  Sorts the users individual stats
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void sortIndiv(String sMainUser,LogTotals myIndTotals) throws IOException,IllegalArgumentException  {
  Vector vSorted;
  String sDir;
  String sPass = new String();
  String sAbove = new String();
  String sBelow = new String();
  Vector vHold[] = new Vector[2];
  boolean bForwards;
  int iStart;int iEnd;
  int iBegin;int iFinish;
    if (iAllSort > 1) { iStart = 0; iEnd = 2; iBegin = 0; iFinish = 3; bForwards = true; sDir = "D"; } 
    else { iStart = 0; iEnd = 1; iBegin = iSort; iFinish = iSort + 1; bForwards = bDirect; sDir = ""; }
    for (int iLoop = iStart;iLoop < iEnd;iLoop++) {
      for (int iBase = iBegin;iBase < iFinish;iBase++) {
        vSorted = myTools.sortHash(htSortInd,iBase,bForwards);
        vHold = addCycle(vSorted,htSortInd,htHoldInd,myIndTotals,vIndivOponAbove,vIndivOponBelow,iBase);
        if (!bForwards) { sAbove = myTools.getString(vHold[1]); sBelow = myTools.getString(vHold[0]); } 
        else { sAbove = myTools.getString(vHold[0]); sBelow = myTools.getString(vHold[1]); }
        if (!sDir.equals("")) { sPass = "_" + iBase + sDir; }
        writeIndUser(sMainUser,sAbove,sBelow,sPass,iBase);
        vSorted = new Vector();
      }
      bForwards = !bForwards;
      sDir = "U";
    }
  }

  //  Returns 2 vectors containing the sorted users put into Below and Above average lists
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  private Vector[] addCycle(Vector vSorted,Hashtable htSort,Hashtable htHold,LogTotals myTotal,Vector vRowsAbove,Vector vRowsBelow,int iBase) {
  int iCount = 0;
  Vector vReturn[] = { new Vector(),new Vector() };  
  String sUser = new String();
  Float[] fVals;
  Float fHold = new Float(0);
  Float fOld = new Float(99999);
  String sMember = new String();
    for (Iterator itSorted = vSorted.iterator(); itSorted.hasNext();) {
      iCount++;
      sUser = (String)itSorted.next();
      fVals = (Float[])htSort.get(sUser);
      fHold = fVals[iBase];
      if (fHold.equals(fOld)) { sMember = "-"; }
      else { sMember = new Integer(iCount).toString(); }
      htTags = (Hashtable)htHold.get(sUser);
      htTags.put(TAGMEMBER,sMember);
      if (fHold.floatValue() < myTotal.getNum(iBase)) { vReturn[0].add(swapTags(vRowsBelow,htTags)); } 
      else { vReturn[1].add(swapTags(vRowsAbove,htTags)); }
      fOld = fHold;
    }
    return vReturn;
  }

  //  Writes out the individual user pages
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void writeIndUser(String sUser,String sAbove,String sBelow,String sDir,int iBase) throws IOException {
  BufferedWriter bwUser;
  String sFile = new String();
  String sCUser = new String();
    if (htHoldTotals.get(sUser) != null) {
      htTags = new Hashtable();
      if (bColours) { sCUser = myTools.stripChars((String)htFun.get(sUser),aColours); }
      else { sCUser = sUser; }
      htTags.put(TAGUSER,sCUser);
      htTags.put(TAGABOVE,sAbove);
      htTags.put(TAGBELOW,sBelow);
      htTags.put(TAGTOTALS,(String)htHoldTotals.get(sUser));
      if (iAllSort > 1) { 
        htTags.put(TAGURLUP,sSortUp);
        htTags.put(TAGURLDOWN,sSortDown);
        htTags.put(TAGLINKUP,HREFBEFORE + sServer + "_" + sUser + REPVALUE + HREFMIDDLE);
        htTags.put(TAGCLOSE,HREFAFTER);
      }
      sFile = sOutDir + sAdd + "/" + sServer + "_" + sUser + sDir + ".html";
      myTools.makeDirs(sOutDir + sAdd);
      bwUser = new BufferedWriter(new FileWriter(sFile));
      bwUser.write(swapTags(vIndivMain,htTags,iBase));
      bwUser.close();
    }
  }
  
  //  Builds the totals row for the main table
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void makeTotals() {
    htTags = new Hashtable();
    htTags.put(TAGKILLS,new Integer(myTotals.getKills()).toString());
    htTags.put(TAGDEATHS,new Integer(myTotals.getDeaths()).toString());
    htTags.put(TAGSUICIDES,new Integer(myTotals.getSuicides()).toString());
    htTags.put(TAGKILLRATIO,myTotals.getKillRatio());
    htTags.put(TAGRANK,myTotals.getRank());
    htTags.put(TAGEFFICIENCY,myTotals.getEfficiency());
    sTotals = swapTags(vTableTRow,htTags);
  }

  //  Writes out the main page from the template into the bw passed in
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void writeMain(String sFile,String sPass,int iBase) throws IOException,IllegalArgumentException {
    myTools.makeDirs(sOutDir);
    BufferedWriter bwOut = new BufferedWriter(new FileWriter(sOutDir + sFile + sPass + ".html"));
    htTags = new Hashtable();
    htTags.put(TAGTITLE,sTitle);
    htTags.put(TAGFRAGS,new Integer(myTotals.getFrags()).toString());
    htTags.put(TAGUSERS,new Integer(myTotals.getTotalUsers()).toString());
    htTags.put(TAGABOVE,myTools.getString(vRowsAbove));
    htTags.put(TAGBELOW,myTools.getString(vRowsBelow));
    htTags.put(TAGWEAPONS,myTools.getString(vMainWeapons));
    htTags.put(TAGTOTALS,sTotals);
    if (iAllSort > 0) { 
      htTags.put(TAGURLUP,sSortUp);
      htTags.put(TAGURLDOWN,sSortDown);
      htTags.put(TAGLINKUP,HREFBEFORE + sFile + REPVALUE + HREFMIDDLE);
      htTags.put(TAGCLOSE,HREFAFTER);
    }
    bwOut.write(swapTags(vTableMain,htTags,iBase));
    bwOut.close();
  }

  //  Outputs stats files based on the current kill hashtable
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  protected void outputLog(String sLocOutDir,String sLocServer) throws IOException,IllegalArgumentException {
  sTitle = new String("Quake 3 Arena - " + sType + " Stats (" + sLocServer + ")");
    
    sOutDir = sLocOutDir;
    sServer = sLocServer;
    
    readStatic();

    buildUsers();
    
    buildTable();

    makeTotals();

    sortUsers();
    
  }

  //  Sets up the parser with the appropriate values
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   27th September 2002
  //
  //
  private void setupParser(String sLocAdd) {
    sAdd = sLocAdd;
    getWeaponsList(sAdd);
    myParser = new LogParser(sWeaponsList);
    sType = getType(sAdd);
  }

  //  Performs a full parse of a log file
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public void startParse(String sLog,String sInput,String sDelete) throws IOException,IllegalArgumentException {
  String[] aLog = myTools.split(sLog,PATHSEP);
  boolean bDelete = new Boolean(sDelete).booleanValue();
  File fLog;
  int iLoop = 0;
    myParser = new LogParser();
    try {
      for (iLoop = 0;iLoop < aLog.length;iLoop++) {
        fLog = new File(aLog[iLoop]);
        addLog(aLog[iLoop],sInput,false,bDelete);
      }
    } catch (FileNotFoundException e) {
      System.out.println("The input file " + aLog[iLoop] + " was not found");
    }
  }
  
  //  Performs a full output on a log file
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public void startOutput(String sInput,String sOutDir,String sServer,String sType) throws IllegalArgumentException,FileNotFoundException,IOException {
  String[] aInput = myTools.split(sInput,PATHSEP);
  File fInput;
  boolean bOutput = true;
    setupParser(sType);
    htKills = new Hashtable();
    if (checkType(sType)) {
      for (int iLoop = 0;iLoop < aInput.length;iLoop++) {
        fInput = new File(aInput[iLoop]);
        if ((fInput.exists()) && (fInput.isFile())) {
          if (!getLog(aInput[iLoop],false)) { bOutput = false; }
        } else {
          throw new IllegalArgumentException("File " + aInput[iLoop] + " doesn't exist");
        }
      }
      if (bOutput) { outputLog(sOutDir,sServer); }
    } else {
      throw new IllegalArgumentException("Invalid Type - " + sType);
    }
  }
  
  //  Performs both a full parse and full output on a log file
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public void startBoth(String sLog,String sInput,String sOutDir,String sServer,String sType,String sDelete,String sForce) throws IllegalArgumentException,IOException,FileNotFoundException {
  String[] aLog = myTools.split(sLog,PATHSEP);
  boolean bDelete = new Boolean(sDelete).booleanValue();
  boolean bForce = new Boolean(sForce).booleanValue();
  boolean bGotIt = false;
  File fLog;
  String sALog = new String();
  File fInput;
  int iLoop = 0;
    setupParser(sType);
    try {
      htKills = new Hashtable();
      if (checkType(sType)) {
        for (iLoop = 0;iLoop < aLog.length;iLoop++) {
          fLog = new File(aLog[iLoop]);
          sALog = fLog.getPath();
          if ((addLog(aLog[iLoop],sInput,false,bDelete)) || (bForce)) {
            getLog(sInput,false);
            bGotIt = true;
          }
        }
      } else {
        throw new IllegalArgumentException("Invalid Type - " + sType);
      }
    } catch (FileNotFoundException e) {
      System.out.println("The log file " + sALog + " was not found");
    }
    if (bGotIt) {
      outputLog(sOutDir,sServer);
    }
  }

  //  Called when someone runs this class, it decides what they want to do and tries to do it
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public static void main (String[] args) {
  String sError = new String();
  String sHelp = new String();
  String sProp = new String();
  boolean bFailed = false;
  int iLen = args.length;
  String sAction = new String();
  Q3Log myLog = new Q3Log();
  IllegalArgumentException eIAETest = new IllegalArgumentException();
    sHelp = PROGFULL + " v" + PROGVER + " - " + PROGDESC + LINESEP + LINESEP +
            "The following options are valid:" + LINESEP +
            "   help" + LINESEP +
            "   parse Config LogName[@LogName@...] InputName Delete" + LINESEP +
            "   output Config InputName[@InputName@..] OutputDir ServerName Type" + LINESEP +
            "   both Config LogName[@LogName@...] InputName OutputDir ServerName Type Delete" + LINESEP + 
            "        Force" + LINESEP +
            LINESEP +
            "   help        - This help output" + LINESEP +
            LINESEP +
            "   parse       - This option will just take a standard Quake 3 logfile and" + LINESEP +
            "                 produce a Q3Log file" + LINESEP +
            LINESEP +
            "   output      - This will take a Q3Log file and produce the html output files" + LINESEP +
            LINESEP +
            "   both        - This will take a standard Quake 3 logfile and produce the html" + LINESEP + 
            "                 output files (and the Q3Log file)" + LINESEP +
            LINESEP +
            "   Config      - The fully qualified path of the config file to use" + LINESEP +
            "   LogName     - The fully qualified path of the logfile (for multiple logfiles" + LINESEP +
            "                 seperate them by @)" + LINESEP +
            "   InputName   - The fully qualified path of the Q3Log file to get the stats" + LINESEP +
            "                 from (for multiple files seperate them by @)" + LINESEP +
            "   OutputDir   - The fully qualified path of the directory to put the output" + LINESEP +
            "                 files into" + LINESEP +
            "   Delete      - (True|False) Delete logfile after parsing?" + LINESEP +
            "   ServerName  - The name of the server who's stats we are generating" + LINESEP +
            "   Type        - (standard|arena|hh3|ut) The game type of the logfile" + LINESEP +
            "                 (arena = Rocket Arena 3,hh3 = Headhunters 3,ut = Urban Terror)" + LINESEP +
            "   Force       - (True|False) If true the html output will always be" + LINESEP +
            "                 generated, if false then the html output will only" + LINESEP +
            "                 be generated if their were new log files to parse" + LINESEP +
            "   Delete      - (True|False) Delete logfile after parsing?";
    sError = "Error: Incorrect arguments set" + LINESEP + sHelp;
    sProp = "There was an error with your config file." + LINESEP + sHelp;
    if ((iLen > 0) && (iLen < 10)) { 
      try {
        sAction = args[0];
        if (sAction.equalsIgnoreCase(ARGHELP)) {
          sError = sHelp;
          bFailed = true;
        } else if (sAction.equalsIgnoreCase(ARGPARSE)) {
          if (iLen == 5) {
            if (myLog.readProperties(args[1])) {
              myLog.startParse(args[2],args[3],args[4]);
            } else {
              bFailed = true;
              sError = sProp;
            }
          } else {
            bFailed = true;
          }
        } else if (sAction.equalsIgnoreCase(ARGOUTPUT)) {
          if (iLen == 6) {
            if (myLog.readProperties(args[1])) {
              args[3] = myLog.myTools.checkDir(args[3]);
              myLog.startOutput(args[2],args[3],args[4],args[5]);
            } else {
              sError = sProp;
              bFailed = true;
            }
          } else {
            bFailed = true;
          }
        } else if (sAction.equalsIgnoreCase(ARGBOTH)) {
          if (iLen == 9) {
            if (myLog.readProperties(args[1])) {
              args[4] = myLog.myTools.checkDir(args[4]);
              myLog.startBoth(args[2],args[3],args[4],args[5],args[6],args[7],args[8]);
            } else {
              sError = sProp;
              bFailed = true;
            }
          } else {
            bFailed = true;
          }
        } else {
          bFailed = true;
        }
      } catch (Exception e) {
        if (e.getClass() == eIAETest.getClass()) {
         System.out.println(e.getMessage()); 
        } else {
          System.out.println("Exception - " + e.toString());
          e.printStackTrace();
        }
      }
    } else {
      bFailed = true;
    }
    if (bFailed) { System.out.println(sError); }
  }
  
}