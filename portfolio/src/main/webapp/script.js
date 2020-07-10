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
    getGameTabs();

    google.charts.load('current', {'packages':['corechart']});
    //google.charts.setOnLoadCallback(drawChart);
}

function getQuote() {
  fetch('/quotes').then(response => response.text()).then((quote) => {
      document.getElementById('quote-container').innerText = quote;
  });
}


// below handles the tags in the Game section--good practice to separate to new js file?
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