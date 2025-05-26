
public class tmp{
  public static void main(String args[]){
    double tmp[][];
    Object o[] = new double[10][];
    for (int i = 0; i < 10; i++){
      o[i] = new double[10];
    }

    tmp = (double[][])o;
  }

}
