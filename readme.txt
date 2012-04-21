Q3Log v2.1 ReadMe
=================

(Copyright Stuart Butcher <stu@wilf.co.uk> 2002)

Contents:
=========

1.  Introduction
2.  New Features
3.  Installation
4.  Usage
5.  Valid Template Tags
6.  Future Enhancements/Bugs
7.  Distribution
8.  Feedback

------------------------------------------------------------------------------

1.  Introduction
================

This is a log file parser/stats generator for Quake 3 Arena log files.  It takes standard Quake 3 Arena log files and generates cut down versions which it stores and adds to with any new log files.  These cut down log files are then used to generate stats pages in HTML format.

It will also generate stats for Rocket Arena 3, Urban Terror and Headhunters 3.

It is written entirely in Java, and should work with Java 2 (1.2.2) and higher.  A full Runtime Environment is needed to use this program, but it should work under any OS there is a Runtime Environment for.

2.  New Features
================

  - New 'Ignore' setting added to conf file to allow certain users to be ignored in the output
  - New 'Colours' setting added to conf file to allow output of 'fun' user name colours
  - Changed some code so that Q3Log 2.1 will now work with Java 2 1.2.2 or later

3.  Installation
================

Simply unzip the install file anywhere on your machine.  You should have the following directories:

  bin       - contains the java files and the main config file
  input     - should be used to hold the cut down log files
  logs      - should be used to hold the standard Quake 3 Arena log files
  output    - should be used to store the output stats files
  static    - holds the static HTML files that wrap around the stats files
  templates - holds the template files used for the output

You should now setup the config file (Q3Log.conf usually) to contain the values you require (the file is commented)

4.  Usage
=========

Drop your log files (games.log usually) into the logs directory and then run the program.  do:

  java Q3Log help

for help on the parameters you can pass in and what they mean.

Once run, you should have all the HTML files you need containing the stats.

If you want to customise the output you will need to edit the following files (in the templates folder):

  table_main.htm_           - the main page containing summary stats info
  table_row_above.htm_      - each row of the summary stats table for people above the average
  table_row_below.htm_      - each row of the summary stats table for people below the average
  table_row_total.htm_      - the totals row of the summary stats table
  indiv_main.htm_           - the main page holding detailed information for each user
  indiv_oponents_above.htm_ - each row of the individual details page table for oponents above the average
  indiv_oponents_below.htm_ - each row of the individual details page table for oponents below the average
  indiv_oponents_total.htm_ - the totals row of the individual details page table
  indiv_weapons.htm_        - the details of weapons used for each individual user
  indiv_weapons_total.htm_  - the totals for the weapons used for each individual user

5.  Valid Template Tags
=======================

Tags have different values in different pages.  The following lists all possible combinations:

  table_main.htm_ -
  
                    Value Tags
                    ----------
                    Title       = The title of the page
                    Frags       = Total number of frags on the server
                    Users       = Total number of users on the server
                    Up          = The image for the up arrow
                    Down        = The image for the down arrow
                    Close       = Link closing tag (if link is valid)
                    
                    Pass Tags
                    ---------
                    LinkUp      = The href of this page sorted by the 
                                  passed in column upwards
                    LinkDown    = The href of this page sorted by the 
                                  passed in column downwards
                    
                    Section Tags
                    ------------
                    Above       = table_row_above.htm_ repeated once for 
                                  each user above average
                    Below       = table_row_below.htm_ repeated once for 
                                  each user below average
                    Totals      = The averages for the main table 
                                  (table_row_total.htm_)

  table_row_above.htm_ & table_row_below.htm_ -
  
                    Value Tags
                    ----------
                    Member      = This users table place
                    User_Link   = The HREF to the individual user page for 
                                  this user
                    User        = The username for this user
                    Kills       = The total number of kills for this user
                    Deaths      = The total number of deaths for this user
                    Suicides    = The total number of suicides for this user
                    KillRatio   = The kill ratio for this user
                    Rank        = The rank for this user
                    Efficiency  = The efficiency for this user

  table_row_total.htm_ - 

                    Value Tags
                    ----------
                    Kills       = The average number of kills for this server
                    Deaths      = The average number of deaths for this server
                    Suicides    = The average number of suicides for this server
                    KillRatio   = The average kill ratio for this server
                    Rank        = The average rank for this server
                    Efficiency  = The average efficiency for this server

  indiv_main.htm_ -
                    
                    Value Tags
                    ----------
                    User        = The username for this user
                    Up          = The image for the up arrow
                    Down        = The image for the down arrow
                    Close       = Link closing tag (if link is valid)
                    
                    
                    Pass Tags
                    ---------
                    LinkUp      = The href of this page sorted by the passed 
                                  in column upwards
                    LinkDown    = The href of this page sorted by the passed 
                                  in column downwards
                    
                    Section Tags
                    ------------
                    Above       = indiv_oponents_above.htm_ repeated once for 
                                  each oponent above average
                    Below       = indiv_oponents_below.htm_ repeated once for 
                                  each oponent below average
                    Totals      = The averages for this user 
                                  (indiv_oponents_total.htm_)

  indiv_oponents_above.htm_ & indiv_oponents_below.htm_ -
                    
                    Value Tags
                    ----------
                    Member      = This oponents table place
                    Oponent     = The opononts username
                    Kills       = The total number of kills against this oponent
                    Deaths      = The total number of deaths against this oponent
                    Efficiency  = The efficiency against this oponent
                    
                    Section Tags
                    ------------
                    Weapons     = indiv_weapons.htm_ repeated once for each type 
                                  of weapon used to kill this oponent

  indiv_oponents_total.htm_ -
                    
                    Value Tags
                    ----------
                    Kills       = The average number of kills for this user
                    Deaths      = The average number of deaths for this user
                    Efficiency  = The average efficiency for this user                                                              
                    Section Tags
                    ------------
                    Weapons     = indiv_weapons_total.htm_ repeated once for each 
                                  type of weapon used by this user

  indiv_weapons.htm_ -
  
                    Value Tags
                    ----------
                    Weapon      = The name of this weapon
                    Kills       = Total number of kills with this weapon against 
                                  this oponent
  
  indiv_weapons_total.htm_ -
  
                    Value Tags
                    ----------
                    Weapon      = The name of this weapon
                    Kills       = Total number of kills with this weapon for this 
                                  user
  
  All Pages -
                    System Tages
                    ------------
                    Date        = the date the output files were generated
                    Time        = the time the output files were generated
                    ProgName    = the name of the program used to generate the log 
                                  files (Q3Log)
                    ProgVer     = the version of the program used to generate the log 
                                  files
                    ProgDesc    = short description of the program used to generate the 
                                  log files
                    ProgFull    = the 3 above parameters put into a meaningfull string
                    ProgURL     = the homepage of the program used to generate the log 
                                  files

5.  Future Enhancements
=======================

No future enhancements currently planned.
 
If you have any suggestions or requests e-mail me at q3logger@wilf.co.uk.

I don't know of any bugs with the program as it stands.  If you find any please e-mail me a full bug report to q3logger@wilf.co.uk

6.  Distribution
================

Generally this program is under GPL licence terms.  Rough details are:

You are free to distribute this program as you wish, provided you don't charge for it and you include this readme file with the program unchanged (unless you make changes to the program that need to be reflected in here) *AND* you include the full source for the program.  If you want to put this program on a magazine CD/DVD/Whatever then please contact me at givememoney@wilf.co.uk and we can talk about it.

If you want to modify this program, feel free!  I have included the .java file to make it easier.  The comments are a bit sketchy, and the coding is very poor (I havent got time to look over it and make it anywhere near good), so good luck.  You can distribute your changed program as long as you don't charge for it, and provided that you include my name and e-mail address and state I wrote the original that you improved on.  Also, you should only distribute any versions of this program if they have a readme or similar file that states that it shouldn't be distributed for money.

7.  Feedback
============

If you use Q3Log to produce public stats, please e-mail me a link to the server.  I wont publish its location or any details about it if you dont want me to, its just always nice to see other people using Q3Log.

If you have any feedback or questions (and have read this file and the help generated by the program first) then please contact me at q3logger@wilf.co.uk

I play under the nickname Wilf, but don't usually go on big public servers.  I run my own home server now and then (Rocket Arena 3 mostly).  The IP address changes occasionally (that's Pipex ADSL for you), but at the moment its 81.86.197.48 (standard port), and its usually on Gamespy etc as Billy RA Server.

I also use the following IM clients:
  ICQ - 7625349
  MSN - oroblramuk@hotmail.com

My own servers stats page is:
  http://www.wilf.co.uk/

My homepage is:
  http://www.oroblram.co.uk/
  


Quake 3 Arena is owned by ID Software.  Q3Log is owned by Stuart Butcher (stu@wilf.co.uk).