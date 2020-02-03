
package com.acme.pf;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.*;
import org.neo4j.procedure.Context;
import org.neo4j.procedure.Description;
import org.neo4j.procedure.Name;
import org.neo4j.procedure.UserFunction;

public class PathFinder{

    @Context
    public GraphDatabaseService db;

    @UserFunction
    @Description("com.acme.pf.traverse(n, dest, ['r1','r2',...], delimiter) - traverse the graph with the given delimiter.")
    public List<Path> traverse(@Name("location") Node location,@Name("dest") Node destination,@Name("rels") List<String> rels,@Name("limit") long limit ) throws IOException
    {
        List<Path> result = new LinkedList<Path>();

        try {
            result = getAssets(location, destination, rels, limit).collect(Collectors.toList());


        }catch (NullPointerException e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String exceptionAsString = sw.toString();
            System.out.println(exceptionAsString);
        }
//        catch (Exception e)
//        {
//            StringWriter sw = new StringWriter();
//            e.printStackTrace(new PrintWriter(sw));
//            String exceptionAsString = sw.toString();
//            System.out.println(exceptionAsString);
//        }

        return result;
    }

    @UserFunction
    @Description("com.acme.pf.returnLocation(n) - return Location.")
    public Node returnLocation(@Name("location") Node location ) throws IOException{

        return location;
    }


    // tag::getAssets[]
    private Stream<Path> getAssets( final Node startNode, final Node endNode, final List<String> rels,final long limit )
    {

        MutableDouble shortestPathSofar = new MutableDouble(Double.MAX_VALUE);
        PathFinderEvaluator pathFinderEvaluator = new PathFinderEvaluator(endNode,shortestPathSofar,new LinkedList<>());
        PathFinderExpander expander = new PathFinderExpander(rels);
        InitialBranchState.State<PathFinderState> pfState = new InitialBranchState.State<>(new PathFinderState(), new PathFinderState());

        TraversalDescription td = db.traversalDescription()
                   .breadthFirst().expand( expander, pfState ).evaluator(pathFinderEvaluator)
                    .uniqueness(Uniqueness.NODE_PATH);


        return td.traverse( startNode ).stream();
    }
}




