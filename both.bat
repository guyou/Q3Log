@echo off
echo This will try to parse the log files, hold on to your hats...
echo Parsing...

cd bin

echo MyServer Rocket Arena
java Q3Log both ..\config\default.conf ..\logs\myserver\rocketarena\games.log ..\input\myserver_arena.dat ..\output\ myserver arena true false

echo ...Finished

cd ..