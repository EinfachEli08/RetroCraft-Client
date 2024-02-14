@echo off
echo Installing RetroCraft to AppData.minecraft\versions\RetroCraft...

set destinationDir=%APPDATA%\.minecraft\versions\RetroCraft

echo Create the destination directory if it doesnt exist
if not exist "%destinationDir%" mkdir "%destinationDir%"

if exist "%destinationDir%\RetroCraft.jar" (
    del "%destinationDir%\RetroCraft.jar"
)
if exist "%destinationDir%\RetroCraft.json" (
    del "%destinationDir%\RetroCraft.json"
)

copy .\RetroCraft\RetroCraft.jar "%destinationDir%"
copy .\RetroCraft\RetroCraft.json "%destinationDir%"

echo Installed successfully