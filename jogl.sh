#!/bin/sh
LIBPATH=./jogl-maclib
export LIBPATH
CLASSPATH=.:./jogl-jar/gluegen-rt-natives-macosx-universal.jar:./jogl-jar/gluegen-rt.jar:./jogl-jar/jogl-all-natives-macosx-universal.jar:./jogl-jar/jogl-all.jar
export CLASSPATH
javac *.java
java -Djava.library.path=$LIBPATH CgMain
