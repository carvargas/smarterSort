package csi403;


// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.json.*;


// Extend HttpServlet class
public class smarterSort extends HttpServlet {
	

  // Standard servlet method 
  public void init() throws ServletException
  {
      // Do any required initialization here - likely none
  }

  // Standard servlet method - we will handle a POST operation
  public void doPost(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      doService(request, response); 
  }

  // Standard servlet method - we will not respond to GET
  public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
      // Set response content type and return an error message
      response.setContentType("application/json");
      PrintWriter out = response.getWriter();
      out.println("{ 'message' : 'Use POST!'}");
  }

  // Our main worker method
  // Parses messages e.g. {"inList" : [5, 32, 3, 12]}
  // Returns the list sorted.   
  private void doService(HttpServletRequest request,
                    HttpServletResponse response)
            throws ServletException, IOException
  {
	  try{
      // Get received JSON data from HTTP request
      BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
      String jsonStr = "";
      if(br != null){
          jsonStr = br.readLine();
      }
	  
      // Create JsonReader object
      StringReader strReader = new StringReader(jsonStr);
      JsonReader reader = Json.createReader(strReader);

      // Get the singular JSON object (name:value pair) in this message.    
      JsonObject obj = reader.readObject();
      // From the object get the array named "inList"
      JsonArray inArray = obj.getJsonArray("inList");
      JsonArrayBuilder outArrayBuilder = Json.createArrayBuilder();
	  
	  //variables
	  int i, j, k, q, n;
	  int break_l = 0, check = 0, index = 0, temp;
	  
	  String buf1 = "";
	  
	  String buf2 = "";
	  
	  String temp_s = "";
	  
	  //to store all names in the pairs of each array
	  String[] names = new String[(2 * inArray.size())];
	  
	  int[] order = new int[(2 * inArray.size())];
	  
	  for(i = 0; i < inArray.size(); i++)
	  {
		  //grabs each object in the inList array
		  JsonObject inObj = inArray.getJsonObject(i);
		  
		  //grabs each array in the smarter array 
		  JsonArray innerArray = inObj.getJsonArray("smarter"); 
			
		  //stores the names in pair
		  buf1 = innerArray.getString(0);
		  buf2 = innerArray.getString(1);
		  
		  if(check == 0)
		  {
			  /*no searching for these names since array hasn't
			  been initialized yet so just store the first two 
			  names and their orders*/
			  names[index] = buf1;
			  order[index] = index++;
			  names[index] = buf2;
			  order[index] = index++;
			  check++;
			  continue;
		  } 
			
		  for(k = 0; k < index; k++)
		  {
			  if(buf1.equals(names[k]))//if first name is stored
			  {
				  for(j = 0; j < index; j++)
				  {
					  if(buf2.equals(names[j]))
					  {
						  //both names are in array so no storing
						  //determine proper order
						  if(order[k] < order[j])
						  {
							  order[k] = order[j] + 1;
						  }
						  
						  break_l++;
						  break;
					  }
					  
					  else if((j+1) == index)
					  {
						  //first name in array, second name is not
						  //grab its order number
						  //store order + 1 for next element in json array
						  names[index] = buf2;
						  order[index] = order[k] + 1;
						  
						  break_l++;
						  index++;
						  break;
					  }
				  }
			  }
			  
			  else if(buf2.equals(names[k]))//if second name is stored
			  {
				//first name is not in array, but second name is 
				names[index] = buf1;
				order[index] = order[k] - 1;
				if(order[index] == -1)
				{
					//switch positions in the array
					temp_s = names[k];
					names[k] = buf1;
					names[index] = temp_s;
					order[index] = order[k];	
					for(n = 1; n <= index; n++)
					{
						order[n] = (order[n] + 1);
					}
				}
				break_l++;
				index++;
			  }
			  
			  else if((k+1) == index)//both names are not in array
			  {	
				names[index] = buf1;
			    order[index] = index++;
				names[index] = buf2;
				order[index] = index++;
				break_l++;
			  }
			  
			  //break loop once one of the conditions are satisfied
			  if(break_l != 0)
			  {
				  break_l = 0;
				  break;
			  }
		  }
	  }
	  
	  //name arrays and order arrays should be done now
	  
	  //sort names and orders array in ascending order
	  for (i = 0; i < index; i++) 
	  {
		  for (j = i + 1; j < index; j++) 
		  {
			  if (order[i] > order[j]) 
			  {
				  temp_s = names[i];
				  names[i] = names[j];
				  names[j] = temp_s;
				
				  temp = order[i];
				  order[i] = order[j];
				  order[j] = temp;
			  }
		  }
	  }
		
	  for(i = 0; i < index; i++) 
	  {
          outArrayBuilder.add(names[i]); 
      }
      
      // Set response content type to be JSON
      response.setContentType("application/json");
      // Send back the response JSON message
      PrintWriter out = response.getWriter();
      out.println("{ \"outList\" : " + outArrayBuilder.build().toString() + "}"); 
      }
	  
	  //catch errors
	  catch(JsonException e){
		  response.setContentType("application/json");
		  PrintWriter out = response.getWriter();
		  out.println("{ \"message\" : \"Json Exception!\"}");
	  }
	  catch(IllegalStateException e){
		  response.setContentType("application/json");
		  PrintWriter out = response.getWriter();
		  out.println("{ \"message\" : \"illegal state!\"}");
	  }
	  catch(Exception e){
		  response.setContentType("application/json");
		  PrintWriter out = response.getWriter();
		  out.println("{ \"message\" : \"exception!\"}");
	  }
  }	  
	
  // Standard Servlet method
  public void destroy()
  {
      // Do any required tear-down here, likely nothing.
  }
}

