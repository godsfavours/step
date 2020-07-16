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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/user-data")
public class UserDataServlet extends HttpServlet {
    private DatastoreService datastore;
    private UserService userService;
    private Entity userEntity;
    private Entity pingPongEntity;

    @Override
    public void init() {
        datastore = DatastoreServiceFactory.getDatastoreService();
        userService = UserServiceFactory.getUserService();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String postType = request.getParameter("postType");
        if (postType.equals("nickname")) {
            changeNickName(request.getParameter("newNickName"));
        } else if (postType.equals("loggame")) {
            String game = request.getParameter("game");
            if (game.equals("pong")) {
                logGameStats(request.getParameter("result").equals("win") ? true : false);
            }
        }
    }

    public void changeNickName(String newNickName) {
        Entity userEntity = getEntity("User");
        userEntity.setProperty("nickname", newNickName);
        datastore.put(userEntity);
    }

    public void logGameStats(boolean gameWon) {
        Entity pingPongEntity = getEntity("PingPongEntity");
        if (gameWon) {                 
            long playerWins = (Long) pingPongEntity.getProperty("wins");
            long winStreak = (Long) pingPongEntity.getProperty("winStreak");

            playerWins++;
            winStreak++;
            
            pingPongEntity.setProperty("wins", playerWins);
            pingPongEntity.setProperty("winStreak", winStreak);
            if (winStreak > (Long) pingPongEntity.getProperty("longestWinStreak")) {
                pingPongEntity.setProperty("longestWinStreak", winStreak);
            }
        } else {
            long playerLoses = (Long) pingPongEntity.getProperty("loses");
            playerLoses++;
            pingPongEntity.setProperty("loses", playerLoses);
            pingPongEntity.setProperty("winStreak", 0);
        }
        datastore.put(pingPongEntity);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ptoString();
        if (request.getParameter("query").equals("post")) {
            doPost(request, response);
            return;
        } 

        String getType = request.getParameter("getType");
        if (getType.equals("login")) {
            response.setContentType("application/json");

            String thisUrl = "/games.html";
            String responseJson;

            if (!userService.isUserLoggedIn()) {
                responseJson = createJsonForLog(false, thisUrl);
                response.getWriter().println(responseJson);
            } else if (userService.isUserLoggedIn()) {
                confirmUserEntity();
                responseJson = createJsonForLog(true, thisUrl);
                response.getWriter().println(responseJson);
            }
        } else if (getType.equals("property")) {
            response.setContentType("text/html");
            if (request.getParameter("gameEntity").equals("PingPongEntity")) {
                String propertyName = request.getParameter("propertyName");
                String propertyVal = String.valueOf(pingPongEntity.getProperty(propertyName));
                response.getWriter().println(propertyVal);
            }
        }
    }

    public String createJsonForLog(boolean loggedIn, String returnUrl) {
        String returnLogUrl = (!loggedIn ? userService.createLoginURL(returnUrl) 
        : userService.createLogoutURL(returnUrl));

        String response = "{";
        response += "\"loggedIn\": ";
        response += "\"" + loggedIn + "\"";
        response += ", ";
        if (loggedIn) {
            response += "\"nickname\": ";
            Entity entity = getEntity("User");
            response += "\"" + entity.getProperty("nickname")  + "\"";
            response += ", ";
        }
        response += "\"returnUrl\": ";
        response += "\"" + returnLogUrl + "\"";
        response += "}";
        return response;
    }

    private void addUserEntity() {
        Entity user = new Entity("User");
        user.setProperty("nickname", userService.getCurrentUser().getNickname());
        user.setProperty("userId", userService.getCurrentUser().getUserId()); 
        user.setProperty("email", userService.getCurrentUser().getEmail());
        datastore.put(user);
    }

    private void addPingPongEntity() {
        Entity pingPongEntity = new Entity("PingPongEntity");
        pingPongEntity.setProperty("userId", userService.getCurrentUser().getUserId());
        pingPongEntity.setProperty("wins", 0);
        pingPongEntity.setProperty("loses", 0);
        pingPongEntity.setProperty("winStreak", 0);
        pingPongEntity.setProperty("longestWinStreak", 0);
        datastore.put(pingPongEntity);
    }

    // This method ensures that all necessary entities exist for the web page.
    private void confirmUserEntity() {
        confirmUserEntityHelper("User");
        confirmUserEntityHelper("PingPongEntity");
    }

    // Helper calls getEntity() to search for the Entities in the datastore. If not found, 
    // add[entityName]() adds the entity to the datastore
    private void confirmUserEntityHelper(String entityType) {
        Entity entity = getEntity(entityType);
        if (entity == null) {
            if (entityType.equals("User")) {
                addUserEntity();
            } else if (entityType.equals("PingPongEntity")) {
                addPingPongEntity();
            }
        } 
    }

    private Entity getEntity(String entityType) {
        Query query = new Query(entityType);
        PreparedQuery results = datastore.prepare(query);
        for (Entity entity: results.asIterable()) {
            if (entity.getProperty("userId").equals(userService.getCurrentUser().getUserId())) {
            return entity;
            }
        }
        return null;
    }
    
    private void ptoString() {
        System.out.println("#########################################");
        Query query = new Query("User");
        PreparedQuery results = datastore.prepare(query);

        for (Entity entity: results.asIterable()) {
            //datastore.delete(entity.getKey());
            System.out.println(entity.toString());
        }

        query = new Query("PingPongEntity");
        results = datastore.prepare(query);
        for (Entity entity: results.asIterable()) {
            //datastore.delete(entity.getKey());
            System.out.println(entity.toString());
        }
    }
}
