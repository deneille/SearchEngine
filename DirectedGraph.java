import java.util.*;

class DirectedGraph {
    
    /* The graph is stored in a hash table (called HashMap in Java).
       The vertices are identified by a String, which is used as key in the
       hash table. The information associated to each vertex v is a LinkedList of
       Strings, describing all vertices that v links to v */
    
    HashMap<String,LinkedList<String> > vertices;
    
    /* The key of that hash table is a String (URL) and the value is a boolean indicating
       if the vertex has been set to visited or not */
    HashMap<String, Boolean> visited;
    
    /* We use this hash table to store page ranks */
    HashMap<String, Double> pageRank;
    
    // Constructor, creates an empty graph
    public DirectedGraph() {
        vertices = new HashMap<String,LinkedList<String> > ();
        visited = new HashMap<String, Boolean>();
        pageRank = new HashMap<String, Double>();
    } 
    

    // Adds a vertex v to the graph
    public void addVertex( String v ) {
        // if the vertex is already present, don't add it
        if ( vertices.containsKey(v) ) return;
        vertices.put( v, new LinkedList<String>() );
    } // end of addVertex
    
    
    // Adds an edge from v1 to v2
    public void addEdge(String v1, String v2) {
        // if key1 or key2 are not in the graph, add them.
        if (!vertices.containsKey(v1)) addVertex(v1);
        if (!vertices.containsKey(v2)) addVertex(v2);

        // if the edge is not already in the list of neighbors of key1, add it
        // notice that we need to cast the result of the get method 
        LinkedList<String> edges = vertices.get(v1);
        if (!edges.contains(v2)) edges.addLast(v2);
    } // end of addEdge 
    
    
    // Returns a LinkedList of vertices that are the neighbors of key
    public LinkedList<String> getNeighbors(String v) {
        return vertices.get(v);
    } // end of getNeighbors
    
    
    // Returns a LinkedList of all vertices in the graph
    public LinkedList<String> getVertices() {
        Iterator<String> i = vertices.keySet().iterator();
        // since keySet return a Set, we need to transfer it to a LinkedList 
        LinkedList<String> l = new LinkedList<String>();
        while ( i.hasNext() ) l.addLast( i.next() );
        return l;
    } // end of getVertices
    
    // Returns the list of vertices that have links to v
    LinkedList<String> getEdgesInto( String v ) {
        LinkedList<String> l = new LinkedList<String>();
        Iterator<String> i = vertices.keySet().iterator();
        while (i.hasNext()) {
            String s = i.next();
            if ( ( vertices.get(s) ).contains(v)) l.addLast(s);
        }
        return l;
    } // end of getEdgesInto
    
    // Returns the numbers of edges going out of vertex v
    int getOutDegree(String v) {
        return ( vertices.get(v) ).size();
    }        
    
    // returns the page rank of a given vertex. If the vertex doesn't exist, returns 0
    double getPageRank(String url) {
        if ( pageRank.containsKey(url) ) return (pageRank.get(url) ).doubleValue();
        else return 0;
    }


    // sets the pageRank of a given vertex
    void setPageRank(String url, double pr) {
        pageRank.put(url,new Double(pr));
    }


    // returns the visited status of a given url
    boolean getVisited(String url) {
        if (visited.containsKey(url) && (visited.get(url) ).booleanValue()) return true;
        else return false;
    }

    // sets the visited status of a given url
    void setVisited(String url, boolean b) {
        visited.put(url,new Boolean(b));
    }

    public String toString() {
        return "Vertices: \n" + vertices.toString() + 
            "\nVisited:\n"+visited.toString()+
            "\npageRanks:\n"+pageRank;
    }
}
