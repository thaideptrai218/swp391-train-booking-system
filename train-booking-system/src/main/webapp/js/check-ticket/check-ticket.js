
function fetchStations(query, datalistId) {
  fetch(`station-autocomplete?query=${encodeURIComponent(query)}`)
    .then(response => response.json())
    .then(data => {
      const datalist = document.getElementById(datalistId);
      datalist.innerHTML = "";
      data.forEach(station => {
        const option = document.createElement("option");
        option.value = station;
        datalist.appendChild(option);
      });
    });
}

document.getElementById("departureStation").addEventListener("input", function () {
  fetchStations(this.value, "departureSuggestions");
});

document.getElementById("arrivalStation").addEventListener("input", function () {
  fetchStations(this.value, "arrivalSuggestions");
});

