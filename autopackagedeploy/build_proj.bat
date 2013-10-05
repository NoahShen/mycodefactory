set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_25
cd D:\work\gdo-helios-workspace-64\coke\tif-core
call mvn clean install -Dmaven.test.skip
cd D:\work\gdo-helios-workspace-64\coke\tif-cca
call mvn clean install -Dmaven.test.skip