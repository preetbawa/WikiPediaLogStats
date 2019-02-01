# WikiPediaLogStats
Github to explore doing statistical analysis on Wikipedia logs
The project depends on the format of logs in pagecount format downloaded from this location 
https://dumps.wikimedia.org/other/pagecounts-ez/merged/2019/2019-01/ - i used 01-25 dump for my testing.

log for application goes under target/rolling/<logname>
  
 maven based project - so just run mvn clean install 
 then go to target folder 
 and run executable java jar with these parameters. - i used 4gb jvm heap starting frm 2gb - 
 
 java -Xms2048m -Xmx4096m -Dlog4j.configurationFile=..\src\log4j2.properties -jar wikipediaStats-1.0-SNAPSHOT.jar <Directory path where u download wikipedia logs from>
 logic:
  code is structured with use of OOP concepts using inheritance, modeling, design patterns.  - it processes one line at a time from file to avoid loading while into array with bigger memory footprint, though i do have to store objects for each line to aggregate page visits for each pagetitle. visitor pattern is used to assess max, min visited page along with k frequent pages. code is very flexible as u can generate kfrequent for any type of page. 
  
  every time it processes new file, it will clear cache stored for the last file. 
  
  
