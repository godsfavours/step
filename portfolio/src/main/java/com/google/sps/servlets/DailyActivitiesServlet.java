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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Returns daily activities data as a JSON object */
@WebServlet("/daily-activities")
public class DailyActivitiesServlet extends HttpServlet {
  // Private variables used to construct json. Represents hours spend on each activity
  private double eating, freeTime, reading, sleeping, working, exercising;

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
                Long keyvalue = (long) jsonObj.get(keyStr); 
                updateValues((String) keyStr, (double) keyvalue);
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    response.setContentType("application/json;");
    response.getWriter().println(constructJSON());
  }

  // Updates the private variables of DailyActivitiesServlet class
  private void updateValues(String key, double value) {
    switch (key) {
        case "Eating":
            this.eating = getAverage(this.eating, value);
            break;
        case "Free Time":
            this.freeTime = getAverage(this.freeTime, value);
            break;
        case "Reading":
            this.reading = getAverage(this.reading, value);
            break;
        case "Sleeping":
            this.sleeping = getAverage(this.sleeping, value);
            break;
        case "Working":
            this.working = getAverage(this.working, value);
            break;
        case "Exercising":
            this.exercising = getAverage(this.exercising, value);
            break;
        default:
            System.out.println("Key not identified.");
            break;
    }
  }

  // Averages the values for each element
  private double getAverage(double mainVal, double newVal) {
    if (mainVal == 0 || newVal == 0){
        return mainVal + newVal;
    }
    return (mainVal + newVal) / 2;
  }

  // Constructs the response JSON as a String
  private String constructJSON() {
    String json = "{";
    json += "\"Eating\": " + String.valueOf(this.eating) + ", ";
    json += "\"Free Time\": " + String.valueOf(this.freeTime) + ", ";
    json += "\"Reading\": " + String.valueOf(this.reading) + ", ";
    json += "\"Sleeping\": " + String.valueOf(this.sleeping) + ", ";
    json += "\"Working\": " + String.valueOf(this.working) + ", ";
    json += "\"Exercising\": " + String.valueOf(this.exercising);
    json += "}";

    System.out.println(json);
    return json;
  }
}