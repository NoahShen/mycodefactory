set CURRENT=%cd%
call build_proj.bat
cd %CURRENT%
python packing_changed_files.py
fab -f deploy_server.py deploy
pause