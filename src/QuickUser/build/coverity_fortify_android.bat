@echo off

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_131
set GRADLE_HOME=C:\Users\Administrator\.gradle\wrapper\dists\gradle-3.5.1-all\42vjebfdws9pjts3l4bouoq0p\gradle-3.5.1
set CI_ROOT=D:\windows_buildcloud-agent
set FORTIFY_HOME=%CI_ROOT%\plugins\CodeDEX\tool\fortify
set COVERITY_HOME=%CI_ROOT%\plugins\CodeDEX\tool\coverity
set KLOCWORK_HOME=%CI_ROOT%\plugins\CodeDEX\tool\klocwork
set PATH=%FORTIFY_HOME%\bin;%COVERITY_HOME%\bin;%JAVA_HOME%\bin;%GRADLE_HOME%\bin;%PATH%

set codedex_tool=%CI_ROOT%\plugins\CodeDEX\tool

set inter_dir=D:\codeDEX_output
set cov_tmp_dir=%inter_dir%\cov_tmp
set for_tmp_dir=%inter_dir%\for_tmp

set project_root=D:\build\QuickUesr_Android\code\current\Server\QuickUser

set FORTIFY_BUILD_ID=quickuser

rmdir /q /s %inter_dir%

cd %project_root%\app

call cov-build --dir "%cov_tmp_dir%" gradle clean --no-daemon build

cd /d %inter_dir%

call java -jar %codedex_tool%\transferfortify-1.3.1.jar "java" "%FORTIFY_BUILD_ID%" "%inter_dir%" 

cd /d %cov_tmp_dir%
%codedex_tool%\7za.exe a -tzip coverity.zip * -r
xcopy coverity.zip "%inter_dir%" /S /Q /Y /H /R /I

cd /d %for_tmp_dir%
%codedex_tool%\7za.exe a -tzip fortify.zip * -r
xcopy fortify.zip "%inter_dir%" /S /Q /Y /H /R /I
