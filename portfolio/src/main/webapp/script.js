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

function getQuote() {
  console.log('Fetching a random quote.');

  // wait for response
  const responsePromise = fetch('/data');

  // pass the response to handleResponse()
  responsePromise.then(handleResponse);
}

function handleResponse(response) {
  console.log('Handling the response.');

  // wait for/get text promise from response
  const textPromise = response.text();

  // if ready, pass along to addQuoteToDom()
  textPromise.then(addQuoteToDom);
}

function addQuoteToDom(quote) {
  console.log('Adding quote to dom: ' + quote);

  const quoteContainer = document.getElementById('quote-container');
  quoteContainer.innerText = quote;

  console.log('Added quote to dom');
}
