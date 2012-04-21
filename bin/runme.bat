@echo off
echo This will try to parse the log files, hold on to your hats...
echo Parsing...

echo MyServer Rocket Arena
java Q3Log both C:\Q3Log\bin\Q3Log.conf C:\Q3Log\logs\myserver\rocketarena\games.log C:\Q3Log\input\myserver_arena.dat E:\Q3Log\output\ myserver arena true false

echo ...Finished