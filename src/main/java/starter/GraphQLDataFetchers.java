package starter;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

//ORIENTDB IMPORTS
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;

public class GraphQLDataFetchers {

    private static List<Map<String, String>> books = Arrays.asList(
            ImmutableMap.of("id", "book-1", "name", "Harry Potter and the Philosopher's Stone", "pageCount", "223",
                    "authorId", "author-1"),
            ImmutableMap.of("id", "book-2", "name", "Moby Dick", "pageCount", "635", "authorId", "author-2"),
            ImmutableMap.of("id", "book-3", "name", "Interview with the vampire", "pageCount", "371", "authorId",
                    "author-3"));

    private static List<Map<String, String>> authors = Arrays.asList(
            ImmutableMap.of("id", "author-1", "firstName", "Joanne", "lastName", "Rowling"),
            ImmutableMap.of("id", "author-2", "firstName", "Herman", "lastName", "Melville"),
            ImmutableMap.of("id", "author-3", "firstName", "Anne", "lastName", "Rice"));

    public DataFetcher getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return books.stream().filter(book -> book.get("id").equals(bookId)).findFirst().orElse(null);
        };
    }

    public DataFetcher getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String, String> book = dataFetchingEnvironment.getSource();
            String authorId = book.get("authorId");
            return authors.stream().filter(author -> author.get("id").equals(authorId)).findFirst().orElse(null);
        };
    }

    /*CUSTOMER DATA_FETCHER*/
    public DataFetcher getCustomersDataFetcher(ODatabaseSession db)
    {
        try {
/*---------------------------SOLUTION - 3 START ----------------------------*/
/*AS DIDN'T GET THE CONTEXT SOLUTION YET,
    TRIED PASSING THE DB SESSION TO getCustomersDataFetcher FUNCTION
    BUT THIS DB SESSION ALSO DIDN'T WORKED INSIDE BELOW
        return dataFetchingEnvironment -> { }
    AS MENTIONED IN SOLUTION 2
    SO TRIED EXECUTING QUERY HERE AND STORED IT IN CUSTOMERS LIST AND SAME LIST IS RETURNED WHENEVER RESOLVER CALLS THE GRAPHQL QUERY

    THIS IS NOT THR PROPER SOLUTION AS RESOLVER WILL ONLY RETURN THE INITIALLY DATA STORED IN THE CUSTOMERS LIST BELOW
 */
            List<Map<String, String>> customers = new ArrayList<Map<String, String>>();
            OResultSet rs = db.query("SELECT @rid AS rid, CustomerNo, name,CurrencyCode FROM CUSTOMER");
            System.out.println("\nQuery Result received ");
             if (rs == null) {
                 System.out.println("ResultSet is null");
             }
             else
             {
                 System.out.println("ResultSet is present going in while " );
                 while (rs.hasNext())
                 {
                     OResult item = rs.next();
                     Map<String, String> mp = new HashMap<String, String>();
                     mp.put("name",item.getProperty("name"));
                     mp.put("rid", ""+ item.getProperty("rid"));
                     mp.put("CustomerNo",item.getProperty("CustomerNo"));
                     mp.put("CurrencyCode",item.getProperty("CurrencyCode"));
                     customers.add(mp);
                 }
             }
             rs.close();
/*---------------------------SOLUTION - 3 END ----------------------------*/

            return dataFetchingEnvironment -> {
        //                    List<Map<String, String>> customers = new ArrayList<Map<String, String>>();
                    System.out.println("Inside dataFetchingEnvironment of customer");

/*---------------------------SOLUTION - 1 START ----------------------------*/
/* THIS BELOW CHANGES TRIED WHICH
* 1. CONNECTS TO THE ORIENTDB CLIENT
* 2. GETS THE DB SESSION
* 3. EXECUTES THE QUERY AND RETURN THE RESULT
* BUT IT WILL TAKE TIME IF EACH TIME RESOLVER TRIES TO CONNECT TO THE DATABASE AND TAKE NEW SESSION
* */
                    // OrientDB orient = new OrientDB("remote:192.168.0.113", OrientDBConfig.defaultConfig());
                    // ODatabaseSession db = orient.open("test", "admin", "admin");
//                     String query = "SELECT @rid AS rid, CustomerNo, name,CurrencyCode FROM CUSTOMER";
//                     System.out.println("\nQuery to run: " + query);
//                     if (db != null ) {
//                     OResultSet rs = db.query(query);
//                     if (rs == null) {
//                         System.out.println("ResultSet is null");
//                     }
//                     else
//                     {
//                         System.out.println("ResultSet is present going in while " );
////                         while (rs.hasNext())
////                         {
////                             OResult item = rs.next();
////                             Map<String, String> mp = new HashMap<String, String>();
////                             mp.put("name",item.getProperty("name"));
////                             mp.put("rid", ""+ item.getProperty("rid"));
////                             mp.put("CustomerNo",item.getProperty("CustomerNo"));
////                             mp.put("CurrencyCode",item.getProperty("CurrencyCode"));
////                             customers.add(mp);
////                             System.out.println("name: " + item.getProperty("name"));
////                         }
//                     }

                        // rs.close();
                        // db.close();
                        // orient.close();
//                     }else
//                         System.out.println("Database instance is null" );

/*---------------------------SOLUTION - 1 END ----------------------------*/



/*---------------------------SOLUTION - 2 START ----------------------------*/
    /*TRIED GETTING THE CONTEXT FORM THE dataFetchingEnvironment BUT DIDN'T GET THE CONTEXT ATTRIBUTES
     SET IN ON_BEFORE AND DECORATOR IN APP.JAVA
     HERE IF WE GET THE DATABASE SESSION INSTANCE FROM CONTEXT THEN WE CAN DIRECTLY CALL TO THE DATABASE FOR FETCHING CUSTOMERS DATA
     */
//                    System.out.println("\n=================dataFetchingEnvironment.getContext() == null=================");
//                    System.out.println("\nContents are:  "+ dataFetchingEnvironment.getContext());

/*---------------------------SOLUTION - 2 END ----------------------------*/



/*---------------------------SOLUTION - 3 START ----------------------------*/
        /* INSTEAD OF THE SOLUTION 3 AFTER GETTING THE db SESSION INSTANCE FOR THE getCustomersDataFetcher FUNCTION
        * TRIED EXECUTING DB QUERY IN THE
        *       return dataFetchingEnvironment -> {   }
        * SECTION ,
        * BUT FOR SOME REASON IT GIVES THE NULL POINTER EXCEPTION AFTER EXECUTING db.query() EVEN IF DB SESSION INSTANCE IS AVAILABLE
        * */
/*---------------------------SOLUTION - 3 END ----------------------------*/
                return customers;
            };
           
        } catch (Exception e) {
            System.out.println("Error Inside getCustomersDataFetcher");
            System.out.println(e);
            throw e;
        }
    }
}


/*
* CUSTOMERS GRAPHQL QUERY:
*
 query {
  customers {
    rid
    CustomerNo
    name
  }
}
*
* */