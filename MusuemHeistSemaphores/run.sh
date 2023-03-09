find . -name "*.class" -type f -print0 | xargs -0 /bin/rm -f
javac MusuemHeist.java
java -Djava.util.logging.SimpleFormatter.format='%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n' MusuemHeist
