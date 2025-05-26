class A{
}

class B extends A{
}

public class test{


  static void test(A a){
    System.out.println("A");
  }  
  static void test(B b){
    System.out.println("B");
  }

  public static void main(String args[]){
    A a = new A();
    A b = new B();
    test(a);
    test((B)b);
  }
}
