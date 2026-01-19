native-image ^
-Os ^
--gc=serial ^
--enable-http --enable-https ^
-jar target\downloader-1.0.0-jar-with-dependencies.jar