package Part2;

//Tuple container
public class Pair<K,V> {
  K _1;
  V _2;

  public Pair(K _1, V _2) {
    this._1 = _1;
    this._2 = _2;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder("{");
    sb.append("\"1\":").append(_1);
    sb.append(",\"2\":").append(_2);
    sb.append('}');
    return sb.toString();
  }
}
