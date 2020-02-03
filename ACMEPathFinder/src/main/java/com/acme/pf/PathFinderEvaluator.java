package com.acme.pf;

import org.apache.commons.lang3.mutable.MutableDouble;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.traversal.BranchState;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.PathEvaluator;

import java.util.List;

public class PathFinderEvaluator implements PathEvaluator<PathFinderState> {

    private final Node destination;
    private MutableDouble shortestSoFar;
    private final List<Node> exclusionList;

    public PathFinderEvaluator(Node destination, MutableDouble shortestSoFar, List<Node> exclusionList) {
        this.destination = destination;
        this.shortestSoFar = shortestSoFar;

        // Exclusion list for assets out of service temporarily
        this.exclusionList = exclusionList;
    }

    @Override
    public Evaluation evaluate(Path path, BranchState<PathFinderState> state) {

        if(path.length() == 0){
            System.out.println("PathFinderEvaluator.evaluate path.length==0");
            return Evaluation.EXCLUDE_AND_CONTINUE;
        }

        if (path.lastRelationship().getProperty(Properties.isActive).toString().equals("false"))
        {
            System.out.println("PathFinderEvaluator.evaluate relationship not active");
            return Evaluation.EXCLUDE_AND_PRUNE;
        }

        System.out.println("PathFinderEvaluator.evaluate lastNode -->  "+path.endNode().toString());
        System.out.println("PathFinderEvaluator.evaluate cost -->  "+state.getState().getCost());

        // Maximum computable path for this example (database has only 21 nodes at the moment)
        if(path.length() > 40){
            System.out.println("PathFinderEvaluator.evaluate path.length>40");
            return Evaluation.EXCLUDE_AND_PRUNE;
        }

        // We could prune paths that are any longer than the shortest we have so far
//        if ( state.getState().getCost() >= (Double) shortestSoFar.getValue() ) {
//            System.out.println("PathFinderEvaluator.evaluate path.length>shortestSoFar");
//            return Evaluation.EXCLUDE_AND_PRUNE;
//        }

        // If we reach the destination we can stop (INCLUDE_AND_PRUNE), or keep going to find other solutions (INCLUDE_AND_CONTINUE)
        if (path.endNode().equals(destination)){
            System.out.println("PathFinderEvaluator.evaluate reaching destination");

            //Updating shortest path
            if ( state.getState().getCost() < (Double) shortestSoFar.getValue() )
                shortestSoFar.setValue(state.getState().getCost());

            //return Evaluation.INCLUDE_AND_PRUNE;
            return Evaluation.INCLUDE_AND_CONTINUE;
        }

        if (exclusionList != null){
            if(exclusionList.contains(path.endNode())){
                return Evaluation.EXCLUDE_AND_PRUNE;
            }
        }

//        if (path.endNode().hasLabel(Labels.Point)){
//            if(path.endNode().hasLabel(Labels.Escalator)){
//                return Evaluation.EXCLUDE_AND_CONTINUE;
//            } else {
//                return Evaluation.EXCLUDE_AND_PRUNE;
//            }
//        }

        return Evaluation.EXCLUDE_AND_CONTINUE;
    }

    @Override
    public Evaluation evaluate(Path path) {
        return null;
    }
}
