package Part2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArbitrageFinder extends BestConversionFinder {

  public static void main(String[] args) {
    System.out.println("---- TEST1 ----");
    //Test1
    double[][] input1 = {
    	//0:NZD, 1:EUR, 2:CNY, 3:USD, 4:GBP, 5:AUD
        {1, 0.58, 0, 0, 0, 0}, //NZD
        {0, 1, 8.0, 0, 0, 0}, //EUR
        {0, 0, 1, 0.15, 0, 0}, //CNY
        {0, 0, 0, 1, 0.75, 0}, //USD
        {0, 0, 0, 0, 1, 1.77}, //GBP
        {0, 0, 0, 0, 0, 1}, //AUD
    };
    ArbitrageFinder arbitrageFinder1 = new ArbitrageFinder(input1);
    System.out.println(arbitrageFinder1.hasArbitrage);
    arbitrageFinder1.getArbitrage().forEach(System.out::println);

    //Test2
    System.out.println("---- TEST2 ----");
    double[][] input2 = {
    	//0:NZD, 1:EUR, 2:CNY, 3:USD
        {1, 0.58, 0, 1.8}, //NZD
        {0, 1, 8.0, 0}, //EUR
        {0.13, 0, 1, 0.15}, //CNY
        {0, 0, 6.67, 1}, //AUD
    };
    ArbitrageFinder arbitrageFinder2 = new ArbitrageFinder(input2);
    System.out.println(arbitrageFinder2.hasArbitrage);
    arbitrageFinder2.getArbitrage().forEach(System.out::println);
  }

  private boolean hasArbitrage = false;
  private List<String> arbitrage;

  public ArbitrageFinder(double[][] input) {
    super(input);
    arbitrage = new ArrayList<>();
    compute();
  }

  //Calculate whether there is a closed loop
  private void compute() {
    for (int i = 0; i < n; i++) {
      Pair<Double, String>[] book = getNewPairs();
      DepthFirstSearch(i, i, book, 1.0, "");
      if (book[i]._1 > 1) {
        hasArbitrage = true;
        arbitrage.add(book[i]._2);
      }
    }
    deduplication(arbitrage);
  }

  //Deduplication
  private void deduplication(List<String> arbitrage) {
    List<String> res = new ArrayList<>();
    Set<String> set = new HashSet<>();
    arbitrage.forEach(str -> {
      String[] strs = str.split("->");
      Arrays.sort(strs);
      String key = Arrays.toString(Arrays.stream(strs).distinct().toArray());
      if (!set.contains(key)) 
    	res.add(str);
      set.add(key);
    });
    this.arbitrage = res;
  }

  //Depth first search
  private void DepthFirstSearch(int start, int cur, Pair<Double, String>[] book, double value, String path) {
    if (book[cur]._1 >= value) 
      return;
    book[cur]._1 = value;
    if (path.isEmpty()) 
      book[cur]._2 = cur + "";
    else 
      book[cur]._2 = path + "->" + cur;
    if (book[cur]._2.split("->").length > n) 
      return;
    if (cur == start && value != 1) 
      return;
    for (Pair<Integer, Double> next : nexts[cur]) 
      DepthFirstSearch(start, next._1, book, value * next._2 + Math.log(1 / next._2), book[cur]._2);
  }

  //Is there arbitrage
  public boolean hasArbitrage() {
    return hasArbitrage;
  }

  //Return arbitrage
  public List<String> getArbitrage() {
    return arbitrage;
  }
}
