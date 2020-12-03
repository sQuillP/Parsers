import java.util.*;
import java.io.*;




public class Follow
{


  /*
  * William Pattison
  * wmpatti
  */


  /*This program will find the first and follow set of any
  table that does not contain indirect left recursion. This is becuase
  the first and the follow set are printed from this program. They are
  not separate.*/


  /*Grammar list that the program iterates through*/
  private ArrayList<String> grammar = new ArrayList<>();


  /*List of non terminal symbols*/
  private ArrayList<String> NTSymbols = new ArrayList<>();


  /*First set object for getting the first set of a symbol, and for
  printing out the first set to the user.*/
  private First firstSet;


  /*Constructor will read the file, and call first
  and follow set from the data that is provided.*/
  public Follow(String filename)
  {
    readFile(filename);
    firstSet = new First(filename);
    System.out.println("\nFollow Sets\n--------------");
    for(int i = 0; i<NTSymbols.size(); i++)
      System.out.println(NTSymbols.get(i)+": "+followSet(NTSymbols.get(i)));
  }


  /*Finds the follow set of a symbol using recursion. Although recursion
  was highly discouraged for the follow set I got it to work on 7 text files of
  test data.*/
  private String followSet(String element)
  {
    StringTokenizer st;
    String token,folSet = "",orig;
    for(int i = 0; i<grammar.size(); i++)
    {
      st = new StringTokenizer(grammar.get(i)," ");
      token = st.nextToken();orig = token;//first symbol
      if(element.equals(NTSymbols.get(0)))
        folSet += "$ ";
      while(st.hasMoreTokens())
      {
        token = st.nextToken();//second symbol
        if(token.equals(element)&&st.hasMoreTokens())
        {
          token = st.nextToken();
          if(element.equals(orig)&&!st.hasMoreTokens()&&NTSymbols.contains(token))
            break;
          else if(!NTSymbols.contains(token))
            folSet += token + " ";
          if(!st.hasMoreTokens())
          {
            token = firstSet.getSymbols(token);
            if(token.contains("LAMBDA "))
            {
              folSet += token.replace("LAMBDA ","");
              folSet += followSet(orig);
            }
            else
              folSet += token + " ";
          }
          else if(NTSymbols.contains(token))
          {
            folSet += followThrough(st,token);
            if(firstSet.containsLambda(token)&&st.hasMoreTokens())
            {
              token = st.nextToken();
              if(NTSymbols.contains(token))
              {
                folSet += firstSet.getSymbols(token);
                if(firstSet.containsLambda(token))
                {
                  folSet = folSet.replace("LAMBDA ","");
                  if(!orig.equals(element))
                    {folSet += followSet(orig);}
                }
                else
                  folSet += token+ " ";
              }
              else
                {folSet += token+ " ";}
             }
          }
        }
        else if(!orig.equals(element)&&token.equals(element))
          {folSet += followSet(orig);}
      }
    }
    return removeDup(folSet);
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


  /*This method will continue to find the first set of a string if it
  contains lambda. Helper method for the follow set method.*/
  private String followThrough(StringTokenizer st, String token)
  {
    String fThrough = "";
    fThrough += firstSet.getSymbols(token);
    if(fThrough.contains("LAMBDA "))
      fThrough = fThrough.replace("LAMBDA ","");
    else
      return fThrough;
    while(st.countTokens()>1)
    {
      token = st.nextToken();
      fThrough += firstSet.getSymbols(token);
      if(fThrough.contains("LAMBDA "))
        fThrough = fThrough.replace("LAMBDA ","");
      else
        break;
    }
      return fThrough;
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
}
