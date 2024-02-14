@echo off
echo MCP-REBORN ASSET INJECTOR
cd build\libs
echo rename zip
ren RetroCraft*.jar game.zip
echo injecting assets...
..\..\7z.exe a game.zip ..\..\src\main\java\assets\ > log.txt
echo injecting data...
..\..\7z.exe a game.zip ..\..\src\main\java\data\ > log.txt
rem log.txt
echo rename jar
ren game.zip RetroCraft.jar
cd ..\..\