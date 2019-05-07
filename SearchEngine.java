import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class SearchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings) 
    public DirectedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    SearchEngine() {
 // Below is the directory that contains all the internet files
 HtmlParsing.internetFilesLocation = "internetFiles";
 wordIndex = new HashMap<String, LinkedList<String> > ();  
 internet = new DirectedGraph();    
    } // end of constructor//2017
    
    
    // Returns a String description of a searchEngine
    public String toString () {
 return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {
     internet.addVertex(url); //adding to the directed empty internet graph
     internet.setVisited(url,true);//setting the url as visited
     //parsing words found on the url website and links and storing them in two separate LinkedLists
      LinkedList<String> contents = HtmlParsing.getContent(url);
      LinkedList<String> links = HtmlParsing.getLinks(url);
      Iterator<String> i = contents.iterator();
      Iterator<String> j = links.iterator();
      //iterating through the url site
      //finding words in url and adding it to the end of a linked list
      while(i.hasNext()){
        String s = i.next();
        LinkedList <String> words = new LinkedList <String> ();
        if(wordIndex.containsKey(s)){
          words = wordIndex.get(s);
          words.addLast(url);
          wordIndex.put(s, words);
        }
        else{ //checking for if word does not exist and putting in a linked list
          words.addLast(url);
          wordIndex.put(s, words);
        }
      }
      //recursively calling the links and adding to the internet graph
      while(j.hasNext()){
        String t = j.next();
        internet.addEdge(url, t);
        if(!internet.getVisited(t)){
          traverseInternet(t);
        }
      } 
    } // end of traverseInternet
    
    
    /* This computes the pageRanks for every vertex in the internet graph.
       It will only be called after the internet graph has been constructed using 
       traverseInternet.
       Use the iterative procedure described in the text of the assignment to
       compute the pageRanks for every vertices in the graph. 
       
       This method will probably fit in about 30 lines.
    */
    void computePageRanks() {
      LinkedList<String> vertices = new LinkedList<String>();
      vertices = internet.getVertices();
      Iterator<String> j = vertices.iterator(); //iterator to iterator through a link list
      //setting the initial page rank to 1
      while(j.hasNext()){
        String s = j.next();
        internet.setPageRank(s, 1);
      }
      //finding the page rank 100 iterations
      for(int i = 0; i < 100; i++){
        j = vertices.iterator();
        //iterating through every vertex
        //computing the page rank of each vertex
        while(j.hasNext()){
          String s = j.next();
          double pageDamping = 0.5;
          Iterator<String> listEdges = internet.getEdgesInto(s).iterator();
          while(listEdges.hasNext()){
            String r = listEdges.next();
            pageDamping += 0.5*(internet.getPageRank(r)/internet.getOutDegree(r));
          }
          internet.setPageRank(s, pageDamping);
        }
      }
    } // end of computePageRanks
    
 
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
    */
    String getBestURL(String query) {
      String highest = "";//initialising the highest url
      double pageRank = 0; //initialising temp page rank
      double highestPageRank = -1;//initialising highest page rank
      //LinkedList that stores the hashmap value
      LinkedList<String> websites = new LinkedList<String>();
      //checking if the query is in the hashmap
      if(!(wordIndex.containsKey(query))){
        return "";
      }
      else{
        websites = wordIndex.get(query);
      }
      //iterating through the LinkedList
      Iterator<String> i = websites.iterator();
      while(i.hasNext()){
        String s = i.next();
        pageRank = internet.getPageRank(s);
        //updating the highestPage rank and highest url
        if(highestPageRank < pageRank){
          highestPageRank = pageRank;
          highest = s;
        }
      }
      return highest; 
    } // end of getBestURL
    
    
 
    public static void main(String args[]) throws Exception{  
 SearchEngine mySearchEngine = new SearchEngine();
 // to debug your program, start with.
  mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
 
 // When your program is working on the small example, move on to
 //mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
 
 // this is just for debugging purposes. 
 System.out.println(mySearchEngine);
 
 mySearchEngine.computePageRanks();
 
 BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
 String query;
 do {
     System.out.print("Enter query: ");
     query = stndin.readLine();
     if ( query != null && query.length() > 0 ) {
  System.out.println("Best site = " + mySearchEngine.getBestURL(query));
     }
 } while (query!=null && query.length()>0);    
    } // end of main
}
