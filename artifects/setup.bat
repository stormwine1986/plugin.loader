@echo off

for /f "tokens=1,2 delims=:" %%a in ('reg query "HKEY_LOCAL_MACHINE\SOFTWARE\Microsoft\Windows\CurrentVersion\Uninstall\Windchill RV&S Client 12" /v "InstallLocation"') do (
	set "v1=%%a"
	set "v2=%%b"
)

set InstallLocation=%v1:~-1%:%v2%

echo %~dp0

echo Copy file to InstallLocation:%InstallLocation%\bin

copy %~dp0\loader.jar "%InstallLocation%\bin"

pause