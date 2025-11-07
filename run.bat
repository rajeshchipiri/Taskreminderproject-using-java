@echo off
rem Compile project into out\ directory (works with Windows cmd by using a sources list)
cd /d "%~dp0"
if not exist out mkdir out
echo Compiling Java sources...
rem Compile known source directories (avoids recursive glob problems on Windows cmd)
javac -d out src\com\taskreminder\*.java src\com\taskreminder\model\*.java src\com\taskreminder\service\*.java
if errorlevel 1 (
  echo Compilation failed.
  del /q sources.txt >nul 2>&1
  exit /b 1
)
del /q sources.txt >nul 2>&1
echo Compilation successful.
echo Run: java -cp out com.taskreminder.Main
