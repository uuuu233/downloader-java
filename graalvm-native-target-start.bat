native-image ^
-Os ^
--gc=serial ^
--no-fallback ^
--enable-http --enable-https ^
-jar target\downloader-1.0.0-jar-with-dependencies.jar

pause