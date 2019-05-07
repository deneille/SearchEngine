import java.io.*;
import java.util.*;
import java.lang.*;
import java.net.*;

/* YOU SHOULD NOT NEED TO LOOK AT THIS CODE AT ALL.
   BUT IT COULD BE INTERESTING FOR YOU TO SEE HOW IT WORKS. */

// This class implements a simple parser for html documents
public class HtmlParsing {
    
    static String internetFilesLocation="internetFiles"; 
    static HashMap<String,String > queriedURL_links=new HashMap<String,String >();
    static HashMap<String,String > queriedURL_content=new HashMap<String, String >();

    // In Windows, change the line below to static String directoryChar="\\";
    static String directoryChar = "/";
        
    /* getLinks returns a LinkedList of Strings corresponding containing all
       the links out of a given url. Only the the www.cs.mcgill.ca domain is 
       considered. Personnal web pages (those with URL containing "~") 
       are not considered. Only html links are considered. The given url
       must contain the full path (implicit "index.html" are not allowed).
       So "http://www.cs.mcgill.ca" is not a valid URL, but "http://www.cs.mcgill.ca/index.html"
       is. */
           
    static public LinkedList<String> getLinks(String url) throws Exception {
        BufferedReader myURL;
        LinkedList<String> ret = new LinkedList<String>();  // the list of URLs to be returned
        String domain;
        // url=fixURL(url).toLowerCase();
 boolean isMainPage=url.equals("http://www.cs.mcgill.ca");
 if (queriedURL_links.containsKey(url)) throw new Exception("Tried to query the following URL twice: "+url);
 queriedURL_links.put(url,"1");
        try {
                        
              URLConnection Conn;
              URL u= new URL(url);
              Conn  = u.openConnection();
       //       System.out.println("Opening "+url); 
              myURL  = new BufferedReader(new InputStreamReader(Conn.getInputStream()));
              
       //            myURL = new BufferedReader( new FileReader( filename ) );
        }
        catch (Exception e) { 
     System.out.println( "Error opening URL:" + url + "\n" + e);
     return new LinkedList<String>();
        }
 
        // get the domain name

        if ( url.indexOf( ".html" )!=-1 ||  url.indexOf( ".php" ) != -1) domain = url.substring( 0 , url.lastIndexOf( '/' ) );
        else domain = url;
 
 while (domain.substring(domain.length()-1,domain.length()).equals("/")) domain=domain.substring(0,domain.lastIndexOf('/'));
        String line = null;
        // read each line of the file
 boolean inMenu=true;
        do {
            try {
                line = myURL.readLine();
            }
            catch (Exception e) {continue;}
            // get the links in the current line
     LinkedList<String> n = parseLine( line , domain );
     
            if (isMainPage || !inMenu) ret.addAll( n );
     if (line!=null && line.indexOf("panel-body")!=-1) inMenu=false;
        } while (line!=null);
        
        try {
            myURL.close();
        } catch (Exception e) {};
        return ret;
    } // end of getLinks


    /* Returns a LinkedList of Strings corresponding containing all
       the words at a given url. Only the the www.cs.mcgill.ca domain is 
       considered. Personnal web pages (those with URL containing "~") 
       are not considered. Only html links are considered. The given url
       must contain the full path (implicit "index.html" are not allowed).
       So "http://www.cs.mcgill.ca" is not a valid URL, but "http://www.cs.mcgill.ca/index.html"
       is. Words inside html tags are not considered. */
    static public LinkedList<String> getContent(String url) throws Exception {
 //        url = fixURL( url ).toLowerCase();
        // String filename = internetFilesLocation + directoryChar + url.replace(':','+').replace('/','+');
        BufferedReader myURL;

 if (queriedURL_content.containsKey(url)) throw new Exception("Tried to query the following URL twice: "+url);
 queriedURL_content.put(url,"1");

        try {
                
              URLConnection Conn;
              URL u= new URL(url);
              Conn  = u.openConnection();
       //              System.out.println("Opening "+url);
              myURL  = new BufferedReader(new InputStreamReader(Conn.getInputStream()));
       // myURL = new BufferedReader( new FileReader(filename) );
        }
        catch (Exception e) {
     System.out.println("Error opening URL: " + url + "\n" + e); 
            return new LinkedList<String>();
        }
                
        String domain;
        if (url.charAt(0)=='/') domain=new String("http://www.cs.mcgill.ca/");
        if (url.indexOf(".html")!=-1) domain= url.substring(0,url.lastIndexOf('/'));
        else domain=url;
 while (domain.substring(domain.length()-1,domain.length()).equals("/")) domain=domain.substring(0,domain.lastIndexOf('/'));
 // System.out.println("doamin = "+domain);
        String line=null;
                
        // this string will contain the whole file on one line
        String wholeFile ="";
        do {
            try { line = myURL.readLine(); }
            catch ( Exception e ) { continue; }
            if ( line!=null ) wholeFile = wholeFile + " " + line;
        } while ( line!=null );

        try {
            myURL.close();
        } catch (Exception e) {};
        
        // now parse wholeFile, removing anything between < and >
        StringTokenizer st = new StringTokenizer( wholeFile , "<>~`!@#$%^&*(),.?;:[]{}+=-_\\|\" ",true);        
        LinkedList<String> ret = new LinkedList<String>();
        boolean insideTag = false;
        while ( st.hasMoreTokens() ) {
            String next = st.nextToken();
            if ( next.equals("<") ) { insideTag = true; continue;}
            if ( next.equals(">") ) { insideTag = false; continue;}
            if (insideTag) continue;
            if ( Character.isLetterOrDigit( next.charAt(0) )) ret.addLast( next.toLowerCase() );
        }
        return ret;
    } // end of getContent

    // Returns a fixed URL where /index.html is added if necessary
    // You should not need to use this method
    static public String fixURL(String url) {
 // replace %7e by ~
 int i=url.indexOf("%7e");
 if (i==-1) i=url.indexOf("%7E");
 if (i!=-1) url=url.substring(0,i)+"~"+url.substring(i+3,url.length());
 if (url.charAt(url.length()-1)=='/') url=url.substring(0,url.length()-1);
 /*
        if ( url.indexOf(".html") == -1 ) {
            if ( url.charAt(url.length() - 1 ) == '/' ) url = url + "index.html";
            else url = url + "/index.html";
        }
 */

 return url;

        //int i = url.lastIndexOf("//");
        //if (i==5) return url;
        //else return url.substring(0,i)+url.substring(i+1,url.length());
    } //end of fixURL

    // Returns a LinkedList of strings contained in a line
    // You should not need to use this method
    static public LinkedList<String> getLineContent(String line) {
        if ( line == null ) return new LinkedList<String>();
        StringTokenizer st = new StringTokenizer( line , " " , false);        
        LinkedList<String> ret = new LinkedList<String>();
        while ( st.hasMoreTokens() ) ret.addLast( st.nextToken() );
        return ret;
    } // end getLineContent
                        



    // Returns a LinkedList of URLs contained in the line.
    // If only partial URLs is present, domain is prepended
    // You should not need to use this method
    static public LinkedList<String>  parseLine(String line, String domain) {
        String key = "a href=\"" ;
        LinkedList<String> ret = new LinkedList<String> ();
 // hacf to prevent navigational links to count as real links.

        if ( line == null) return ret;
 // if (line.indexOf("<li>")!=-1) return ret; // this prevents lines corresponding to the domain map to be included.
        //line = line.toLowerCase();
        int position = line.indexOf(key);        
        if (position == -1 || line.length() < key.length() ) return ret;
        String s = line.substring(position+key.length());

        int end = s.indexOf("\"");
        if ( end==-1 ) return ret;
        String u = s.substring( 0 , end );

 // System.out.println(u);
        // Remove URLS that are not html files.
        if (( domain.indexOf(u)!=-1 ||  u.indexOf("javascript")!=-1 ||  u.indexOf("~")!=-1  || u.indexOf("https")!=-1 ||  u.indexOf("Members")!=-1 || u.indexOf(".doc")!=-1 || u.indexOf("reservations")!=-1 || u.indexOf("announcements")!=-1 || u.indexOf("registration")!=-1 || u.indexOf("login_form")!=-1 || u.indexOf("mailto")!=-1 || u.indexOf("#")!=-1 || u.indexOf("jette")!=-1 || u.indexOf("bias")!=-1 || u.indexOf(".cgi")!=-1|| u.indexOf(".mp3")!=-1 || u.indexOf(".ppt")!=-1|| u.indexOf(".pl")!=-1||  u.indexOf(".jpg")!=-1||  u.indexOf(".dmg")!=-1||  u.indexOf(".mso")!=-1|| u.indexOf(".xml")!=-1|| u.indexOf(".css")!=-1||  u.indexOf("internal")!=-1||  u.indexOf(".java")!=-1||  u.indexOf(".png")!=-1|| u.indexOf("<li>")!=-1 ||
       u.indexOf(".exe")!=-1 || u.indexOf(".aft")!=-1 || u.indexOf(".pdf")!=-1 || u.indexOf(".PDF")!=-1 ||u.indexOf(".ico")!=-1 || u.indexOf(".ps")!=-1 || u.indexOf("?")!=-1 || u.indexOf("credits")!=-1 || u.indexOf("..")!=-1 || u.indexOf("./")!=-1 || u.indexOf("@")!=-1 || u.indexOf("tar")!=-1 || u.indexOf("gz")!=-1 || u.indexOf("zip")!=-1 || u.indexOf("txt")!=-1 || u.indexOf("hqx")!=-1 || u.indexOf("mws")!=-1 || u.indexOf("finak")!=-1 || u.indexOf("nex")!=-1) || u.indexOf("docs")!=-1)
            return ret;
        if (u.length()>0 && u.charAt(0)=='/') u = "http://www.cs.mcgill.ca/"+u;        
        else if ( u.indexOf("http:") == -1) u = domain + "/" + u;

 // replace // by / and remove / and the end
 int flag;
 flag=0;
 // System.out.println("Fixing "+u);
 do {
     flag=u.indexOf("//",7);
     if (flag!=-1) u=u.substring(0,flag)+u.substring(flag+1,u.length());
 } while (flag!=-1);
 if (u.charAt(u.length()-1)=='/') u=u.substring(0,u.length()-1);
 // System.out.println("becomes "+u);
        if (u.indexOf(" ")==-1 && u.indexOf("http://www.cs.mcgill.ca") != -1 ) {
            u = fixURL(u);
            ret.addLast(u);
        }
        return ret;
    }   // end of parseLine  

        // write the content of a URL to a file.
        // You should not need to use this method
    static public void writeContent(String url) {
             url=url.toLowerCase();
             String fileurl="internetFiles"+directoryChar+url.replace('/','+').replace(':','+');
        BufferedReader myURL;
        URLConnection Conn;
        BufferedWriter out=null; 
        try {
            out = new BufferedWriter(new FileWriter(fileurl));
        } catch (Exception e) {System.out.println("e0"+e);}        
        try {
            URL u= new URL(url);
            Conn  = u.openConnection();
            
            myURL  = new BufferedReader(new InputStreamReader(Conn.getInputStream()));
        }
        catch (Exception e) { System.out.println("e1+"+e);return;}
        System.out.println("WRITING TO "+fileurl);
        String domain;
        if (url.charAt(0)=='/') domain="http://www.cs.mcgill.ca";
        else if (url.indexOf(".html")!=-1) domain= url.substring(0,url.lastIndexOf('/'));
        else domain=url;
        String line=null;
        String wholeFile ="";
        do {
            try {
                line=myURL.readLine();
                if (line!=null) out.write(line+"\n",0,line.length()+1);
            }
            catch (Exception e) {System.out.println("e2+"+e);continue;}
            if (line!=null) wholeFile=wholeFile+"\n"+line;
        } while (line!=null);
        try {
            myURL.close();
            out.close();
        } catch (Exception e) {System.out.println("e3+"+e);};
        System.out.println("Done writing");
    }
} // end of writeContent
