public class Main
{

  /*
  * William Pattison
  * wmpatti
  */

  /*Runner class for first and follow sets*/

  public static void main(String[] args)
  {
    if(args.length!=1)
      System.out.println("Use: java Main <text file>");
    else
    {
      Follow test = new Follow(args[0]);
      First test2 = new First(args[0]);
      test2.printFirstSet();
    }
  }
}
