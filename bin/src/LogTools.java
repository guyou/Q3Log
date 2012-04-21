import java.io.*;
import java.util.*;
import java.text.*;
import java.util.Date;

public class LogTools {

/*

  This class contains 'handy' functions used by the other Q3Log classes.
  
  You are free to distribute this file as long as you don't charge anything for it.  If you want to put it on
  a magazine CD/DVD then e-mail me at givememoney@wilf.co.uk and we can talk.
  You are also free to modify this file as much as you want, but if you distribute a modified copy please 
  include this at the top of the file and be aware it remains the property of me, so no selling modified 
  version please.

  Feel free to e-mail me with comments/suggestions/whatever at q3logger@wilf.co.uk
  
  Copyright Stuart Butcher (stu@wilf.co.uk) 2002
    
*/

// The following Strings are globally used variables defined once here
public static final String LINESEP = System.getProperty("line.separator"); // The line seperator for this system
public static final String DIRSEP = System.getProperty("file.separator"); // The directory seperator for this system
  
  //  Returns true if the passed file is valid, false if it isnt
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public boolean checkFile(String sFile) throws IllegalArgumentException,FileNotFoundException {
  File fCheck;
  boolean bReturn = false;
    if ((sFile != null) && (!sFile.equals(""))) {
      fCheck = new File(sFile);
      if (fCheck.exists()) { bReturn = true; }
      else { throw new FileNotFoundException("File " + sFile + " not found."); }
    } else {
      throw new IllegalArgumentException("Input File Invalid");
    }
    return bReturn;
  }

  //  Returns true if the passed string is not null or empty, otherwise returns false
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public boolean checkString(String sString) {
  boolean bReturn = false;
    if ((sString != null) && (!sString.equals(""))) { bReturn = true; }
    return bReturn;
  }
   

  //  Returns the passed string with a "/" or "\" at the end if it doesnt have one
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public String checkDir(String sDir) {
  String sEnd = new Character(sDir.charAt(sDir.length() - 1)).toString();
  String sReturn = new String(sDir);
    if (!sEnd.equals(DIRSEP)) { sReturn = sReturn + DIRSEP; }
    return sReturn;
  }

  //  Returns the passed in string minus special characters (denoted by '^##')
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public String stripChars(String sUser,String[] aColours) {
  String sRep;
  String sAfter = new String();
  String sBefore = new String("<FONT COLOR=\"");
  String sMiddle = new String("\">");
    sUser = replaceAll(sUser,"<","(");
    sUser = replaceAll(sUser,">",")");
    sUser = replaceAll(sUser,"*","_");
    sUser = replaceAll(sUser,"?","_");
    sUser = replaceAll(sUser,"|","_");
    sUser = replaceAll(sUser,":","_");
    sUser = replaceAll(sUser,"\\","_");
    sUser = replaceAll(sUser,"/","_");
    sUser = replaceAll(sUser,"\"","_");
    for (int iLoop = 0;iLoop < 10;iLoop++) {
      if ((aColours != null) && (aColours.length > iLoop)) { sRep = sBefore + aColours[iLoop] + sMiddle;sAfter = "</FONT>"; }
      else { sRep = new String(); }
      sUser = replaceAll(sUser,"^" + iLoop,sRep);
    }
    return sUser + sAfter;
  }
  public String stripChars(String sUser) { return stripChars(sUser,new String[0]); }
  
  //  Makes sure the directory passed exists
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public boolean makeDirs(File fFile) {
    return fFile.mkdirs();
  }
  public boolean makeDirs(String sFile) { return makeDirs(new File(sFile)); }

  //  Makes sure that the passed in string is in the format '####.##' or similar, and passes it back
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   12th September 2002
  //
  //
  public String decimalPlaces(String sValue) {
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
  
  //  Returns a vector of the lines of the passed in file
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  public Vector readFile(File fIn) throws IllegalArgumentException,FileNotFoundException,IOException {
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

  //  Returns a string representation of the passed in vector
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   13th September 2002
  //
  //
  public String getString(Vector vIn) {
  String sReturn = new String();
  String sLine = new String();
  Object objHold;
    for (Iterator itLocal = vIn.iterator(); itLocal.hasNext();) {
      sReturn = sReturn + LINESEP + (String)itLocal.next();
    }
    return sReturn;
  }

  //  Replaces all occurances of sReplace in sMain with sWith (like String.replaceAll but without regexp)
  //  This is not just here to make this program work with Java 2 1.2.2 or later.  It is used when regexp
  //  is an annoyance.
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

  //  Returns a vector of keys that link to the hashtable passed in but sorted
  //
  //  Written by    :   Stuart Butcher
  //
  //  Date          :   22nd September 2002
  //
  //
  public Vector sortHash(Hashtable htIn,int iSort,boolean bForward) {
  Float[] fSort;
  Float[] fValue = new Float[htIn.size()];
  Hashtable htHold = new Hashtable();
  Vector vReturn = new Vector();
  Float fHold = new Float(0);
  String sHold = new String();
  String sOld = new String();
  String sValue = new String();
  String sUser = new String();
  String[] aUser;
  int iCount = 0;int iNumber = 0;
    for (Enumeration eSort = htIn.keys();eSort.hasMoreElements();) {
      sUser = (String)eSort.nextElement();
      fSort = (Float[])htIn.get(sUser);
      fHold = fSort[iSort];
      fValue[iCount] = fHold;
      sValue = fHold.toString();
      sHold = (String)htHold.get(sValue);
      if (htHold.get(sValue) != null) { sUser = sHold + "*" + sUser; }
      htHold.put(sValue,sUser);
      iCount++;
    }
    if (bForward) { Arrays.sort(fValue); }
    else { Arrays.sort(fValue,Collections.reverseOrder()); }
    for (int iLoop = 0;iLoop < fValue.length;iLoop++) {
      fHold = fValue[iLoop];
      sUser = (String)htHold.get(fHold.toString());
      if (!sOld.equals(sUser)) { iNumber = 0; }
      sOld = sUser;
      if (sUser.indexOf("*") > -1) {
        aUser = split(sUser,"*");
        sUser = aUser[iNumber];
        iNumber++;
      } else {
        iNumber = 0;
      }
      fSort = (Float[])htIn.get(sUser);
      vReturn.add(sUser);
    }
    return vReturn;
  }

  // Function to return an array from a string broken up by a splitting string
  //  This is only here to make this program work with Java 2 1.2.2 and later
  //  If you have Java 2 1.4 or later and want to use the String.split function,
  //  feel free
  //
  // Written by       :   Stuart Butcher
  //
  // Date             :   4th August 2002
  //
  // Written for      :   GSK
  //
  public static String[] split(String sCheck,String sChar) {
  String[] aReturn;
  ArrayList lReturn = new ArrayList();
  int iPos,iPos2;
    // Make sure we have a list
    iPos = sCheck.indexOf(sChar);
    if (iPos > -1) {  
      // We have a list so start adding terms to it
      lReturn.add(sCheck.substring(0,iPos));
      while (iPos > -1) { // Loop through all occurances
        // Get the end of the term
        iPos2 = sCheck.indexOf(sChar,iPos + 1);
        // If we are at the end, then set ourselves to the last position
        if (iPos2 == -1) { iPos2 = sCheck.length(); }
        // Add the term
        lReturn.add(sCheck.substring(iPos + 1,iPos2));
        // Get the next occurance
        iPos = sCheck.indexOf(sChar,iPos + 1);
      }
    } else { 
      // We dont have a list so return just sCheck
      lReturn.add(sCheck); 
    } 
    aReturn = new String[lReturn.size()];
    for (int iLoop = 0;iLoop < lReturn.size();iLoop++) {
      aReturn[iLoop] = (String)lReturn.get(iLoop);
    }
    // Return
    return aReturn;
  }

}
