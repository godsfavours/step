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

function init_index() {
    getQuote();
}

function init_games() {
    getLogin();
    getGameTabs();

    google.charts.load('current', {'packages':['table']});
    google.charts.setOnLoadCallback(getLeaderboard);
    getLeaderboard();
}

function getLogin() {
    var loginButton = document.getElementById('game-login-button');
    var loginText = document.getElementById('game-login-text');
    var nameForm = document.getElementById('nickname-form');
    var nameFormButton = document.getElementById('form-submit');
    var nameFormInput = document.getElementById('form-input');

    fetch('/user-data?query=get&getType=login').then(response => response.json()).then((response) => {
      var message = (response.loggedIn == "true") ? "Hello, " + response.nickname + ". Enjoy the games!" : "Please log in to play the games!";
      var url = response.returnUrl;

      loginButton.href = url;
      loginText.innerText = message;

      if (response.loggedIn == "true") {
          showPlayButtons(true);
          loginButton.innerText = "Log out";
          nameForm.style.display = "block";
      } else {
          showPlayButtons(false);
          loginButton.innerText = "Login";
          nameForm.style.display = "none";
      }

      nameFormButton.addEventListener("click", function() {
          if (nameFormInput.value.length != 0) {
              let queryString = "/user-data?query=post&postType=nickname&newNickName=" + nameFormInput.value;
              fetch(queryString);
          }    
      });
    });
}

function showPlayButtons(gamesPlayable) {
    var playButtons = document.querySelectorAll('#play-button');
    playButtons.forEach((button) => {
        console.log("disabling button");
        gamesPlayable ? button.style.display = "block" : button.style.display = "none";
    })
}
// Handles the Comments and Leaderboard tags in the Game section
function getGameTabs() {
    const tabs = document.querySelectorAll('[data-tab-target]');
    const tabContents = document.querySelectorAll('[data-tab-content]')
 
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const target = document.querySelector(tab.dataset.tabTarget);
            // Hide tabs that are showing
            tabContents.forEach(tabContent => {
                tabContent.classList.remove('active');
            })
            tabs.forEach(tab => {
                tab.classList.remove('active');
            })
            // Set desired tab to active
            tab.classList.add('active')
            target.classList.add('active')
        });
    });
}

function getLeaderboard() {  
    fetch('/leaderboards').then(response => response.json())
    .then((highestStreaks) => {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Player');
    data.addColumn('number', 'Longest Win Streak');
    Object.keys(highestStreaks).forEach((player) => {
      data.addRow([player, highestStreaks[player]]);
    });

    const options = {
      width: '100%',
      height: '100%',
      showRowNumber: true,
      sort: 'disable'
    };

    const table = new google.visualization.Table(document.getElementById('chart-container'));
    table.draw(data, {options});
  });
}

function init_about() {
}

function init_comments() {
    getMessages();
}

//Retrives quotes stored in resources/quotes.txt to be displayed in index.html
function getQuote() {
  fetch('/quotes').then(response => response.text()).then((quote) => {
      document.getElementById('quote-container').innerText = quote;
  });
}

// Retrieves messages stored in datastore to be dislayed on comments.html
function getMessages() {
    var commentListElement = document.getElementById('comment-list');
    commentListElement.innerHTML = ""; // clear out old comments

    var queryString = '/comments?maxComments=' + document.getElementById('max-comments').value;
    console.log(queryString);
    fetch(queryString).then(response => response.json()).then((messages) => {
        messages.forEach((message) => {
            commentListElement.appendChild(createCommentElement(message));
        })
    });
}

// Creates <li> element containing text to be used in getMessages()
function createCommentElement(message) {
    let commentElement = document.createElement('li');
    commentElement.className = 'comment';
    commentElement.innerText = message;
    return commentElement;
}