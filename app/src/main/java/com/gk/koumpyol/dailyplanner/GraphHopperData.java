package com.gk.koumpyol.dailyplanner;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GraphHopperData
{
    public class GraphHopperResponse
    {
        @SerializedName("paths")
        private List<Path> paths;

        public List<Path> getPaths() {return paths;}
    }

    public class Path
    {
        @SerializedName("points")
        private String points;

        public String getPoints() {return points;}
    }
}


