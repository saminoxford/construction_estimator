@echo off
set JAVA_EXE=%USERPROFILE%\.vscode\extensions\redhat.java-1.53.0-win32-x64\jre\21.0.10-win32-x86_64\bin\java.exe
set CP=%~dp0bin;%~dp0lib\postgresql-42.7.3.jar
"%JAVA_EXE%" -cp "%CP%" Main
pause
