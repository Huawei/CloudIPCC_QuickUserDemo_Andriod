set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_131

mkdir ..\app\libs\armeabi

xcopy ..\..\openSrc\lib\gson\gson-2.8.2.jar ..\app\libs
xcopy ..\..\openSrc\lib\okhttp\okhttp-3.5.0.jar ..\app\libs
xcopy ..\..\openSrc\lib\okio\okio-1.13.0.jar ..\app\libs
xcopy ..\..\openSrc\lib\retrofit\converter-gson-2.3.0.jar ..\app\libs
xcopy ..\..\openSrc\lib\retrofit\retrofit-2.3.0.jar ..\app\libs

xcopy ..\..\sources\libs ..\app\libs
xcopy /s/e D:\android\armeabi ..\app\libs\armeabi

cd ../
gradlew clean assembleRelease -p /app --offline
