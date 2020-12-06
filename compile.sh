. $(dirname "$0")/config.sh

hadoop com.sun.tools.javac.Main java/$jobname.java
cd java
jar cf mapreduce-$jobname.jar $jobname*.class
mv mapreduce-$jobname.jar ../