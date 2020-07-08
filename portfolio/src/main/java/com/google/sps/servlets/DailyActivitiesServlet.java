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
import java.util.Map;
import com.google.gson.Gson;

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

        activityHours.clear();
        for (int i = 0; i < activities.size(); i++) {
            JSONObject jsonObj = (JSONObject) activities.get(i);

            // JSONObject inherits keySet() from the Map class. It is used here to
            // get the keys for each element within the json object (ex. "eating", "reading")
            jsonObj.keySet().forEach(keyStr ->
            {              
                updateValues((String) keyStr, (Double) jsonObj.get(keyStr));
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    response.setContentType("application/json;");
    response.getWriter().println(constructJSON());
  }

  // Updates the private variables of DailyActivitiesServlet class
  private void updateValues(String activity, Double hours) {
    activityHours.put(activity, getAverage(activityHours.get(activity), hours));
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
    for (Map.Entry activity : activityHours.entrySet()) { 
        json += "\"" + (String) activity.getKey() + "\": ";
        json += String.valueOf((Double) activity.getValue());
        json += ", ";
    } 

    // Remove comma after last element
    json = json.substring(0, json.length() - 2);
    json += "}";

    return json;
  }
}