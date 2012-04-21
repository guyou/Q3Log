import java.io.*;
import java.util.*;
import java.text.*;

public class LogTotals {

/*

  This class is built to hold user totals for ease of use.  It can work out the average efficiency, rank and 
  kill ratio.
  
  You are free to distribute this file as long as you don't charge anything for it.  If you want to put it on
  a magazine CD/DVD then e-mail me at givememoney@wilf.co.uk and we can talk.
  You are also free to modify this file as much as you want, but if you distribute a modified copy please 
  include this at the top of the file and be aware it remains the property of me, so no selling modified 
  version please.

  Feel free to e-mail me with comments/suggestions/whatever at q3logger@wilf.co.uk
  
  Copyright Stuart Butcher (stu@wilf.co.uk) 2002
    
*/

// The following int's are positions in arrays for certain values to go
public static final int KILLS = 0;
public static final int DEATHS = 1;
public static final int EFFICIENCY = 2;
public static final int SUICIDES = 3;
public static final int RANK = 4;
public static final int RATIO = 5;

private LogTools myTools = new LogTools(); // Create a LogTools object to use

// The following are the running totals that are held
private float fFrags = 0;
private float fKills = 0;
private float fSuicides = 0;
private float fDeaths = 0;
private float fUsers = 0;
private float fOtherUsers = 0;
private float fEfficiency = 0;
private float fKillRatio = 0;
private float fRank = 0;
  
  //  Can be called with the initial number of users (used for individual user totals)
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public LogTotals(int iUsers) { fUsers = new Float(iUsers).floatValue(); }
  public LogTotals() { }
  
  //  Adds a number of frags into the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public void addFrag(int iNum) { fFrags += iNum; }
  
  //  Adds a number of kills into the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public void addKill(int iNum) { fKills += iNum; }
  
  //  Adds a number of suicides into the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public void addSuicide(int iNum) { fSuicides += iNum; }
  
  //  Adds a number of deaths into the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public void addDeath(int iNum) { fDeaths += iNum; }
  
  //  Adds a user to the totals (either a valid one for the totals or one that doesnt count)
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public void addUser(boolean bLive) { 
    if (bLive) { fUsers++; }
    else { fOtherUsers++; }
  }
 
  //  Returns the number of frags from the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public int getFrags() { return new Float(fFrags).intValue(); }
  
  //  Returns the average number of kills from the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public int getKills() { return new Float(fKills / fUsers).intValue(); }
  
  //  Returns the total number of kills from the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public int getActualKills() { return new Float(fKills).intValue(); }
  
  //  Returns the average number of suicides from the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public int getSuicides() { return new Float(fSuicides / fUsers).intValue(); }

  //  Returns the average number of deaths from the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public int getDeaths() { return new Float(fDeaths / fUsers).intValue(); }
  
  //  Returns the number of users from the totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public int getUsers() { return new Float(fUsers).intValue(); }
  
  //  Returns the total number of invalid and valid users from the totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public int getTotalUsers() { return new Float(fUsers + fOtherUsers).intValue(); }
  
  //  Returns the average efficiency from the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public String getEfficiency() { return new Integer(efficiency(fKills,fDeaths,fSuicides).intValue()).toString(); }

  //  Returns the average kill ratio from the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public String getKillRatio() { return myTools.decimalPlaces(killratio(fKills,fDeaths,fSuicides).toString()); }

  //  Returns the average rank from the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public String getRank() { return new Integer(rank(fKills,fDeaths,fSuicides).intValue()).toString(); }
  
  //  Returns the requested average from the totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public float getNum(int iNum) {
  float fReturn = 0;
    if (iNum == KILLS) { fReturn = fKills / fUsers; }
    else if (iNum == DEATHS) { fReturn = fDeaths / fUsers; }
    else if (iNum == SUICIDES) { fReturn = fSuicides / fUsers; }
    else if (iNum == RANK) { fReturn = rank(fKills,fDeaths,fSuicides).floatValue(); }
    else if (iNum == RATIO) { fReturn = killratio(fKills,fDeaths,fSuicides).floatValue(); }
    else if (iNum == EFFICIENCY) { fReturn = efficiency(fKills,fDeaths,fSuicides).floatValue(); }
    return fReturn;
  }

  //  Adds the requested type of frag to the user totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public void addNum(int iNum,int iAdd) {
    if (iNum == KILLS) { fKills += iAdd; }
    else if (iNum == DEATHS) { fDeaths += iAdd; }
    else if (iNum == SUICIDES) { fSuicides += iAdd; }
  }
  
  //  Returns the average efficiency from the passed in totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  private static Float efficiency(float fKills,float fDeaths,float fSuicides) { return new Float((100 / (fKills + fDeaths)) * (fKills - (fSuicides * 2))); }

  //  Returns the average kill ratio from the passed in totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  private static Float killratio(float fKills,float fDeaths,float fSuicides) { return new Float(fKills / fDeaths); }

  //  Returns the average rank from the passed in totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  private static Float rank(float fKills,float fDeaths,float fSuicides) { return new Float(fKills - fDeaths - fSuicides); }
  
  //  Returns the average efficiency from the passed in totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public static int workEfficiency(int iKills,int iDeaths,int iSuicides) {
  float fKills = new Float(iKills).floatValue();
  float fDeaths = new Float(iDeaths).floatValue();
  float fSuicides = new Float(iSuicides).floatValue();
    return efficiency(fKills,fDeaths,fSuicides).intValue();
  }

  //  Returns the average rank from the passed in totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public static int workRank(int iKills,int iDeaths,int iSuicides) {
  float fKills = new Float(iKills).floatValue();
  float fDeaths = new Float(iDeaths).floatValue();
  float fSuicides = new Float(iSuicides).floatValue();
    return rank(fKills,fDeaths,fSuicides).intValue();
  }

  //  Returns the average kill ratio from the passed in totals
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public static int workRatio(int iKills,int iDeaths,int iSuicides) {
  float fKills = new Float(iKills).floatValue();
  float fDeaths = new Float(iDeaths).floatValue();
  float fSuicides = new Float(iSuicides).floatValue();
    return killratio(fKills,fDeaths,fSuicides).intValue();
  }
  
}