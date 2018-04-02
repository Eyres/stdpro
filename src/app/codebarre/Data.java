package app.codebarre;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Data {
    private String pathFileOne;

    private String pathFileTwo;

    private Map<String, Map<String, String>> source;

    private List<Map<String, String>> data;
    
    private Set<String> rejet;

    public String getPathFileOne() {
        return pathFileOne;
    }

    public void setPathFileOne(String pathFileOne) {
        this.pathFileOne = pathFileOne;
    }

    public String getPathFileTwo() {
        return pathFileTwo;
    }

    public void setPathFileTwo(String pathFileTwo) {
        this.pathFileTwo = pathFileTwo;
    }

    public List<Map<String, String>> getData() {
        return data;
    }

    public void setData(List<Map<String, String>> map) {
        this.data = map;
    }

    /**
     * @return the source
     */
    public Map<String, Map<String, String>> getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Map<String, Map<String, String>> source) {
        this.source = source;
    }

    /**
     * @return the rejet
     */
    public Set<String> getRejet() {
        return rejet;
    }

    /**
     * @param rejet the rejet to set
     */
    public void setRejet(Set<String> rejet) {
        this.rejet = rejet;
    }
}
