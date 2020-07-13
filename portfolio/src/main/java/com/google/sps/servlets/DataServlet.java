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

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime; 

/** Servlet responsible for handling comments **/
@WebServlet("/data")
public class DataServlet extends HttpServlet {
  private DatastoreService datastore;
  
  @Override
  public void init() {
      datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("dateposted", Query.SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    
    // maxComments used to limit the number of comments the client sees
    String maxCommentsStr = request.getParameter("maxComments");
    if (maxCommentsStr == null || maxCommentsStr.isEmpty()) {
        return;
    }

    int maxComments = Integer.parseInt(maxCommentsStr);
    int count = 0;
    List<String> comments = new ArrayList<>();
    for (Entity entity: results.asIterable()) {
        if (count >= maxComments) break;

        String message = (String) entity.getProperty("usercomment") +
                        " - " + (String) entity.getProperty("username") +
                         ", " + (String) entity.getProperty("dateposted");
        comments.add(message);
        count++;     
    }

    Gson gson = new Gson();
    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userName = request.getParameter("user-name");
    String userComment = request.getParameter("user-comment");
  
    // DateTimeFormatter included to allow for comment sorting by date added
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
    LocalDateTime now = LocalDateTime.now();  
    String datePosted = dtf.format(now);  

    // Create the Entity and add to datastore
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("username", userName);
    commentEntity.setProperty("usercomment", userComment);
    commentEntity.setProperty("dateposted", datePosted);
    datastore.put(commentEntity);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }
}
