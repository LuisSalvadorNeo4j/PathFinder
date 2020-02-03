package com.acme.pf;

import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.BranchState;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

public class PathFinderExpander implements PathExpander<PathFinderState>{
    private final List<String> rels;

    public PathFinderExpander(List<String> rels) {
        this.rels = rels;
    }


    /**
     * PF - Path expand
     *
     * config:
     *
     *
     * @param path
     * @param state
     * @return Iterable<Relationship>
     */
    @Override
    public Iterable<Relationship> expand(Path path, BranchState<PathFinderState> state) {


        PathFinderState pathFinderState = state.getState().copy();

//        if (path.endNode().hasLabel(Labels.Gatelines)){
//            return expandFromGatelines(path, state);
//        }
//
//        if (path.endNode().hasLabel(Labels.Escalator) && path.lastRelationship().isType(RelationshipTypes.RAMP)){
//            return path.endNode().getRelationships(RelationshipTypes.RAMP, Direction.OUTGOING );
//        }


        if (path.lastRelationship() != null) {
            System.out.println("PathFinderExpander.expand Incrementing cost in: " + (double)path.lastRelationship().getProperty(Properties.distance));
            pathFinderState.incrementCost((double)path.lastRelationship().getProperty(Properties.distance));

            System.out.println("PathFinderEvaluator.evaluate cost -->  "+pathFinderState.getCost());
        }

        return path.endNode().getRelationships( Direction.OUTGOING,RelationshipTypes.SURFACE_LEVEL, RelationshipTypes.ENTRANCE,RelationshipTypes.ESCALATOR,RelationshipTypes.RAMP,RelationshipTypes.GATE,RelationshipTypes.STAIRS,RelationshipTypes.LIFT );
    }



    /**
     * PF - Path expandFromGatelines
     *
     * config:
     *
     *
     * @param path
     * @param state
     * @return Iterable<Relationship>
     */
    private Iterable<Relationship> expandFromGatelines(Path path, BranchState<PathFinderState> state){

        Iterable<Relationship> result = null;
        PathFinderState pathFinderState = state.getState().copy();

        //if ( directionalChannel.hasProperty( Properties.latency ) ) {
        pathFinderState.incrementCost(10.0d);
        //}

        state.setState( pathFinderState );
//        for (String rel:rels)
//            td = td.relationships(RelationshipTypes.valueOf(rel));


        return path.endNode().getRelationships(RelationshipTypes.RAMP, Direction.OUTGOING );
    }



    @Override
    public PathExpander<PathFinderState> reverse() {
        return null;
    }
}
