#!/bin/bash
# Run the Mintz Construction Estimator
# java.exe is a Windows program, so classpath must be Windows-style paths
if [ -d "/mnt/c/Users" ]; then
    BASE="/mnt/c"
elif [ -d "/c/Users" ]; then
    BASE="/c"
else
    echo "Error: Cannot find Windows C: drive mount"
    exit 1
fi

JAVA_EXE="$BASE/Users/Sam/.vscode/extensions/redhat.java-1.53.0-win32-x64/jre/21.0.10-win32-x86_64/bin/java.exe"
CLASSPATH='C:\Users\Sam\CoWork\Bro-Code-Java-main\SquareFeetCalculator\bin;C:\Users\Sam\CoWork\Bro-Code-Java-main\SquareFeetCalculator\lib\postgresql-42.7.3.jar'

exec "$JAVA_EXE" -cp "$CLASSPATH" Main
