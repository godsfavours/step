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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/quotes")
public class DataServlet extends HttpServlet {

  private List<String> quotes;

  @Override
  public void init() {
    getQuotes();
  }

  public void getQuotes() {
    quotes = new ArrayList<>();

    BufferedReader reader;
    try {
      reader = new BufferedReader(new FileReader("resources/quotes.txt"));
    
      String line;    
      while ((line = reader.readLine()) != null) {
        quotes.add(line);
      }
      
      reader.close();
    } catch (IOException e) {
        e.printStackTrace();
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String quote = quotes.get((int) (Math.random() * quotes.size()));

    response.setContentType("text/html;");
    response.getWriter().println(quote);
  }
}
