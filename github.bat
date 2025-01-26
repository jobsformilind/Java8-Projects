@echo off 

ECHO ---Pull-----------------------------
git pull
ECHO. 

ECHO ----Status--------------------------
git status
ECHO.

ECHO ---Add------------------------------
git add -A -v
ECHO. 

ECHO ---Commit/Push----------------------
git commit -m "Commiting changes"
git push
ECHO. 

ECHO ----Status--------------------------
git status
ECHO. 
pause