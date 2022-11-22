package Part2;

import java.util.ArrayList;
import java.util.List;

public class BestConversionFinder {
  public static void main(String[] args) throws Exception {
    double[][] input = {
    	//0:NZD, 1:EUR, 2:CNY, 3:USD, 4:GBP, 5:AUD
        {1, 0.58, 0, 0, 0, 0}, //NZD
        {0, 1, 8.0, 0, 0, 0}, //EUR
        {0, 0, 1, 0.15, 0, 0}, //CNY
        {0, 0, 0, 1, 0.75, 0}, //USD
        {0, 0, 0, 0, 1, 1.77}, //GBP
        {0, 0, 0, 0, 0, 1}, //AUD
    };
    BestConversionFinder finder = new BestConversionFinder(input);
    System.out.println(finder.findBestConversionAndRoute(0,2));
    System.out.println(finder.findBestConversionAndRoute(0,3));
    System.out.println(finder.findBestConversionAndRoute(0,4));
    System.out.println(finder.findBestConversionAndRoute(0,5));
    System.out.println(finder.findBestConversionAndRoute(1,3));
    System.out.println(finder.findBestConversionAndRoute(1,4));
    System.out.println(finder.findBestConversionAndRoute(1,5));
    System.out.println(finder.findBestConversionAndRoute(2,4));
    System.out.println(finder.findBestConversionAndRoute(2,5));
    System.out.println(finder.findBestConversionAndRoute(3,5));
  }

  protected List<Pair<Integer, Double>>[] nexts;
  protected final int n;
  private final double[][] input;
  
  //Search exchange rate return route
  public String findBestConversionAndRoute(int src, int dst) throws Exception {
    if (src < 0 || src >= n || dst < 0 || dst >= n) 
      throw new Exception("Out of range");
    Pair<Double, String>[] book = getNewPairs();
    final double MAX = (Integer.MAX_VALUE >> 1) * 1.0;
    for (int i = 0; i < book.length; i++)
      book[i]._1=MAX;
    DepthFirstSearch(src, book, 1.0, "");
    if(book[dst]._1 == MAX)
      return "Cannot redeem!";
    return "Exchange cost:"+ book[dst]._1 + "-------The value of the exchanged money:" + help(book[dst]._2) + "-------Conversion path: " + book[dst]._2;
  }

  private String help(String s) {
    String[] strs = s.split("->");
    double res= 1d;
    for (int i=1; i<strs.length; i++) 
      res *= input[Integer.parseInt(strs[i-1])][Integer.parseInt(strs[i])];
    return res+"";
  }

  //help creation
  @SuppressWarnings("unchecked")
  protected Pair<Double, String>[] getNewPairs() {
    Pair<Double, String>[] book = new Pair[n];
    for (int i = 0; i < n; i++) 
      book[i] = new Pair<>(0d, "");
    return book;
  }

  protected void DepthFirstSearch(int cur, Pair<Double, String>[] book, double value, String path) {
    if (book[cur]._1 <= value) 
      return;
    book[cur]._1 = value;
    if(path.isEmpty())
      book[cur]._2=cur+"";
    else 
      book[cur]._2=path+"->"+cur;
    for (Pair<Integer, Double> next : nexts[cur]) 
      DepthFirstSearch(next._1, book, value-Math.log(1 / next._2), book[cur]._2);
  }

  @SuppressWarnings("unchecked")
  public BestConversionFinder(double[][] input) {
    n = input.length;
    nexts = new List[n];
    this.input=input;
    for (int i = 0; i < n; i++) 
      nexts[i] = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (input[i][j] != 0) 
          nexts[i].add(new Pair<>(j, input[i][j]));
      }
    }
  }
}
