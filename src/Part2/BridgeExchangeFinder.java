package Part2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class BridgeExchangeFinder {
  public static void main(String[] args) {
    BridgeExchangeFinder finder1 = new BridgeExchangeFinder(new double[][]{
    	/* 0:a, 1:b, 2:c, 3:d, 4:e, 5:f, 6:g, 
    	 * 7:h, 8:i, 9:j, 10:k, 11:l, 12:m */
    	{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //a
        {1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //b
        {0, 1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0}, //c
        {0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, //d
        {0, 0, 1, 0, 0, 1, 1, 1, 0, 1, 0, 0, 0}, //e
        {0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0}, //f
        {0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0}, //g
        {0, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0}, //h
        {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0}, //i
        {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 1, 1}, //j
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0}, //k
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1}, //l
        {0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0}, //m 
    });
    System.out.println("cutPoints:");
    finder1.allCutVer().forEach(System.out::println);
    System.out.println("---------------");
    System.out.println("cutEdges:");
    finder1.allCutEdge().stream().map(Arrays::toString).forEach(System.out::println);
  }

  private Set<Integer>[] edges;
  protected final int n;
  private boolean[] marked;//Used to mark vertices that have been visited
  private int[] low;
  private int[] dfn;
  private int[] parent;
  private boolean[] isCutVer;//Used to mark whether or not a cutPoint
  private List<Integer> cutVers;//Container for storing cutPoints

  //The container that stores the cut edge. The container stores an array.
  // Each array has only two elements, representing the two vertices attached to the edge.
  private List<int[]> cutEdges;

  @SuppressWarnings("unchecked")
  public BridgeExchangeFinder(double[][] input) {
    n = input.length;
    low = new int[n];
    dfn = new int[n];
    parent = new int[n];
    isCutVer = new boolean[n];
    marked = new boolean[n];
    cutVers = new ArrayList<>();
    cutEdges = new ArrayList<>();
    edges = new HashSet[n];
    for (int i = 0; i < n; i++) 
      edges[i] = new HashSet<>();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (input[i][j] != 0) {
          edges[i].add(j);
          edges[j].add(i);
        }
      }
    }
    DepthFirstSearch();
  }

  private int visitOrder;//TimesTamp variable

  // Depth First Search
  private void DepthFirstSearch() {
    int childTree = 0;
    marked[0] = true;
    visitOrder = 1;
    parent[0] = -1;
    for (int w : edges[0]) {
      if (!marked[w]) {
        marked[w] = true;
        parent[w] = 0;
        DepthFirstSearch0(w);
        /* Whether the edge connected by the root vertex is a bridge */
        if (low[w] > dfn[0]) 
          cutEdges.add(new int[]{0, w});
        childTree++;
      }
    }
    /* Process root vertices individually */
    if (childTree >= 2) //The root vertex is the condition of the cutPoint
      isCutVer[0] = true;  
  }

  //In addition to the root vertex
  private void DepthFirstSearch0(int v) {
    dfn[v] = low[v] = ++visitOrder;
    for (int w : edges[v]) {
      if (!marked[w]) {
        marked[w] = true;
        parent[w] = v;
        DepthFirstSearch0(w);
        low[v] = Math.min(low[v], low[w]);
        if (low[w] >= dfn[v]) {
          isCutVer[v] = true;
          if (low[w] > dfn[v]) 
        	cutEdges.add(new int[]{v, w});
        }
      } 
      else if (parent[v] != w && dfn[w] < dfn[v]) 
    	low[v] = Math.min(low[v], dfn[w]);
    }
  }

  //Return all cutPoints
  public List<Integer> allCutVer() {
    for (int i = 0; i < isCutVer.length; i++) {
      if (isCutVer[i]) 
    	cutVers.add(i);
    }
    return cutVers;
  }

  //Return all cut edges
  public List<int[]> allCutEdge() {
    return cutEdges;
  }

  //Determine whether vertex is a cutPoint
  public boolean isCutVer(int v) {
    return isCutVer[v];
  }
}
