package starter;

import graphql.schema.idl.RuntimeWiring;
import io.jooby.Jooby;
import io.jooby.Route;
import io.jooby.Router;
import io.jooby.annotations.*;
import io.jooby.graphql.GraphQLModule;
//import io.jooby.graphql.GraphQLPlaygroundModule;
import io.jooby.graphql.GraphiQLModule;
import io.jooby.json.JacksonModule;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;
//ORIENTDB IMPORTS
import com.orientechnologies.orient.core.db.ODatabaseSession;
import com.orientechnologies.orient.core.db.OrientDB;
import com.orientechnologies.orient.core.db.OrientDBConfig;
import com.orientechnologies.orient.core.sql.executor.OResult;
import com.orientechnologies.orient.core.sql.executor.OResultSet;
import jdk.nashorn.internal.ir.RuntimeNode;
import javax.annotation.Nonnull;

public class App extends Jooby {
    {
        /*HERE CREATED THE ORIENTDB CLIENT AND GOT THE TEST DB SESSION WHICH NEEDS TO BE SENT IN THE GRAPHQL CONTEXT*/
        OrientDB orient = new OrientDB("remote:192.168.0.113", OrientDBConfig.defaultConfig());
        ODatabaseSession db = orient.open("test", "admin", "admin");

    /*TRIED THE DECORATOR AND THE BEFORE METHODS FOR SETTING THE ATTRIBUTES IN CONTEXT AND
    * REFER SAME IN GRAPHQL DATA_FETCHERS BUT THIS BELOW CONTEXT SET, NOT ACCESSIBLE INSIDE THE DATA_FETCHER CONTEXT */
        decorator(next -> ctx -> {
            System.out.println("\nInside Decorator Setting testing context value");
            ctx.attribute("testing", "TestingValue");
            return next.apply(ctx);
        });

        before(ctx -> {
            System.out.println("\nInside Before Setting context value");
            ctx.attribute("BeforeTesting", "BeforeTestingValue");
        });


    /*CONTEXT ATTRIBUTES SET IN ABOVE BEFORE AND DECORATOR CAN BE ACCESSIBLE IN THE NORMAL GET POST REQUESTS*/
        get("/", req -> {
            System.out.println("getting request context  " + req.getAttributes());
            return "Testing Query ";
        });


        install(new JacksonModule());

        GraphQLDataFetchers graphQLDataFetchers = new GraphQLDataFetchers();
        install(new GraphQLModule(RuntimeWiring.newRuntimeWiring()
                .type(newTypeWiring("Query").dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher()))
                .type(newTypeWiring("Query").dataFetcher("customers", graphQLDataFetchers.getCustomersDataFetcher( db ))) //HERE PASSED THE DB SESSION TO DATA_FETCHER
                .type(newTypeWiring("Book").dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher()))
                .build())
                );

        /** Choose between GraphiQL or GraphQLPlayGround: */
        install(new GraphiQLModule());
        // install(new GraphQLPlaygroundModule());

    }

    public static void main(String[] args) {
        runApp(args, App::new);
    }
}

