#!/bin/bash 
fileName=`cygpath -w $2`
cd /cygdrive/c/eclipse/workspace/BeaMeWW/scripts
cmd /c java -classpath '.;c:\eclipse\workspace\BeaMeWW\target\BeaMeWW-0.0.1-SNAPSHOT-jar-with-dependencies.jar' de.beatmesse.ww.SMSPump $1 $fileName
#cd /cygdrive/c/Users/Juergen/Documents/workspace-sts-2.5.1.RELEASE/BeaMeWW/scripts
#cmd /c java -classpath '.;C:\Users\Juergen\Documents\workspace-sts-2.5.1.RELEASE\BeaMeWW\target\BeaMeWW-0.0.1-SNAPSHOT-jar-with-dependencies.jar' de.beatmesse.ww.SMSPump $1 $fileName
