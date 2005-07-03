@echo off
rem ---------------------------------------------------------------------------
rem Start script for running the Amazon Queuing service
rem
rem ---------------------------------------------------------------------------

rem store the current directory
set CURRENT_DIR=%cd%

rem check the AXIS_HOME environment variable
if not "%AXIS_HOME%" == "" goto gotHome

rem guess the home. Jump two directories up nad take that as the home
cd ..
cd ..
set AXIS_HOME=%cd%

:gotHome
if EXIST "%AXIS_HOME%\lib\axis2-0.9.jar" goto okHome
echo The AXIS_HOME environment variable seems not to point to the correct location!
echo This environment variable is needed to run this program
pause
exit

:okHome
cd %CURRENT_DIR%
rem get the classes for the simple axis server
set AXIS2_CLASS_PATH="%AXIS_HOME%"
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%"
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\axis2-0.9.jar"
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\axis-wsdl4j-1.2.jar"
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\commons-logging-1.0.3.jar"
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\log4j-1.2.8.jar"
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\stax-1.1.1-dev.jar"
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\stax-api-1.0.jar"
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\geronimo-spec-activation-1.0.2-rc3.jar
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\axis-wsdl4j-1.2.jar        
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\geronimo-spec-javamail-1.3.1-rc3.jar
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\xbean-2.0.0-beta1.jar
set AXIS2_CLASS_PATH="%CLASS_PATH";"%AXIS_HOME%\lib\stax-api-1.0.jar

set AXIS2_CLASS_PATH=%AXIS2_CLASS_PATH%;"%CURRENT_DIR%\amazonQS.jar"
start javaw -cp %AXIS2_CLASS_PATH% sample.amazon.amazonSimpleQueueService.RunGUICQ
start javaw -cp %AXIS2_CLASS_PATH% sample.amazon.amazonSimpleQueueService.RunGUIRQ
:end