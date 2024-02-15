@echo off
echo MCP-REBORN ASSET INJECTOR
cd build\libs

echo Opening jar
ren RetroCraft*.jar game.zip

echo injecting assets...
..\..\7z.exe a game.zip ..\..\src\main\java\assets\ > log.txt

echo injecting data...
..\..\7z.exe a game.zip ..\..\src\main\java\data\ > log.txt
del log.txt

echo Closing jar
ren game.zip RetroCraft.jar

echo Moving into the RetroCraft Folder

if exist "..\..\RetroCraft\RetroCraft.jar" (
    del "..\..\RetroCraft\RetroCraft.jar"
)

move RetroCraft.jar ..\..\RetroCraft
cd ..\..\
