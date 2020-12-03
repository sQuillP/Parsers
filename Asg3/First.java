import java.util.*;
import java.io.*;
/*
* William Pattison
* wmpatti
*/
public class First
{

  /*Grammar list read from file */
  private ArrayList<String> grammar = new ArrayList<>();

  /*List of non-terminal symbols from the grammar list*/
  private ArrayList<String> NTSymbols = new ArrayList<>();


  /*Constructor class, calls readFile to build the grammar, then adds
  the Non-terminals symbols to the non-terminal symbols list.
  firstSet() method is used to print the first set of every non-terminal
  symbol*/
  public First(String filename)
  {readFile(filename);}


  /*Finds the first set of a symbol from a grammar file. Uses recursion
  to traverse down the list of grammar. */
  public String getSymbols(String NTSymbol)
  {
    StringTokenizer st;
    String match,symbols = "";
    if(!NTSymbols.contains(NTSymbol))
      return NTSymbol;
    for(int i = 0; i<grammar.size(); i++)
    {
      st = new StringTokenizer(grammar.get(i)," ");
      match = st.nextToken();
      if(NTSymbol.equals(match))
      {
        match = st.nextToken();
        if(!st.hasMoreTokens())
        {
          if(!match.equals(NTSymbol))
          {
            if(!NTSymbols.contains(match)&&symbols.indexOf(match+" ")<0)
              symbols += match + " ";
            else
              symbols += getSymbols(match);
          }
        }
        else
        {
          while(st.hasMoreTokens())
          {
            if(containsLambda(match)&&NTSymbol.equals(match))
              match = st.nextToken();
            else if(match.equals(NTSymbol))
             break;
            if(!NTSymbols.contains(match))
            {symbols += match + " "; break;}
            else if(containsLambda(match))
              symbols += (getSymbols(match)).replace("LAMBDA ","");
            else
            {
              symbols += getSymbols(match);
              break;
            }
            if(st.countTokens()==1&&containsLambda(match))
            {
              match = st.nextToken();
              if(NTSymbols.contains(match))
                symbols += getSymbols(match);
              else
              {symbols += match+ " ";break;}
            }
            else if(st.hasMoreTokens())
              match = st.nextToken();
          }
        }
     }
    }
    return removeDup(symbols);
  }


  /*Reads a file from a specified file path, returns an error if the
  file cannot be opened. */
  public void readFile(String filename)
  {
    String line;
    StringTokenizer st;
    try {
      BufferedReader br = new BufferedReader(new FileReader(filename));
      line = br.readLine();
      while(line!=null)
      {
        grammar.add(line);
        line = br.readLine();
      }
    } catch(IOException E){
      System.out.println("Could not open file");
    }
    for(int i = 0; i<grammar.size(); i++)
    {
      st = new StringTokenizer(grammar.get(i)," ");
      line = st.nextToken();
      if(NTSymbols.indexOf(line)<0)
        NTSymbols.add(line);
    }
  }


  /*Removes a duplicate value from a string*/
  private String removeDup(String funcRet)
  {
    StringTokenizer st = new StringTokenizer(funcRet," ");
    String el, append = "";
    while(st.hasMoreTokens())
    {
      el = st.nextToken();
      if(!append.contains(el+" "))
        append += el+" ";
    }
    return append;
  }


  /*Returns true if Lambda is contained in the first set of a Non-terminal symbol,
  helper method for firstSet().*/
  public boolean containsLambda(String NTSymbol)
  {
    StringTokenizer st;
    String el;
    boolean lambda = false;
    if(NTSymbols.contains(NTSymbol))
    {
      for(int i = 0; i<grammar.size(); i++)
      {
        st = new StringTokenizer(grammar.get(i)," ");
        el = st.nextToken();
        if(el.equals(NTSymbol))
        {
          el = st.nextToken();
          if(el.equals("LAMBDA"))
          {lambda = true; break;}
          else if(NTSymbols.contains(el)&&!st.hasMoreTokens())
            lambda = containsLambda(el);
        }
      }
    }
    return lambda;
  }


  /*Print the first set of every non-terminal symbol
  to the screen.*/
  public void printFirstSet()
  {
    System.out.println("\nFirst Sets\n-------------------");
    for(int i = 0; i<NTSymbols.size(); i++)
      System.out.println(NTSymbols.get(i)+": "+getSymbols(NTSymbols.get(i)));
  }
}
