@ECHO OFF
SETLOCAL

IF NOT "%MAVEN_HOME%"=="" IF EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  CALL "%MAVEN_HOME%\bin\mvn.cmd" %*
  EXIT /B %ERRORLEVEL%
)

SET LOCAL_MAVEN=C:\apache-maven\bin\mvn.cmd
IF EXIST "%LOCAL_MAVEN%" (
  CALL "%LOCAL_MAVEN%" %*
  EXIT /B %ERRORLEVEL%
)

CALL mvn %*
