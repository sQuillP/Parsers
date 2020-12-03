import java.util.*;

/*
* William Pattison
* wmpatti
*/

/*
  This LR1 shift-reduce parser will validate and calculate any string expression
  passed into the constructor. The number ranges from 0-32767.
*/

public class LR1
{


  /*Helper class for LR1 parser. This class holds information about terminal
  and non terminal symbols.*/
  class Item
  {
    public String symbol;
    public double value;
    public int state;
    private boolean isNum;


    /*Constructor method for Item*/
    Item(String symbol, double value, int state)
    {
      this.state = state;
      this.symbol = symbol;
      this.value = value;

      if(value == -1)
        isNum = false;
      else
        isNum = true;
    }


    /*Print the contents of the item, helper method for printing contents of stack*/
    public String toString()
    {
      if(isNum)
        return "("+symbol+"="+value+":"+state+")";
      else
        return "("+symbol+":"+state+")";
    }
  }


  /*Hold Contents of items to be parsed*/
  private ArrayList<String> queue = new ArrayList<>();


  /*Hold all items in the stack*/
  private ArrayList<Item> stack = new ArrayList<>();


  /*Holds the value of the first item in the queue*/
  private String element;


  /*Constructor method, creates a string tokenizer and loads each element into the queue
  */
  LR1(String input)
  {
    StringTokenizer st = new StringTokenizer(input,"()-+/*",true);
    while(st.hasMoreTokens())
    {
      queue.add(st.nextToken());
      if(queue.get(queue.size()-1).equals("$"))
       error();
    }

    stack.add(new Item("#",-1,0));
    next();
  }


  /*First entry in the LR1 table, once this method is called, the program will begin parsing*/
  private void S0()
  {
    if(isDigit(element))
    {
      stack.add(new Item("n",Double.parseDouble(element),5));
      next();
      S5();
    }
    else if(element.equals("("))
    {
      stack.add(new Item("(",-1,4));
      next();
      S4();
    }
    else
      error();
  }


  /*State 1 in LR1 table. This method deals with pushing symbols "+" and "-" onto
  the stack.*/
  private void S1()
  {
    if("+-".contains(element))
    {
      stack.add(new Item(element,-1,6));
      next();
      S6();
    }
    else if(element.equals("$"))
      System.out.println("\nValid Expression, value = "+
      (stack.get(stack.size()-1)).value);

    else
      error();
  }


  /*State 2 in LR1 table, Builds non-terminal symbol E or adds
  multiplication and division to the stack.*/
  private void S2()
  {
    if("+-)$".contains(element))
    {
      Item temp = stack.get(stack.size()-1);
      temp.symbol = "E";
      nextState(temp);
    }
    else if("*/".contains(element))
    {
      stack.add(new Item(element, -1, 7));
      next();
      S7();
    }
    else
      error();
  }


  /*State 3 in LR1 table, Builds non-terminal symbol T from F.*/
  private void S3()
  {
    if("+-*/)$".contains(element))
    {
      Item temp = stack.get(stack.size()-1);
      temp.symbol = "T";
      nextState(temp);
    }
    else
      error();
  }


  /*State 4 in LR1 table, adds "n" with its value to the stack and
  calls the 5th table index. It also adds a closing parenthesis and calls
  state 4 in the table*/
  private void S4()
  {
    if(isDigit(element))
    {
      stack.add(new Item("n",Double.parseDouble(element),5));
      next();
      S5();
    }
    else if(element.equals("("))
    {
      stack.add(new Item("(",-1,4));
      next();
      S4();
    }
    else
      error();
  }


  /*State 5 in LR1 table, builds non-terminal symbol "F"*/
  private void S5()
  {
    if("+-*/)$".contains(element))
    {
      Item temp = stack.get(stack.size()-1);
      temp.symbol = "F";
      nextState(temp);
    }
    else
      error();
  }


  /*State 6 in LR1 table and has two functionalities. It pushes a
  double onto the stack and calls state 5 or it pushes a closing parenthesis
  and calls state 4.*/
  private void S6()
  {
    if(isDigit(element))
    {
      stack.add(new Item("n",Double.parseDouble(element),6));
      next();
      S5();
    }
    else if("(".contains(element))
    {
      stack.add(new Item("(",-1,4));
      next();
      S4();
    }
    else
      error();
  }


  /*State 7 in LR1 table, it either pushes an double onto the stack
  and calls state 5 or it pushes a closing parenthesis and calling state 4. */
  private void S7()
  {
    if(isDigit(element))
    {
      stack.add(new Item("n",Double.parseDouble(element),5));
      next();
      S5();
    }
    else if(element.equals("("))
    {
      stack.add(new Item("(", -1, 4));
      next();
      S4();
    }
    else
      error();
  }


  /*State 8 in LR1 table. It can push an addition or subtraction operator followed
  by calling state 6. It can also push a closing parenthesis onto the stack followed by
  calling state 11.*/
  private void S8()
  {
    if("+-".contains(element))
    {
      stack.add(new Item(element, -1, 6));
      next();
      S6();
    }
    else if(element.equals(")"))
    {
      stack.add(new Item(element, -1, 11));
      next();
      S11();
    }
    else
      error();
  }


  /*State 9 in LR1 table. It can build non-terminal symbol E or it can
  push division or multiplication onto the stack followed by calling state 7.*/
  private void S9()
  {
    if("+-)$".contains(element))
    {
      Item n = new Item("E", evaluate(), 0);
      stack.add(n);
      nextState(stack.get(stack.size()-1));
    }
    else if("*/".contains(element))
    {
      stack.add(new Item(element,-1, 7));
      next();
      S7();
    }
    else
      error();
  }


  /*State 10 in LR1 table. Its only function is to build T given the current
  token that is in the queue, otherwise it will create an error.*/
  private void S10()
  {
    if("+-*/)$".contains(element))
    {

      Item n = new Item("T", evaluate(), 0);
      stack.add(n);
      nextState(n);
    }
    else
      error();
  }


  /*State 11 in the LR1 table. It's only function is to eliminate
  parenthesis from an expression that has been evaluated already. It grabs
  the value from E and pops both enclosing parenthesis, building a new non-terminal
  symbol F.*/
  private void S11()
  {
    if("+-*/)$".contains(element))
    {
      Item n = (stack.get(stack.size()-2));
      Item replace = new Item("F",n.value,0);
      for(int i = 0; i<3; i++)
        stack.remove(stack.size()-1);
      stack.add(replace);
      nextState(replace);
    }
    else
      error();
  }


  /*Prints the contents of the queue to the screen*/
  private void printQueue()
  {
    System.out.print("Input Queue: ["+element);
    for(String el : queue)
      System.out.print(el);

      System.out.println("]");
  }


  /*Prints the contents of hte stack to the screen*/
  private void printStack()
  {
    System.out.print("Stack [");
    for(Item n : stack)
      System.out.print(n.toString());
      System.out.print("]\t\t");
  }


  /*Returns true if a String can be cast into a double*/
  boolean isDigit(String number)
  {
    try{
      Double.parseDouble(number);
      return true;
   }
    catch(Exception e) {
       return false;
    }
  }


  /*Removes three items (two operands and an operator) from the stack and returns
  the left to right evaluation of the expression*/
  private double evaluate()
  {
    double op2 = (stack.get(stack.size()-1)).value;
    String operator = (stack.get(stack.size()-2)).symbol;
    double op1 = (stack.get(stack.size()-3)).value;
    for(int i = 0; i<3; i++)
      stack.remove(stack.size()-1);
    if(operator.equals("+"))
      return op1 + op2;
    else if(operator.equals("-"))
      return op1-op2;
    else if(operator.equals("/"))
    {
      if(op2 == 0)
      {
        System.out.println("Division by zero error");
        error();
      }
      return op1/op2;
    }
    else if(operator.equals("*"))
     return op1*op2;
    else
    {
      error();
      return 0;
    }
  }


  /*Returns the state of the first terminal symbol in the stack*/
  private int findState()
  {
    int find;
    for(int i = stack.size()-1; ; i--)
    {
      if("#+-*/()".contains((stack.get(i)).symbol))
      {
        find = (stack.get(i)).state;
        break;
      }
    }
    return find;
  }



  /*After building a non-terminal symbol, nextState() will call findState()
  to get the state of the first non-terminal symbol. Once this is done, nextState()
  will figure out which state to call given the current non-terminal symbol on the stack.
  Helper method editState() is used to update the current item on the stack to reduce redundancy
  of code*/
  private void nextState(Item cur)
  {
    int state = findState();
    if(state == 0)
    {
      switch(cur.symbol)
      {
        case "E": editState(cur,1); S1(); break;
        case "T": editState(cur,2); S2(); break;
        case "F": editState(cur,3); S3(); break;
        default: error(); break;
      }
    } else if(state == 4)
    {
      switch(cur.symbol)
       {
        case "E": editState(cur,8); S8(); break;
        case "T": editState(cur,2); S2(); break;
        case "F": editState(cur,3); S3(); break;
        default: error(); break;
      }
    } else if(state == 6)
    {
      switch(cur.symbol)
      {
        case "T": editState(cur,9); S9(); break;
        case "F": editState(cur,3); S3(); break;
        default: error(); break;
      }
    } else if(state == 7)
    {
      if(cur.symbol.equals("F"))
      {
        editState(cur,10);
        S10();
      }
      else
        error();
    }
  }


  /*Remove an element from the queue and update the current element,
  print contents of stack and queue to the screen.*/
  public void next()
  {
    if(queue.isEmpty())
      element = "$";
    else
    {
      element = queue.get(0);
      queue.remove(0);
    }
    printStack();
    printQueue();
  }


  /*Method will update the state of an item followed by printing the stack
  and queue to the screen. Helper method for nextState()*/
  private void editState(Item current, int state)
  {
    current.state = state;
    printStack();
    printQueue();
  }


  /*Print an error to the screen and exit the program.*/
  private void error()
  {
    System.out.println("Invalid expression");
    System.exit(0);
  }


  /*Begins parsing elements from the queue by calling the root index 0 from
  the LR1 table*/
  public void parse() {S0();}


  /*Runner creates the LR1 object and calls parse() to start the program.*/
  public static void main(String[] args)
  {
    if(args.length == 1)
    {
      LR1 test = new LR1(args[0]);
      test.parse();
    }
    else
      System.out.println("Use: java LR1 \"<expression>\"");
  }
}
