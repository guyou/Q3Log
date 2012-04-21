import java.io.*;
import java.util.*;
import java.text.*;
import java.util.Date;

public class LogParser {

/*
  
  This class reads log files into memory, and outputs smaller versions for keeping
  
  You are free to distribute this file as long as you don't charge anything for it.  If you want to put it on
  a magazine CD/DVD then e-mail me at givememoney@wilf.co.uk and we can talk.
  You are also free to modify this file as much as you want, but if you distribute a modified copy please 
  include this at the top of the file and be aware it remains the property of me, so no selling modified 
  version please.

  Feel free to e-mail me with comments/suggestions/whatever at q3logger@wilf.co.uk
  
  Copyright Stuart Butcher (stu@wilf.co.uk) 2002
    
*/

private LogTools myTools = new LogTools(); // Create a LogTools object to use
public static final String WEAPONLINK = new String(LogTools.WEAPONLINK); // Linked weapon text
// The following contains all the different lines that are defined as System (feel free to add to it for other mods)
public static final int INDENT = 7; // Indent in Logfile (usually 7)
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
// The text preceding a chat line
public static final String[] TXTSAY = {
  "say",
  "say_world",
  "say_team"
};
public static final String TXTKILL = new String("Kill"); // The text preceding a frag line
public static final String TXTKILLED = new String("killed"); // The text seperating the killers name from the preys name
public static final String TXTGAME = new String("------------------------------------------------------------"); // New game line
public static final String BREAK = new String(":"); // Whats after the commands but before the text in the log file
public static final String SPACE = new String(" "); // Blank space text
public static final String EQUALS = new String("="); // Equals sign
// The following define the different line types in a logfile
public static final int NOTHING = 0; // We dont know what it is
public static final int SYSTEM = 1; // System (ignored)
public static final int CHAT = 2; // Say commands (ignored)
public static final int KILL = 3; // A frag
public static final int GAME = 4; // New game (ignored)
public static final String TXTSUICIDE = new String("<world>"); // Used to tell when a kill is actually a suicide (also if Killer = Killed)

public static final String TXTTOTALS = new String("TOTALS");
public static final String TXTOPONENTS = new String("OPONENTS");

private Hashtable htKills = new Hashtable(); // Holds all the kills in the logfile
private Hashtable htUsers = null; // Holds a list of users and their details
private Hashtable htFun = new Hashtable(); // Holds a list of users fun names

private LogTotals grandTotal; // The grand total of kills

private File fIn = null; // Input file
private File fOut = null; // Output file
private int iNextUserID = 1; // Holds the next UserId to use
private static final int SUICIDE = 0; // Suicides user id
private String[] aIgnore = null; // Ignore users
private String[] sWeaponsList;

  //  Can be called with the input and output files as paramters, or not
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public LogParser() { }
  public LogParser(String sLocWeaponsList[]) {
    sWeaponsList = sLocWeaponsList;
    grandTotal = new LogTotals(sWeaponsList);
  }
  public LogParser(String sLocInFile) throws IllegalArgumentException,FileNotFoundException {
    if (myTools.checkFile(sLocInFile)) { fIn = new File(sLocInFile); }
  }
  public LogParser(String sLocInFile,String sLocOutFile) throws IllegalArgumentException,FileNotFoundException {
    if (myTools.checkFile(sLocInFile)) { fIn = new File(sLocInFile); }
    if (myTools.checkString(sLocOutFile)) { fOut = new File(sLocOutFile); }
    else { throw new IllegalArgumentException("Output File Invalid"); }
  }

  //  Sets the input file (after checking it is valid)
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public void setInput(String sLocInFile) throws IllegalArgumentException,FileNotFoundException {
    if (myTools.checkFile(sLocInFile)) { fIn = new File(sLocInFile); }
  }

  //  Returns the current input file
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public String getInput() {
  String sReturn = new String();
    if (fIn != null) { sReturn = fIn.getPath(); }
    return sReturn;
  }

  //  Sets the output file (after checking it is valid)
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public void setOutput(String sLocOutFile) throws IllegalArgumentException {
    if (myTools.checkString(sLocOutFile)) { fOut = new File(sLocOutFile); }
    else { throw new IllegalArgumentException("Output File Invalid"); }
  }

  //  Returns the current output file
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public String getOutput() {
  String sReturn = new String();
    if (fOut != null) { sReturn = fOut.getPath(); }
    return sReturn;
  }

  //  Parses a log file and outputs a compressed version containing just the frag lines
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public Hashtable execute(boolean bRead) throws FileNotFoundException,IOException,IllegalArgumentException {
  BufferedReader brIn;
  BufferedWriter bwOut;
  String sLine = new String();
  int iType;
    if ((fIn != null) && (myTools.checkFile(fIn.getPath()))) { 
      if (fOut != null) {
        brIn = new BufferedReader(new FileReader(fIn.getPath()));
        sLine = brIn.readLine();
        if (sLine != null) {
          myTools.makeDirs(fOut.getParentFile());
          bwOut = new BufferedWriter(new FileWriter(fOut.getPath(),true));
          while (sLine != null) {
            if (!sLine.equals("")) {
              sLine = sLine.substring(INDENT);
              iType = getLineType(sLine);
              if (iType == SYSTEM) {
                // Do nothing
              } else if (iType == KILL) {
                // Got a kill
                if (bRead) { processKill(sLine); }
                bwOut.write(sLine + myTools.LINESEP);
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
            }
            sLine = brIn.readLine();
          }
          brIn.close();
          bwOut.close();
        } else {
          throw new IOException("Input file is empty");
        }
      } else {
        throw new IllegalArgumentException("Output file hasn't been set");
      }
    } else {
      throw new IllegalArgumentException("Input file hasn't been set");
    }
    return htKills;
  }
  public Hashtable execute(String sLocOutFile,String sLocInFile) throws FileNotFoundException,IOException,IllegalArgumentException {
    if (myTools.checkFile(sLocInFile)) { fIn = new File(sLocInFile); }
    if (myTools.checkString(sLocOutFile)) { fOut = new File(sLocOutFile); }
    else { throw new IllegalArgumentException("Output File Invalid"); }
    return execute(false);
  }
  public Hashtable execute(String sLocOutFile) throws FileNotFoundException,IOException,IllegalArgumentException {
    if (myTools.checkString(sLocOutFile)) { fOut = new File(sLocOutFile); }
    else { throw new IllegalArgumentException("Output File Invalid"); }
    return execute(false);
  }
  public Hashtable execute(String sLocOutFile,String sLocInFile,boolean bLocRead) throws FileNotFoundException,IOException,IllegalArgumentException {
    if (myTools.checkFile(sLocInFile)) { fIn = new File(sLocInFile); }
    if (myTools.checkString(sLocOutFile)) { fOut = new File(sLocOutFile); }
    else { throw new IllegalArgumentException("Output File Invalid"); }
    return execute(bLocRead);
  }
  public Hashtable execute(String sLocOutFile,boolean bLocRead) throws FileNotFoundException,IOException,IllegalArgumentException {
    if (myTools.checkString(sLocOutFile)) { fOut = new File(sLocOutFile); }
    else { throw new IllegalArgumentException("Output File Invalid"); }
    return execute(bLocRead);
  }
  public Hashtable execute() throws FileNotFoundException,IOException,IllegalArgumentException {
    return execute(false);
  }
  

  //  Reads in a compressed log file into the main kills hashtable
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public Hashtable readLog(String sInFile,boolean bNew) throws FileNotFoundException,IOException {
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
      throw new IOException("Input file is empty");
    }
    return htKills;
  }
  public Hashtable readLog(String sInFile,boolean bNew,String[] aLocIgnore) throws FileNotFoundException,IOException {
    setIgnore(aLocIgnore);
    return readLog(sInFile,bNew);
  }

  //  Sets the ignore list
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   24th September 2002
  //
  //
  public void setIgnore(String[] aLocIgnore) { aIgnore = aLocIgnore; }
  
  //  Returns the ignore list
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   24th September 2002
  //
  //
  public String[] getIgnore() { return aIgnore; }

  //  Returns the line type for the passed in line
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private int getLineType(String sLine) {
  int iReturn = NOTHING;
    if ((sLine.length() >= TXTKILL.length()) && (sLine.substring(0,TXTKILL.length()).equalsIgnoreCase(TXTKILL))) {
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
      if (iReturn == NOTHING) {
        for (int iLoop = 0;iLoop < TXTSAY.length;iLoop++) {
          if ((sLine.length() >= TXTSAY[iLoop].length()) && (sLine.substring(0,TXTSAY[iLoop].length()).equalsIgnoreCase(TXTSAY[iLoop]))) {
            iReturn = CHAT;
            break;
          }
        }
      }
    }      
    return iReturn;
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
      sUser = myTools.stripChars(sUser);
      if (htUsers == null) {
        htUsers = new Hashtable();
        htUsers.put(new Integer(iNextUserID),sUser);
        iReturn = 1;
        iNextUserID++;
        htUsers.put(new Integer(SUICIDE),TXTSUICIDE);
      } else {
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
  
  //  Returns the string representation of the user who killed someone in the passed in line
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   24th September 2002
  //
  //
  private String getKillerUser(String sLine) {
  String sReturn = new String();
  int iStart = 0;
  int iEnd = 0;
    iStart = sLine.indexOf(BREAK,TXTKILL.length() + BREAK.length() + 1) + 2;
    iEnd = sLine.indexOf(SPACE,iStart);
    sReturn = sLine.substring(iStart,iEnd);
    return sReturn;    
  }
  
  //  Returns the string representation of the user who got killed in the passed in line
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   24th September 2002
  //
  //
  private String getKilledUser(String sLine) {
  String sReturn = new String();
  int iStart = 0;
  int iEnd = 0;
    iStart = sLine.indexOf(TXTKILLED) + TXTKILLED.length() + 1;
    iEnd = sLine.indexOf(SPACE,iStart);
    sReturn = sLine.substring(iStart,iEnd);
    return sReturn;    
  }
  
  //  Return the grand total of kills
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   27th September 2002
  //
  //
  public LogTotals getGrandTotal() { return grandTotal; }
  
  //  Returns the User ID for the killer on the line passed in
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private int getKiller(String sLine) {
  String sUser = new String();
    return getUserID(getKillerUser(sLine));
  }
  
  //  Returns the User ID for the prey on the line passed in
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  private int getKilled(String sLine) { return getUserID(getKilledUser(sLine)); }
  
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
  private void addKill(int iKiller,String sKiller,int iKilled,String sKilled,int iWeapon) {
  Hashtable htPeople = new Hashtable();
  Hashtable htHold = new Hashtable();
  Integer IKiller = new Integer(iKiller);
  Integer IKilled = new Integer(iKilled);
  LogTotals myTotal = new LogTotals(sWeaponsList);
  LogTotals myOponent = new LogTotals(1,sWeaponsList);
  String sWeapon;
  
    sWeapon = sWeaponsList[iWeapon];
    if ((sWeapon.length() > WEAPONLINK.length()) && (sWeapon.substring(0,WEAPONLINK.length()).equals(WEAPONLINK))) { 
      iWeapon = new Integer(sWeapon.substring(WEAPONLINK.length())).intValue(); 
    }

    grandTotal.addFrag(1,iWeapon);

    if ((!sKiller.equals(sKilled)) && (!sKiller.equals(TXTSUICIDE))) {
      
      // Normal killing
      if (htKills.get(IKiller) != null) {
        htHold = (Hashtable)htKills.get(IKiller);
        if (htHold.get(TXTTOTALS) != null) { myTotal = (LogTotals)htHold.get(TXTTOTALS); }
        if (htHold.get(TXTOPONENTS) != null) { 
          htPeople = (Hashtable)htHold.get(TXTOPONENTS);
          if (htPeople.get(IKilled) != null) { myOponent = (LogTotals)htPeople.get(IKilled); }
        }
      }
      
      myOponent.addKill(1);
      myOponent.addUser(sKilled,true);
      myOponent.addFrag(1,iWeapon);
      htPeople.put(IKilled,myOponent);

      myTotal.addFrag(1,iWeapon);
      myTotal.addKill(1);
      myTotal.addUser(sKilled,true);
      htHold.put(TXTTOTALS,myTotal);
      htHold.put(TXTOPONENTS,htPeople);
      htKills.put(IKiller,htHold);
      
      htHold = new Hashtable();
      myTotal = new LogTotals(sWeaponsList);
      myOponent = new LogTotals(1,sWeaponsList);
      htPeople = new Hashtable();
      
      if (htKills.get(IKilled) != null) { 
        htHold = (Hashtable)htKills.get(IKilled);
        if (htHold.get(TXTTOTALS) != null) { myTotal = (LogTotals)htHold.get(TXTTOTALS); }
        if (htHold.get(TXTOPONENTS) != null) { 
          htPeople = (Hashtable)htHold.get(TXTOPONENTS); 
          if (htPeople.get(IKiller) != null) { myOponent = (LogTotals)htPeople.get(IKiller); }
        }
      }

      myOponent.addDeath(1);
      myOponent.addUser(sKiller,true);
      htPeople.put(IKiller,myOponent);

      myTotal.addDeath(1);
      myTotal.addUser(sKiller,true);
      htHold.put(TXTTOTALS,myTotal);
      htHold.put(TXTOPONENTS,htPeople);
      htKills.put(IKilled,htHold);
    
    } else {

      // Suicide
      htHold = new Hashtable();
      myTotal = new LogTotals(sWeaponsList);
      myOponent = new LogTotals(1,sWeaponsList);
      htPeople = new Hashtable();

      if (htKills.get(IKilled) != null) { 
        htHold = (Hashtable)htKills.get(IKilled);
        if (htHold.get(TXTTOTALS) != null) { myTotal = (LogTotals)htHold.get(TXTTOTALS); }
        if (htHold.get(TXTOPONENTS) != null) { htPeople = (Hashtable)htHold.get(TXTOPONENTS); }
      }

      myTotal.addSuicide(1);
      htHold.put(TXTTOTALS,myTotal);
      htHold.put(TXTOPONENTS,htPeople);
      htKills.put(IKilled,htHold);
    
    }
    
    htFun.put(myTools.stripChars(sKiller),sKiller);
    htFun.put(myTools.stripChars(sKilled),sKilled);
    
  }
  
  //  Checks if a line from the input contains a user on the ignore list
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   24th September 2002
  //
  //
  private boolean checkIgnore(String sLine) {
  boolean bReturn = true;
  String sKiller;
  String sKilled;
  String sIgnore;
    if ((aIgnore != null) && (aIgnore.length > 0)) {
      sKiller = myTools.stripChars(getKillerUser(sLine));
      sKilled = myTools.stripChars(getKilledUser(sLine));
      for (int iLoop = 0;iLoop < aIgnore.length;iLoop++) {
        sIgnore = myTools.stripChars(aIgnore[iLoop]);
        if ((sIgnore.equalsIgnoreCase(sKiller)) || (sIgnore.equalsIgnoreCase(sKilled))) {
          bReturn = false;
          break;
        }
      }
    }
    return bReturn;
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
  String sKiller;
  String sKilled;
    if (checkIgnore(sLine)) {
      iKiller = getKiller(sLine);
      iKilled = getKilled(sLine);
      iWeapon = getWeapon(sLine);
      sKiller = getKillerUser(sLine);
      sKilled = getKilledUser(sLine);
      addKill(iKiller,sKiller,iKilled,sKilled,iWeapon);
    }
  }
  
  //  Returns the users hashtable
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public Hashtable getUsers() { return htUsers; }

  //  Returns the users funname hashtable
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   24th September 2002
  //
  //
  public Hashtable getFun() { return htFun; }

  //  Returns the kills hashtable
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public Hashtable getKills() { return htKills; }

}
