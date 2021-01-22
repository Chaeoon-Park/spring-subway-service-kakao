package subway.path.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.line.domain.Line;
import subway.line.domain.Sections;
import subway.station.domain.Station;
import subway.station.dto.StationResponse;

import java.util.Collection;
import java.util.List;

public class Path {
    private List<Station> stations;
    private int distance;
    private int fare;

    public Path(List<Station> stations, int distance, int fare) {

    }

    public static Path make(List<Line> lines, Station sourceStation, Station targetStation) {
        DijkstraShortestPath dijkstraShortestPath = getDijkstraPath(lines, sourceStation, targetStation);
        return new Path(
                dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList(),
                (int)dijkstraShortestPath.getPath(sourceStation, targetStation).getWeight() ,
                0
        );
    }

    private static DijkstraShortestPath getDijkstraPath(List<Line> lines, Station sourceStation, Station targetStation) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph
                = new WeightedMultigraph(DefaultWeightedEdge.class);

        lines.stream()
                .map(Line::getSections)
                .map(Sections::getSections)
                .flatMap(Collection::stream)
                .forEach(section ->{
                    Station upStation = section.getUpStation();
                    Station downStation = section.getDownStation();

                    if(!graph.containsVertex(upStation)) {
                        graph.addVertex(upStation);
                    }
                    if(!graph.containsVertex(downStation)) {
                        graph.addVertex(downStation);
                    }
                    graph.setEdgeWeight(graph.addEdge(upStation,downStation), section.getDistance());
                    graph.setEdgeWeight(graph.addEdge(downStation,upStation), section.getDistance());
                } );

        return new DijkstraShortestPath(graph);

        //return ;
    }

    public int getDistance() {
        return distance;
    }

    public int getFare() {
        return fare;
    }

    public List<Station> getStations() {
        return stations;
    }
}