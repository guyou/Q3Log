echo This will try to parse the log files, hold on to your hats...
echo Parsing...

cd bin

echo MyServer Rocket Arena
java Q3Log parse ../config/default.conf ../logs/myserver/rocketarena/games.log ../input/myserver_arena.dat true

echo ...Finished

cd ..