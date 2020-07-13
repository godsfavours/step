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

// Calls functions to fetch the state of the page
function init() {
    getQuote();
    getMessages();
    
    google.charts.load('current', {'packages':['corechart']});
    google.charts.setOnLoadCallback(drawChart);
}

function getQuote() {
  fetch('/quotes').then(response => response.text()).then((quote) => {
      document.getElementById('quote-container').innerText = quote;
  });
}

function getMessages() {
    var commentListElement = document.getElementById('comment-list');
    commentListElement.innerHTML = ""; // clear out old comments

    var queryString = '/data?maxComments=' + document.getElementById('max-comments').value;
    console.log(queryString);
    fetch(queryString).then(response => response.json()).then((messages) => {
        messages.forEach((message) => {
            commentListElement.appendChild(createCommentElement(message));
        })
    });
}

// Creates <li> element containing text
function createCommentElement(message) {
    let commentElement = document.createElement('li');
    commentElement.className = 'comment';
    commentElement.innerText = message;
    return commentElement;
}


function drawChart() {
    fetch('/daily-activities').then(response => response.json())
        .then((dailyActivities) => {
            var data = new google.visualization.DataTable();
            data.addColumn('string', 'Activity');
            data.addColumn('number', 'Hours');

            Object.keys(dailyActivities).forEach((activity) => {
            data.addRow([activity, dailyActivities[activity]]);
            });

            var options = {
                'width':600,
                'height':500,
                is3D: true,
                legend: {position: 'none'}
            };

            var chart = new google.visualization.PieChart(document.getElementById('chart-div'));
            chart.draw(data, options);
    });
}