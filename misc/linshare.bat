@echo off
if "%OS%" == "Windows_NT" setlocal
set CURRENT_DIR=%cd%
set JAVA_OPTIONS=-Xmx512m -Xms256m -Dfile.encoding=UTF-8 -Dorg.mortbay.jetty.webapp.parentLoaderPriority=true -DLINSHARE_HOME="%CURRENT_DIR%"


set _EXECJAVA=start java

cd "%CURRENT_DIR%\jetty"
%_EXECJAVA%  %JAVA_OPTIONS%  -jar start.jar 
