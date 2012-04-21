@echo off
echo This will try to parse the log files, hold on to your hats...
echo Parsing...

cd bin

echo MyServer Rocket Arena
java Q3Log output ..\config\default.conf ..\input\myserver_arena.dat ..\output\ myserver arena

echo ...Finished

cd ..