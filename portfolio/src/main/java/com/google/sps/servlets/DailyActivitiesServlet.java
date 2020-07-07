// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

/** Returns daily activities data as a JSON object */
@WebServlet("/daily-activities")
public class DailyActivitiesServlet extends HttpServlet {
  // Private variables used to construct json. Represents hours spend on each activity
  HashMap<String, Double> activityHours;

  @Override
  public void init() {
      activityHours = new HashMap<String, Double>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONParser parser = new JSONParser();
    try {
        // Parse json file into array that can be worked on
        Object obj = parser.parse(new FileReader("resources/daily-activities.json"));
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray activities = (JSONArray) jsonObject.get("Daily Activities");

        for (int i = 0; i < activities.size(); i++) {
            JSONObject jsonObj = (JSONObject) activities.get(i);

            // JSONObject inherits keySet() from the Map class. It is used here to
            // get the keys for each element within the json object (ex. "eating", "reading")
            jsonObj.keySet().forEach(keyStr ->
            {
                // Cannot directly cast Object to double as double is primitive, so long
                // used as an intermediary
                updateValues((String) keyStr, Double.valueOf((Long) jsonObj.get(keyStr)));
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    response.setContentType("application/json;");
    response.getWriter().println(constructJSON());
  }

  // Updates the private variables of DailyActivitiesServlet class
  private void updateValues(String key, Double value) {
    activityHours.put(key, getAverage(activityHours.get(key), value));
  }

  // Averages the values for each element
  private Double getAverage(Double mainVal, Double newVal) {
    if (mainVal == null){
        return newVal;
    } else if (mainVal == 0 || newVal == 0) {
        return mainVal + newVal;
    }
    return (mainVal + newVal) / 2;
  }

  // Constructs the response JSON as a String
  private String constructJSON() {
    String json = "{";
    json += "\"Eating\": " + String.valueOf(activityHours.get("Eating")) + ", ";
    json += "\"Free Time\": " + String.valueOf(activityHours.get("Free time")) + ", ";
    json += "\"Reading\": " + String.valueOf(activityHours.get("Reading")) + ", ";
    json += "\"Sleeping\": " + String.valueOf(activityHours.get("Sleeping")) + ", ";
    json += "\"Working\": " + String.valueOf(activityHours.get("Working")) + ", ";
    json += "\"Exercising\": " + String.valueOf(activityHours.get("Exercising"));
    json += "}";
   
    return json;
  }
}