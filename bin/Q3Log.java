import java.io.*;
import java.util.*;
import java.text.*;
import java.util.Date;

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

// The following contains all the different lines that are defined as System (feel free to add to it for other mods)
public static final String[] TXTSYSTEM = {
  "InitGame",
  "Map",
  "Default pickup mode is",
  "arena.cfg info for map found",
  "ClientUserinfoChanged",
  "ClientConnect",
  "ClientDisconnect",
  "ShutdownGame",
  "red",
  "Exit",
  "ClientBegin",
  "score",
  "Item",
  "tell",
  "spawnhead",
  "touchhead",
  "AltarScore",
  "ClientUserinfo",
  "Warmup"
};
public static final String TXTSAY = new String("say"); // The text preceding a text line
public static final String TXTKILL = new String("Kill"); // The text preceding a frag line
public static final String TXTKILLED = new String("killed"); // The text seperating the killers name from the preys name
public static final String TXTGAME = new String("------------------------------------------------------------"); // New game line
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
  "Unknown - 17",
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
  "Unknown - 12",
  "Unknown - 13",
  "Unknown - 14",
  "Unknown - 15",
  "Lava",
  "Unknown - 17",
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

// The following define the different line types in a logfile
public static final int NOTHING = 0; // We dont know what it is
public static final int SYSTEM = 1; // System (ignored)
public static final int CHAT = 2; // Say commands (ignored)
public static final int KILL = 3; // A frag
public static final int GAME = 4; // New game (ignored)

public static final int INDENT = 7; // Indent in Logfile (usually 7)
public static final String BREAK = new String(":"); // Whats after the commands but before the text in the log file
public static final String ARGHELP = new String("HELP"); // Help argument
public static final String ARGPARSE = new String("PARSE"); // Parse argument
public static final String ARGOUTPUT = new String("OUTPUT"); // Output argument
public static final String ARGBOTH = new String("BOTH"); // Both argument
public static final String TYPESTANDARD = new String("STANDARD"); // Standard Type
public static final String TYPEARENA = new String("ARENA"); // Arena Type
public static final String TYPEHH3 = new String("HH3"); // Headhunters Type
public static final String TYPEUT3 = new String("UT3"); // Urban Terror Type
public static final String TEXTSTANDARD = new String("Standard"); // Standard Text
public static final String TEXTARENA = new String("Rocket Arena"); // Arena Text
public static final String TEXTHH3 = new String("Headhunters"); // Headhunters Text
public static final String TEXTUT3 = new String("Urban Terror"); // Urban Terror Text
public static final String SPACE = new String(" "); // Blank space text
public static final String EQUALS = new String("="); // Equals sign
public static final String OPENTAG = new String("<Q3LOG "); // Start of template tag
public static final String FAKEOPENTAG = new String("<Q3L0G "); // Fake start of template tag
public static final String CLOSETAG = new String(">"); // End of template tag
public static final String LINENAME = "NAME"; // Internal (Label for the name of the tag)
public static final String LINETYPE = "TYPE"; // Internal (Label for the type of the tag)
public static final String STATICIM = new String("indiv_main.htm_"); // File name for Main Individual page
public static final String STATICIO = new String("indiv_oponents.htm_"); // File name for Oponents Individual page
public static final String STATICIW = new String("indiv_weapons.htm_"); // File name for Weapons Individual page
public static final String STATICIOT = new String("indiv_oponents_total.htm_"); // File name for Oponents Totals Individual page
public static final String STATICIWT = new String("indiv_weapons_total.htm_"); // File name for Weapons Totals Individual page
public static final String STATICTABLE = new String("table_main.htm_"); // File name for Main page
public static final String STATICROW = new String("table_row.htm_"); // File name for Main Table Row page
public static final String STATICTOTAL = new String("table_total.htm_"); // File name for Table Total Row page
public static final String PROPSTATIC = new String("Static"); // Static property name (for conf file)
public static final String PROPTARGET = new String("Target"); // Target property name (for conf file)
public static final String TAGVALUE = "VALUE"; // Type of Tag that is replace by a value
public static final String TAGSECTION = "SECTION"; // Type of Tag that is replaced by another file
public static final String TAGSYSTEM = "SYSTEM"; // Type of Tag that is replaced by a system value

// The following are tag replacements values for internal values, used in the template files
public static final String TAGDEATHS = new String("DEATHS");
public static final String TAGEFFICIENCY = new String("EFFICIENCY");
public static final String TAGFRAGS = new String("FRAGS");
public static final String TAGKILLRATIO = new String("KILLRATIO");
public static final String TAGKILLS = new String("KILLS");
public static final String TAGOPONENT = new String("OPONENT");
public static final String TAGOPONENTS = new String("OPONENTS");
public static final String TAGSUICIDES = new String("SUICIDES");
public static final String TAGRANK = new String("RANK");
public static final String TAGROWS = new String("ROWS");
public static final String TAGTITLE = new String("TITLE");
public static final String TAGTOTALS = new String("TOTALS");
public static final String TAGUSER = new String("USER");
public static final String TAGUSERLINK = new String("USER_LINK");
public static final String TAGWEAPON = new String("WEAPON");
public static final String TAGWEAPONS = new String("WEAPONS");

private static final String SYSDATE = "DATE"; // Value asking for Date
private static final String SYSTIME = "TIME"; // Value asking for Time

private static final String PATHSEP = new String("@"); // What seperates the paths in the command line arguments
private static final String LINESEP = System.getProperty("line.separator"); // The line seperator for this system
private static final String DIRSEP = System.getProperty("file.separator"); // The directory seperator for this system
private static final String TXTSUICIDE = new String("<world>"); // Used to tell when a kill is actually a suicide (also if Killer = Killed)
private static final int SUICIDE = 0; // Suicides user id
private int iNextUserID = 1; // Holds the next UserId to use

// The following vectors hold the template file lines
private Vector vIndivMain = new Vector();
private Vector vIndivOpon = new Vector();
private Vector vIndivWeap = new Vector();
private Vector vIndivTOpo = new Vector();
private Vector vIndivTWea = new Vector();
private Vector vTableMain = new Vector();
private Vector vTableRows = new Vector();
private Vector vTableTRow = new Vector();

private Hashtable htUsers = null; // Holds a list of users and their details
private Hashtable htKills = new Hashtable(); // Holds all the kills in the logfile
private Properties prProp = new Properties(); // Properties read from the config file

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
  
  
  //  Returns the passed string with a "/" or "\" at the end if it doesnt have one
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private String checkDir(String sDir) {
  String sEnd = new Character(sDir.charAt(sDir.length() - 1)).toString();
  String sSep = DIRSEP;
  String sReturn = new String(sDir);
    if (!sEnd.equals(sSep)) { sReturn = sReturn + sSep; }
    return sReturn;
  }

  //  Returns the line type for the passed in line
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private int getLineType(String sLine) {
  int iReturn = NOTHING;
    if ((sLine.length() >= TXTSAY.length()) && (sLine.substring(0,TXTSAY.length()).equalsIgnoreCase(TXTSAY))) {
      iReturn = CHAT;
    } else if ((sLine.length() >= TXTKILL.length()) && (sLine.substring(0,TXTKILL.length()).equalsIgnoreCase(TXTKILL))) {
      iReturn = KILL;
    } else if ((sLine.length() >= TXTGAME.length()) && (sLine.substring(0,TXTGAME.length()).equalsIgnoreCase(TXTGAME))) {
      iReturn = GAME;
    } else {
      for (int iLoop = 0;iLoop < TXTSYSTEM.length;iLoop++) {
        if ((sLine.length() >= TXTSYSTEM[iLoop].length()) && (sLine.substring(0,TXTSYSTEM[iLoop].length()).equalsIgnoreCase(TXTSYSTEM[iLoop]))) {
          iReturn = SYSTEM;
          break;
        }
      }
    }      
    return iReturn;
  }

  //  Returns the passed in string minus special characters (denoted by '^##')
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private String stripChars(String sUser) {
  String sBefore = new String();
  String sAfter = new String();
  int iStart = 0;
    while (sUser.indexOf('^') > -1) {
      iStart = sUser.indexOf('^');
      if (iStart > 0) { sBefore = sUser.substring(0,iStart); }
      iStart = iStart + 2;
      sAfter = sUser.substring(iStart);
      sUser = sBefore + sAfter;
    }
    return sUser.replaceAll("<","(").replaceAll(">",")").replaceAll("\\*","_").replaceAll("\\?","_");
  }
  
  //  Returns the ID for the user passed in, creating one if needed
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private int getUserID(String sUser) {
  int iReturn = 0;
    if (!sUser.equalsIgnoreCase(TXTSUICIDE)) {
      if (htUsers == null) {
        htUsers = new Hashtable();
        htUsers.put(new Integer(iNextUserID),sUser);
        iReturn = 1;
        iNextUserID++;
        htUsers.put(new Integer(SUICIDE),TXTSUICIDE);
      } else {
        sUser = stripChars(sUser);
        if (htUsers.containsValue(sUser)) {
          String sCurrent = new String();
          Integer iCurrent = new Integer(0);
          for (Enumeration eLocal = htUsers.keys();eLocal.hasMoreElements();) {
            iCurrent = (Integer)eLocal.nextElement();
            sCurrent = (String)htUsers.get(iCurrent);
            if (sCurrent.equalsIgnoreCase(sUser)) {
              iReturn = iCurrent.intValue();
              break;
            }
          }
        } else {
          htUsers.put(new Integer(iNextUserID),sUser);
          iReturn = iNextUserID;
          iNextUserID++;
        }
      }
    } else {
      iReturn = SUICIDE;
    }
    return iReturn;
  }
  
  //  Returns the User ID for the killer on the line passed in
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private int getKiller(String sLine) {
  String sUser = new String();
  int iStart = 0;
  int iEnd = 0;
    iStart = sLine.indexOf(BREAK,TXTKILL.length() + BREAK.length() + 1) + 2;
    iEnd = sLine.indexOf(SPACE,iStart);
    sUser = sLine.substring(iStart,iEnd);
    return getUserID(sUser);
  }
  
  //  Returns the User ID for the prey on the line passed in
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private int getKilled(String sLine) {
  String sUser = new String();
  int iStart = 0;
  int iEnd = 0;
    iStart = sLine.indexOf(TXTKILLED) + TXTKILLED.length() + 1;
    iEnd = sLine.indexOf(SPACE,iStart);
    sUser = sLine.substring(iStart,iEnd);
    return getUserID(sUser);
  }
  
  //  Returns the Weapon ID for the weapon used in the frag line passed in
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private int getWeapon(String sLine) {
  int iReturn = 0;
  int iStart = 0;
  int iEnd = 0;
  String sWeapon = new String();
    iEnd = sLine.indexOf(BREAK,TXTKILL.length() + BREAK.length() + 1);
    iStart = sLine.indexOf(BREAK,TXTKILL.length() + BREAK.length() + 1) - 2;
    if (sLine.charAt(iStart) == ' ') { iStart++; }
    iEnd = sLine.indexOf(SPACE,iStart) - 1;
    sWeapon = sLine.substring(iStart,iEnd);
    iReturn = new Integer(sWeapon).intValue();
    return iReturn;
  }
  
  //  Adds the kill details passed in to the main Hashtable of kills (htKills)
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void addKill(int iKiller,int iKilled,int iWeapon) {
  Hashtable htPeople = new Hashtable();
  Hashtable htWeapon = new Hashtable();
  Integer iHolder = new Integer(0);
  int iKills = 1;
  int iHold = 0;
    iHolder = new Integer(iKiller);
    if (htKills.get(iHolder) != null) {
      htPeople = (Hashtable)htKills.get(iHolder);
      iHolder = new Integer(iKilled);
      if (htPeople.get(iHolder) != null) {
        htWeapon = (Hashtable)htPeople.get(iHolder);
        iHolder = new Integer(iWeapon);
        if (htWeapon.get(iHolder) != null) {
          iKills = ((Integer)htWeapon.get(iHolder)).intValue() + 1;
        }
      }
    }
    htWeapon.put(new Integer(iWeapon),new Integer(iKills));
    htPeople.put(new Integer(iKilled),htWeapon);
    htKills.put(new Integer(iKiller),htPeople);
  }
  
  //  Processes a frag line by getting the killer, killed and weapon and passing them to addKill
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private void processKill(String sLine) {
  int iKiller = 0;
  int iKilled = 0;
  int iWeapon = 0;
    iKiller = getKiller(sLine);
    iKilled = getKilled(sLine);
    iWeapon = getWeapon(sLine);
    addKill(iKiller,iKilled,iWeapon);
  }

  //  Makes sure the directory passed exists
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private boolean makeDirs(File fFile) {
    return fFile.mkdirs();
  }
  private boolean makeDirs(String sFile) { return makeDirs(new File(sFile)); }

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
  private String[] getWeaponsList(String sParam) {
  String [] sWeaponsList = TXTQ3WEAPONS;
    if (sParam.equalsIgnoreCase(TYPEARENA)) { sWeaponsList = TXTQ3WEAPONS; } 
    else if (sParam.equalsIgnoreCase(TYPEHH3)) { sWeaponsList = TXTHH3WEAPONS; } 
    else if (sParam.equalsIgnoreCase(TYPEUT3)) { sWeaponsList = TXTUT3WEAPONS; }
    return sWeaponsList;
  }

  //  Makes sure that the passed in string is in the format '####.##' or similar, and passes it back
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private String decimalPlaces(String sValue) {
  String sBefore = new String();
  String sAfter = new String();
  int iPos = sValue.indexOf(".");
  int iLen = sValue.length();
    if (iPos > -1) {
      if (iPos != 0) {
        if (iPos != iLen) {
          sBefore = sValue.substring(0,iPos);
          sAfter = sValue.substring(iPos + 1);
        } else {
          sBefore = sValue.substring(0,iLen - 1);
          sAfter = "00";
        }
      } else {
        sBefore = "0";
        sAfter = sValue.substring(1);
      }
    } else {
      sBefore = sValue;
      sAfter = "00";
    }
    if (sAfter.length() > 2) { sAfter = sAfter.substring(0,2); }
    while (sAfter.length() < 2) { sAfter = sAfter + "0"; }
    return sBefore + "." + sAfter;
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
    if (fInput.exists()) {
      parseLog(fInput.getPath(),sOut,bRead);
      if (bDelete) { bReturn = fInput.delete(); }
      else { bReturn = true; }
    }
    return bReturn;
  }
  
  //  Reads the config file into a properties object
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private boolean readProperties(String sProp) throws FileNotFoundException,IOException {
  File fProp = new File(sProp);
  boolean bReturn = false;
    if ((fProp.exists()) && (fProp.isFile())) {
      prProp.load(new FileInputStream(fProp));
      bReturn = true;
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
  File fInput = new File(sFile);
  boolean bReturn = false;
    if (fInput.exists()) {
      readLog(sFile,bClear);
      bReturn = true;
    } else {
      htKills = new Hashtable();
    }
    return bReturn;
  }

  //  Returns a vector of the lines of the passed in file
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  private Vector readFile(File fIn) throws IllegalArgumentException,FileNotFoundException,IOException {
  BufferedReader brIn;
  Vector vReturn = new Vector();
  String sLine = new String();
    if ((fIn.exists()) && (fIn.isFile())) {
      brIn = new BufferedReader(new FileReader(fIn));
      sLine = brIn.readLine();
      if (sLine != null) {
        while (sLine != null) {
          vReturn.add(sLine);
          sLine = brIn.readLine();
        }
      } else {
        throw new IllegalArgumentException("File " + fIn.getPath() + " is empty");
      }
    } else {
      throw new FileNotFoundException("File " + fIn.getPath() + " doesn't exist");
    }
    return vReturn;
  }

  //  Reads the static template files into their vectors
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  private void readStatic(String sPath) throws IllegalArgumentException,FileNotFoundException,IOException {
  File fIn;
    sPath = checkDir(sPath);
    fIn = new File(sPath + STATICIM);
    vIndivMain = readFile(fIn);
    fIn = new File(sPath + STATICIO);
    vIndivOpon = readFile(fIn);
    fIn = new File(sPath + STATICIW);
    vIndivWeap = readFile(fIn);
    fIn = new File(sPath + STATICIWT);
    vIndivTWea = readFile(fIn);
    fIn = new File(sPath + STATICIOT);
    vIndivTOpo = readFile(fIn);
    fIn = new File(sPath + STATICTABLE);
    vTableMain = readFile(fIn);
    fIn = new File(sPath + STATICROW);
    vTableRows = readFile(fIn);
    fIn = new File(sPath + STATICTOTAL);
    vTableTRow = readFile(fIn);
    
  }

  //  Returns the parameter value from the current tag
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  private String getParam(String sTag,String sParam) { 
  int iSta = 0;
  String sReturn = new String();
    if (sTag.indexOf(sParam) > -1) {
      iSta = sTag.indexOf('"',sTag.indexOf(sParam)) + 1;
      sReturn = sTag.substring(iSta,sTag.indexOf('"',iSta)).toUpperCase(); 
    }
    return sReturn;
  }

  //  Returns a string representation of the passed in vector
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  private String getString(Vector vIn) {
  String sReturn = new String();
  String sLine = new String();
  Object objHold;
    for (Iterator itLocal = vIn.iterator(); itLocal.hasNext();) {
      sReturn = sReturn + LINESEP + (String)itLocal.next();
    }
    return sReturn;
  }

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
  private Vector swapTags(Vector vInput,Hashtable htTags) {
  String sLine = new String();
  Vector vReturn = new Vector();
  String sTag = new String();
  String sName = new String();
  String sType = new String();
  String sValue = new String();
  Vector vHold = new Vector();
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
        } else if (sType.equals(TAGSYSTEM)) {
          sValue = getSysVal(sName);
        } else {
          sValue = sTag.replaceAll(OPENTAG,FAKEOPENTAG);
        }
        sLine = replaceAll(sLine,sTag,sValue);
      }
      sLine.replaceAll(FAKEOPENTAG,OPENTAG);
      vReturn.add(sLine);
    }
    return vReturn;
  }

  //  Parses a log file and outputs a compressed version containing just the frag lines
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  protected void parseLog(String sInFile,String sOutFile,boolean bRead) throws FileNotFoundException,IOException {
  BufferedReader brIn = new BufferedReader(new FileReader(sInFile));
  BufferedWriter bwOut;
  File fOut = new File(sOutFile);
  String sLine = new String();
  int iType;
    sLine = brIn.readLine();
    if (sLine != null) {
      makeDirs(fOut.getParentFile());
      bwOut = new BufferedWriter(new FileWriter(fOut.getPath(),true));
      while (sLine != null) {
        sLine = sLine.substring(INDENT);
        iType = getLineType(sLine);
        if (iType == SYSTEM) {
          // Do nothing
        } else if (iType == KILL) {
          // Got a kill
          if (bRead) { processKill(sLine); }
          bwOut.write(sLine + LINESEP);
        } else if (iType == CHAT) {
          // Do nothing
        } else if (iType == GAME) {
          // Do nothing
        } else if (iType == SYSTEM) {
          // Do nothing
        } else if (iType == NOTHING) {
          // Do nothing
        } else {
          // Something broke, but do nothing anyway
          //System.out.println("Something broke");
        }
        sLine = brIn.readLine();
      }
      brIn.close();
      bwOut.close();
    } else {
      throw new IOException("File was empty");
    }
  }
  
  //  Reads in a compressed log file into the main kills hashtable
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  protected void readLog(String sInFile,boolean bNew) throws FileNotFoundException,IOException {
  BufferedReader brIn = new BufferedReader(new FileReader(sInFile));
  String sLine = new String();
  int iType;
    if (bNew) { htKills = new Hashtable(); }
    sLine = brIn.readLine();
    if (sLine != null) {
      while (sLine != null) {
        iType = getLineType(sLine);
        if (iType == KILL) {
          // Got a kill
          processKill(sLine);
        } else {
          // Something broke, ignore it (the file we have been passed may be a standard logfile)
          //System.out.println("Something broke");
        }
        sLine = brIn.readLine();
      }
      brIn.close();
    } else {
      throw new IOException("File was empty");
    }
  }

  //  Outputs stats files based on the current kill hashtable
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  protected void outputLog(String sOutDir,String sServer,String sAdd) throws IOException,IllegalArgumentException {
  String[] sWeaponsList = getWeaponsList(sAdd);
  String sType = getType(sAdd);
  String sTitle = new String("Quake 3 Arena - " + sType + " Stats (" + sServer + ")");
  String sUser = new String();
  String sOponent = new String();
  String sWeapon = new String();
  String sFile = new String();
  String sAbsTotKillRatio = new String();
  String sKillRatio = new String();
  String sLink = new String();
  String sHold = new String();
  String sCSS = new String();
  String sTarget = new String("individual");
  String sStatic = new String();
  String sTotals = new String();

  Integer iDeaths[] = new Integer[3];
  Integer iHolder = new Integer(0);
  Integer iUser = new Integer(0);
  Integer iOponent = new Integer(0);

  Vector vWeapons = new Vector();
  Vector vOponent = new Vector();
  Vector vTOponent = new Vector();
  Vector vRows = new Vector();

  Hashtable htPeople = new Hashtable();
  Hashtable htPerson = new Hashtable();
  Hashtable htWeapon = new Hashtable();
  Hashtable htMainUser = new Hashtable();
  Hashtable htHold = new Hashtable();
  Hashtable htSort = new Hashtable();
  Hashtable htScores = new Hashtable();
  Hashtable htTags = new Hashtable();
  Hashtable htHoldTotals = new Hashtable();

  BufferedWriter bwOut;
  BufferedWriter bwUser;

  int iWeapon = 0;int iKills = 0;int iKill = 0;int iDeath = 0;int iTotalKills = 0;int iPercent = 0;int iStart = 0;
  int iRank = 0;int iAbsTotFrags = 0;int iAbsTotKills = 0;int iAbsTotDeaths = 0;int iAbsTotSuicides = 0;
  int iAbsTotRank = 0;int iAbsTotEfficiency = 0;int iAbsTotUsers = 0;int iTotKills = 0;int iTotDeaths = 0;
  int[] iWeaponTotals;
  float fKills;float fDeaths;float fSuicides;float fAbsTotKillRatio = 0;float fAbsTotUsers = 0;
  
    if ((prProp.get(PROPSTATIC) != null) && (!prProp.get(PROPSTATIC).equals(""))) { sStatic = (String)prProp.get(PROPSTATIC); }
    else { throw new IllegalArgumentException("Static needs to be set in the config file"); }
    if ((prProp.get(PROPTARGET) != null) && (!prProp.get(PROPTARGET).equals(""))) { sTarget = (String)prProp.get(PROPTARGET); }

    readStatic(sStatic);
 
    for (Enumeration eKills = htKills.keys();eKills.hasMoreElements();) {
      iUser = (Integer)eKills.nextElement();
      sUser = (String)htUsers.get(iUser);
      iTotalKills = 0;
      iTotKills = 0;
      iTotDeaths = 0;
      iWeaponTotals = new int[sWeaponsList.length];
      htPeople = (Hashtable)htKills.get(iUser);
      vWeapons = new Vector();
      vOponent = new Vector();
      vTOponent = new Vector();
      for (Enumeration ePeople = htPeople.keys();ePeople.hasMoreElements();) {
        iOponent = (Integer)ePeople.nextElement();
        sOponent = (String)htUsers.get(iOponent);
        if (sOponent.equals(sUser)) {
          iKills = 0;
          htWeapon = (Hashtable)htPeople.get(iUser);
          for (Enumeration eWeapon = htWeapon.keys();eWeapon.hasMoreElements();) {
            iWeapon = ((Integer)eWeapon.nextElement()).intValue();
            iKill = ((Integer)htWeapon.get(new Integer(iWeapon))).intValue();
          }
          if (htMainUser.get(sUser) != null) {
            iDeaths = (Integer[])htMainUser.get(sUser);
            iHolder = iDeaths[2];
            if (iHolder == null) { iHolder = new Integer(0); }
            iDeaths[2] = new Integer(iHolder.intValue() + iKills);
            htMainUser.put(sUser,iDeaths);
          } else {
            iDeaths = new Integer[3];
            iDeaths[2] = new Integer(iKills);
            htMainUser.put(sUser,iDeaths);
          }
        } else {
          vWeapons = new Vector();
          iKills = 0;
          htWeapon = (Hashtable)htPeople.get(iOponent);
          for (Enumeration eWeapon = htWeapon.keys();eWeapon.hasMoreElements();) {
            iWeapon = ((Integer)eWeapon.nextElement()).intValue();
            sWeapon = sWeaponsList[iWeapon];
            iKill = ((Integer)htWeapon.get(new Integer(iWeapon))).intValue();
            iWeaponTotals[iWeapon] += iKill;
            iKills += iKill;
            htTags = new Hashtable();
            htTags.put(TAGWEAPON,sWeapon);
            htTags.put(TAGKILLS,new Integer(iKill).toString());
            vWeapons.add(getString(swapTags(vIndivWeap,htTags)));
          }
          if (sUser.equalsIgnoreCase(TXTSUICIDE)) {
            if (htMainUser.get(sOponent) != null) {
              iDeaths = (Integer[])htMainUser.get(sOponent);
              iHolder = iDeaths[2];
              if (iHolder == null) { iHolder = new Integer(0); }
              iDeaths[2] = new Integer(iHolder.intValue() + iKills);
              htMainUser.put(sOponent,iDeaths);
            } else {
              iDeaths = new Integer[3];
              iDeaths[2] = new Integer(iKills);
              htMainUser.put(sOponent,iDeaths);
            }
          } else {
            iTotalKills += iKills;
            htPerson = (Hashtable)htKills.get(iOponent);
            if (htPerson != null) {
              htWeapon = (Hashtable)htPerson.get(iUser);
              if (htWeapon != null) { 
                iDeath = 0;
                for (Enumeration eWeapon = htWeapon.keys();eWeapon.hasMoreElements();) {
                  iWeapon = ((Integer)eWeapon.nextElement()).intValue();
                  iDeath += ((Integer)htWeapon.get(new Integer(iWeapon))).intValue();
                }
              } else { iDeath = 0; }
            } else { iDeath = 0; }
            iTotKills += iKills;
            iTotDeaths += iDeath;
            fKills = new Integer(iKills).floatValue();
            fDeaths = new Integer(iDeath).floatValue();
            iPercent = new Float((100 / (fKills + fDeaths)) * fKills).intValue();
            htTags = new Hashtable();
            htTags.put(TAGOPONENT,sOponent);
            htTags.put(TAGKILLS,new Integer(iKills).toString());
            htTags.put(TAGDEATHS,new Integer(iDeath).toString());
            htTags.put(TAGEFFICIENCY,new Integer(iPercent).toString());
            htTags.put(TAGWEAPONS,getString(vWeapons));
            vOponent.add(getString(swapTags(vIndivOpon,htTags)));
            if (htMainUser.get(sOponent) != null) {
              iDeaths = (Integer[])htMainUser.get(sOponent);
              iHolder = iDeaths[1];
              if (iHolder == null) { iHolder = new Integer(0); }
              iDeaths[1] = new Integer(iHolder.intValue() + iKills);
              htMainUser.put(sOponent,iDeaths);
            } else {
              iDeaths = new Integer[3];
              iDeaths[1] = new Integer(iKills);
              htMainUser.put(sOponent,iDeaths);
            }
          }
        }
      }
      if (!sUser.equalsIgnoreCase(TXTSUICIDE)) {
        if (htMainUser.get(sUser) != null) {
          iDeaths = (Integer[])htMainUser.get(sUser);
          iDeaths[0] = new Integer(iTotalKills);
          htMainUser.put(sUser,iDeaths);
        } else {
          iDeaths = new Integer[3];
          iDeaths[0] = new Integer(iTotalKills);
          htMainUser.put(sUser,iDeaths);
        }
        fKills = new Integer(iTotKills).floatValue();
        fDeaths = new Integer(iTotDeaths).floatValue();
        iPercent = new Float((100 / (fKills + fDeaths)) * fKills).intValue();
        vWeapons = new Vector();
        for (int iLoop = 0;iLoop < sWeaponsList.length;iLoop++) {
          if (iWeaponTotals[iLoop] > 0) {
            htTags = new Hashtable();
            htTags.put(TAGWEAPON,sWeaponsList[iLoop]);
            htTags.put(TAGKILLS,new Integer(iWeaponTotals[iLoop]).toString());
            vWeapons.add(getString(swapTags(vIndivTWea,htTags)));
          }
        }
        htTags = new Hashtable();
        htTags.put(TAGKILLS,new Integer(iTotKills).toString());
        htTags.put(TAGDEATHS,new Integer(iTotDeaths).toString());
        htTags.put(TAGEFFICIENCY,new Integer(iPercent).toString());
        htTags.put(TAGWEAPONS,getString(vWeapons));
        vTOponent.add(getString(swapTags(vIndivTOpo,htTags)));
        htHoldTotals.put(sUser,getString(vTOponent));
        htSort.put(sUser,getString(vOponent));
      }
    }
    for (Enumeration eUsers = htMainUser.keys();eUsers.hasMoreElements();) {
      sUser = (String)eUsers.nextElement();
      iDeaths = (Integer[])htMainUser.get(sUser);
      for (int iLoop = 0;iLoop < 3;iLoop++) { if (iDeaths[iLoop] == null) { iDeaths[iLoop] = new Integer(0); } }
      fKills = iDeaths[0].floatValue();
      fDeaths = iDeaths[1].floatValue();
      fSuicides = iDeaths[2].floatValue();
      iPercent = new Float((100 / (fKills + fDeaths)) * (fKills - (fSuicides * 2))).intValue();
      sKillRatio = decimalPlaces(new Float(fKills / fDeaths).toString());
      iRank = new Float(fKills - fDeaths - fSuicides).intValue();
      sLink = "<A HREF=\"" + sAdd + "/" + sServer + "_" + sUser + ".html\" TARGET=\"" + sTarget + "\">";
      
      htTags = new Hashtable();
      htTags.put(TAGUSERLINK,sLink);
      htTags.put(TAGUSER,sUser);
      htTags.put(TAGKILLS,iDeaths[0].toString());
      htTags.put(TAGDEATHS,iDeaths[1].toString());
      htTags.put(TAGSUICIDES,iDeaths[2].toString());
      htTags.put(TAGKILLRATIO,sKillRatio);
      htTags.put(TAGRANK,new Integer(iRank).toString());
      htTags.put(TAGEFFICIENCY,new Integer(iPercent).toString());
      
      htHold.put(sUser,getString(swapTags(vTableRows,htTags)));
      iAbsTotFrags += iDeaths[0].intValue();
      if (iDeaths[0].intValue() + iDeaths[1].intValue() + iDeaths[2].intValue() > 9) {
        iAbsTotKills += iDeaths[0].intValue();
        iAbsTotDeaths += iDeaths[1].intValue();
        iAbsTotSuicides += iDeaths[2].intValue();
        iAbsTotRank += iRank;
        iAbsTotEfficiency += iPercent;
        fAbsTotKillRatio += new Float(fKills / fDeaths).floatValue();
        iAbsTotUsers++;
        if (htScores.get(new Integer(iPercent)) != null) { htScores.put(new Integer(iPercent),(String)htScores.get(new Integer(iPercent)) + "," + sUser); }
        else { htScores.put(new Integer(iPercent),sUser); }
      }
    }
    for (int iLoop = 100;iLoop > -1;iLoop--) {
      if (htScores.get(new Integer(iLoop)) != null) {
        sUser = (String)htScores.get(new Integer(iLoop));
        if (sUser.indexOf(",") < 0) { 
          vRows.add((String)htHold.get(sUser));
          if (htSort.get(sUser) != null) { 
            sFile = sOutDir + sAdd + "/" + sServer + "_" + sUser + ".html";
            makeDirs(sOutDir + sAdd);
            bwUser = new BufferedWriter(new FileWriter(sFile));
            htTags = new Hashtable();
            htTags.put(TAGUSER,sUser);
            htTags.put(TAGOPONENTS,(String)htSort.get(sUser));
            htTags.put(TAGTOTALS,(String)htHoldTotals.get(sUser));
            bwUser.write(getString(swapTags(vIndivMain,htTags)));
            bwUser.close();
          }
        } else {
          while (sUser.length() > 0) {
            iStart = sUser.indexOf(",");
            if (iStart == -1) { 
              sHold = sUser.substring(0); 
              sUser = "";
            } else { 
              sHold = sUser.substring(0,iStart); 
              sUser = sUser.substring(iStart + 1);
            }
            vRows.add((String)htHold.get(sHold));
            if (htSort.get(sHold) != null) { 
              sFile = sOutDir + sAdd + "/" + sServer + "_" + sHold + ".html";
              makeDirs(sOutDir + sAdd);
              bwUser = new BufferedWriter(new FileWriter(sFile));
              htTags = new Hashtable();
              htTags.put(TAGUSER,sHold);
              htTags.put(TAGOPONENTS,(String)htSort.get(sHold));
              htTags.put(TAGTOTALS,(String)htHoldTotals.get(sHold));
              bwUser.write(getString(swapTags(vIndivMain,htTags)));

              bwUser.close();
            }
          }
        }
      }
    }
    fKills = new Float(iAbsTotKills).floatValue();
    fDeaths = new Float(iAbsTotDeaths).floatValue();
    fSuicides = new Float(iAbsTotSuicides).floatValue();
    fAbsTotUsers = new Float(iAbsTotUsers).floatValue();
    iAbsTotKills = new Float(fKills / fAbsTotUsers).intValue();
    iAbsTotDeaths = new Float(fDeaths / fAbsTotUsers).intValue();
    iAbsTotSuicides = new Float(fSuicides / fAbsTotUsers).intValue();
    iAbsTotEfficiency = new Float(iAbsTotEfficiency / iAbsTotUsers).intValue();
    sAbsTotKillRatio = decimalPlaces(new Float(fAbsTotKillRatio / iAbsTotUsers).toString());
    iAbsTotRank = new Float(iAbsTotRank / iAbsTotUsers).intValue();

    htTags = new Hashtable();
    htTags.put(TAGKILLS,new Integer(iAbsTotKills).toString());
    htTags.put(TAGDEATHS,new Integer(iAbsTotDeaths).toString());
    htTags.put(TAGSUICIDES,new Integer(iAbsTotSuicides).toString());
    htTags.put(TAGKILLRATIO,sKillRatio);
    htTags.put(TAGRANK,new Integer(iAbsTotRank).toString());
    htTags.put(TAGEFFICIENCY,new Integer(iAbsTotEfficiency).toString());
    sTotals = getString(swapTags(vTableTRow,htTags));

    makeDirs(sOutDir);
    bwOut = new BufferedWriter(new FileWriter(sOutDir + sServer + "_" + sAdd + ".html"));

    htTags = new Hashtable();
    htTags.put(TAGTITLE,sTitle);
    htTags.put(TAGFRAGS,new Integer(iAbsTotFrags).toString());
    htTags.put(TAGROWS,getString(vRows));
    htTags.put(TAGTOTALS,sTotals);
    bwOut.write(getString(swapTags(vTableMain,htTags)));

    bwOut.close();
  }

  //  Replaces all occurances of sReplace in sMain with sWith (like String.replaceAll but without regexp)
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  public static String replaceAll(String sMain,String sReplace,String sWith) {
  int iLen = sReplace.length(); // The length of the string to replace
  int iStart; // Start position of the string to replace
  int iEnd; // End possition of the string to replace
  String sReturn = new String(sMain); // The return string
    // Check if we have a string to replace
    if ((sMain.length() > sReplace.length()) && (sMain.indexOf(sReplace) > -1)) {
      // Clear the return string
      sReturn = new String();
      // Get the first occurance of the replace string
      iStart = sMain.indexOf(sReplace);
      // Loop through all the occurances
      while (iStart > -1) {
        // Get the end position
        iEnd = iStart + iLen - 1;
        // If we arent at the start then add the strings together, otherwise miss off the main bit
        if (iStart == 0) { sReturn = sReturn + sWith; } 
        else { sReturn = sReturn + sMain.substring(0,iStart) + sWith; }
        // Cut off what we just added
        sMain = sMain.substring(iEnd + 1);
        // Get the next occurance
        iStart = sMain.indexOf(sReplace);
      }
      // Add the last bit on to the return string
      sReturn = sReturn + sMain;
    } else if (sMain.equals(sReplace)) {
      // The main string only contains the replace string so just return the replace with string
      sReturn = sWith;
    }
    // Return
    return sReturn;
  }

  //  Performs a full parse of a log file
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public void startParse(String sLog,String sInput,String sDelete) throws IllegalArgumentException,FileNotFoundException,IOException {
  String[] aLog = sLog.split(PATHSEP);
  boolean bDelete = new Boolean(sDelete).booleanValue();
  File fLog;
    for (int iLoop = 0;iLoop < aLog.length;iLoop++) {
      fLog = new File(aLog[iLoop]);
      if ((fLog.exists()) && (fLog.isFile())) {
        addLog(aLog[iLoop],sInput,false,bDelete);
      } else {
        throw new IllegalArgumentException("File " + aLog[iLoop] + " doesn't exist");
      }
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
  String[] aInput = sInput.split(PATHSEP);
  File fInput;
    htKills = new Hashtable();
    if (checkType(sType)) {
      for (int iLoop = 0;iLoop < aInput.length;iLoop++) {
        fInput = new File(aInput[iLoop]);
        if ((fInput.exists()) && (fInput.isFile())) {
          if (getLog(aInput[iLoop],false)) { outputLog(sOutDir,sServer,sType); }
        } else {
          throw new IllegalArgumentException("File " + aInput[iLoop] + " doesn't exist");
        }
      }
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
  public void startBoth(String sLog,String sInput,String sOutDir,String sServer,String sType,String sDelete,String sForce) throws IllegalArgumentException,FileNotFoundException,IOException {
  String[] aLog = sLog.split(PATHSEP);
  boolean bDelete = new Boolean(sDelete).booleanValue();
  boolean bForce = new Boolean(sForce).booleanValue();
  File fLog;
  File fInput;
    htKills = new Hashtable();
    if (checkType(sType)) {
      for (int iLoop = 0;iLoop < aLog.length;iLoop++) {
        fLog = new File(aLog[iLoop]);
        if ((fLog.exists()) && (fLog.isFile())) {
          if ((addLog(aLog[iLoop],sInput,false,bDelete)) ||
              (bForce)) {
            getLog(sInput,false);
          }
        } else {
          throw new IllegalArgumentException("File " + aLog[iLoop] + " doesn't exist");
        }
      }
      outputLog(sOutDir,sServer,sType);
    } else {
      throw new IllegalArgumentException("Invalid Type - " + sType);
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
  boolean bFailed = false;
  int iLen = args.length;
  String sAction = new String();
  Q3Log myLog = new Q3Log();
    sHelp = "Q3Log v1.2 - Quake 3 Arena Stats Generator" + LINESEP + LINESEP +
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
    /*
      arg[0] = Action (parse,output,both)
      if (Action != help) { arg[1] = ConfigFile }
      if (Action == Parse) {
        arg[2] = LogName|LogName
        arg[3] = InputName
        arg[4] = Delete
      } else if (Action == Output) {
        arg[2] = InputName|InputName
        arg[3] = OutPutDir
        arg[4] = Server Name
        arg[5] = Server Type (standard,arena,hh3,ut)
      } else if (Action == Both) {
        arg[2] = LogName|LogName
        arg[3] = InputName
        arg[4] = OutPutDir
        arg[5] = Server Name
        arg[6] = Server Type (standard,arena,hh3,ut)
        arg[7] = Delete
        arg[8] = Force Output
      }
    */
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
            }
          } else {
            bFailed = true;
          }
        } else if (sAction.equalsIgnoreCase(ARGOUTPUT)) {
          if (iLen == 6) {
            if (myLog.readProperties(args[1])) {
              args[3] = myLog.checkDir(args[3]);
              myLog.startOutput(args[2],args[3],args[4],args[5]);
            } else {
              bFailed = true;
            }
          } else {
            bFailed = true;
          }
        } else if (sAction.equalsIgnoreCase(ARGBOTH)) {
          if (iLen == 9) {
            if (myLog.readProperties(args[1])) {
              args[4] = myLog.checkDir(args[4]);
              myLog.startBoth(args[2],args[3],args[4],args[5],args[6],args[7],args[8]);
            } else {
              sError = "Error: Config file not found" + LINESEP + sHelp;
              bFailed = true;
            }
          } else {
            bFailed = true;
          }
        } else {
          bFailed = true;
        }
      } catch (Exception e) {
        System.out.println("Exception - " + e.toString());
        e.printStackTrace();
      }
    } else {
      bFailed = true;
    }
    if (bFailed) { System.out.println(sError); }
  }
  
}