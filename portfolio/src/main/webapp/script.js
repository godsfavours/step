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
    getGameTabs();
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

    var queryString = '/data?maxComments=' + document.getElementById('max-comments').value;
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