import java.util.*;
/*William Pattison*/

public class LL1
{

  /*Global variables*/
  private String input;
  private String token;
  private String operators = "()*+-/";
  private StringTokenizer st;

  /*Constructor for LL(1), Creates the String tokenizer and grabs the first
  token, print an error if an empty string is entered.*/
  LL1(String input)
  {
    this.input = input;
    if(input.equals(""))
      error();
    else
    {
      st = new StringTokenizer(this.input,operators,true);
      next();
    }
  }

  /*Check to see if the token is an actual number. If the typcasting
  fails, then the typecasting fails*/
  private void isNumber()
  {
    if(!(operators+"$").contains(token))
    {
      try{ Double.parseDouble(token);}
      catch (Exception E) {error();}
    }
  }


  /*Gets the next token to parse, returns end of file if there are no more
  tokens*/
  public void next()
  {
    if(st.hasMoreTokens())
    {
      token = st.nextToken();
      isNumber();
    }
    else
      token = "$";
  }



  /*Builder method for E. This method starts an operation.
  It is the very root of the program.*/
  private void E()
  {
    if("-+*/)$".contains(token))
     error();
    else
    {
      T();
      E1();
    }
  }


  /*Builder method for T. Rather than using a switch statement for every
  possible char, it only makes sense to combine them into a string and then
  check to see if the character is in the string.*/
  private void T()
  {
    if("+-*/)$".contains(token))
      error();
    else
    {
      F();
      T1();
    }
  }


 /*Builder method for E1 (or E prime) Since there are no calculations in this program,
 it made things easier to put the addition and subtraction in the same 'if' statement.
 This method follows the exact rules specified in the LL1 parser table. All addition and subtraction
 is handled in this method*/
 private void E1()
 {
   if("+-".contains(token))
   {
     next();
     T();
     E1();
   }
   else if("$)".contains(token))
     return;
   else
     error();
 }


/*Builder method for T (or T prime). Since there are no calculations in this
program, It only made sense to make the times and division operator execute the same
code. This also method follows the exact rules specified in the LL1 parser table.
Multiplication and division are handled in this method.*/
 private void T1()
 {
   if("/*".contains(token))
   {
     next();
     F();
     T1();
   }
   else if("+-$)".contains(token))
    return;
   else
    error();
 }


 /*Builder method for F, Since it contains terminal symbols,
 it uses next() to get the next token for the other functions in the
 callstack to use.*/
 private void F()
 {
   if(token.equals("("))
   {
     next();
     E();
     if(!token.equals(")"))
      error();
     next();
   }
   else if(!"+-*/)$".contains(token))
    next();
   else
     error();
 }


/*Runs the root and checks if all the tokens have been parsed.
if so, return an error. If there are any extra closing parenthesis on the
right side, the program will terminate early, leaving extra unparsed tokens in the program.
Adding an extra 'if' statement after calling the root will fix this problem.*/
public void validate()
{
  E();
  if(!token.equals("$"))
    error();
  else
    System.out.println("Valid expression");
}


/*Error method if the expression is invalid, exits the program.*/
private void error()
{
  System.out.println("Invalid Expression");
  System.exit(0);
}


  /*Runner for the LL1*/
  public static void main(String[] args)
  {
    //Tell the user how the program works
    if(args.length == 0)
    {
      System.out.println("\nUse: java LL1 <expression>\n");
      System.exit(0);
    }
    else
    {
      LL1 test = new LL1(args[0]);
      test.validate();
    }
  }

}
