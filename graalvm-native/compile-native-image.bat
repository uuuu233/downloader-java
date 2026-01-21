native-image ^
-Os ^
--gc=serial ^
--no-fallback ^
--enable-http --enable-https ^
-jar ..\target\downloader-1.0.0-jar-with-dependencies.jar

pause

::-H:+AddAllCharsets ^
::--initialize-at-build-time=org.slf4j,ch.qos.logback
::-H:NativeLinkerOption=/SUBSYSTEM:WINDOWS
::-H:NativeLinkerOption="${project.basedir}/src/main/resources/icon.res"
::上面2个要绝对路径