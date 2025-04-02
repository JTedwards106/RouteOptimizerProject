package org.example.views;

import javafx.scene.web.WebEngine;

import java.util.List;

public class MapView {
    private WebEngine webEngine;
    private String apiKey;

    public MapView(WebEngine webEngine, String apiKey) {
        this.webEngine = webEngine;
        this.apiKey = apiKey; // Use the dynamically passed API key
    }

    public void loadMap() {
        // Load the HTML content for the map
        webEngine.loadContent(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">" +
                        "<meta charset=\"utf-8\">" +
                        "<style>" +
                        "  html, body { height: 100%; margin: 0; padding: 0; }" +
                        "  #map { height: 100%; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div id=\"map\"></div>" +
                        "<script>" +
                        "  let map;" +
                        "  function initMap() {" +
                        "    map = new google.maps.Map(document.getElementById('map'), {" +
                        "      center: { lat: 18.1096, lng: -77.2975 }, // Jamaica" +
                        "      zoom: 9" + // Adjusted zoom level to show more of Jamaica
                        "    });" +
                        "  }" +
        "<script>" +
        "(function(g) {" +
        "  var h, a, k, p = 'The Google Maps JavaScript API', c = 'google', l = 'importLibrary', q = '__ib__', m = document, b = window;" +
        "  b = b[c] || (b[c] = {});" +
        "  var d = b.maps || (b.maps = {}), r = new Set(), e = new URLSearchParams();" +
        "  var u = () => h || (h = new Promise(async (f, n) => {" +
        "    a = m.createElement('script');" +
        "    e.set('libraries', [...r] + '');" +
        "    for (k in g) e.set(k.replace(/[A-Z]/g, t => '_' + t[0].toLowerCase()), g[k]);" +
        "    e.set('callback', c + '.maps.' + q);" +
        "    a.src = `https://maps.${c}apis.com/maps/api/js?` + e;" +
        "    d[q] = f;" +
        "    a.onerror = () => h = n(Error(p + ' could not load.'));" +
        "    a.nonce = m.querySelector('script[nonce]')?.nonce || '';" +
        "    m.head.append(a);" +
        "  }));" +
        "  d[l] ? console.warn(p + ' only loads once. Ignoring:', g) : d[l] = (f, ...n) => r.add(f) && u().then(() => d[l](f, ...n));" +
        "})({key: '" + "AIzaSyAostccD2PjvYuuP29C-JkS_L7aI_mI6lM" + "', v: 'weekly'});" +
        "</script>" +
        "</body>" +
                        "</html>"
        );
    }

    public void displayRoute(List<String> path) {
        if (path == null || path.isEmpty()) {
            System.out.println("Path is null or empty");
            return;
        }

        // JavaScript function to display the route
        String jsFunction = "function displayRoute(path) { " +
                "  const geocoder = new google.maps.Geocoder(); " +
                "  let waypoints = []; " +
                "  let origin = null; " +
                "  let destination = null; " +
                "  if (path && path.length > 0) { " +
                "     origin = path[0]; " +
                "     destination = path[path.length - 1]; " +
                "  } " +

                "  for (let i = 1; i < path.length - 1; i++) { " +
                "    waypoints.push({ location: path[i], stopover: true }); " +
                "  } " +

                "  if (origin && destination) { " +
                "    geocoder.geocode({ 'address': origin }, function (results, status) { " +
                "      if (status == google.maps.GeocoderStatus.OK) { " +
                "        var originLatLng = results[0].geometry.location; " +
                "         geocoder.geocode({ 'address': destination }, function (results, status) { " +
                "           if (status == google.maps.GeocoderStatus.OK) { " +
                "              var destinationLatLng = results[0].geometry.location; " +

                "             const directionsService = new google.maps.DirectionsService(); " +
                "             const directionsRequest = { " +
                "                origin: originLatLng, " +
                "                destination: destinationLatLng, " +
                "                waypoints: waypoints, " +
                "                travelMode: google.maps.TravelMode.DRIVING " +
                "             }; " +

                "             directionsService.route(directionsRequest, function (result, status) { " +
                "                if (status === google.maps.DirectionsStatus.OK) { " +
                "                  const directionsRenderer = new google.maps.DirectionsRenderer({suppressMarkers: true}); " +
                "                  directionsRenderer.setMap(map); " +
                "                  directionsRenderer.setDirections(result); " +
                "                } else { " +
                "                   console.error('Directions service failed:', status); " +
                "                } " +
                "             }); " +
                "           } else { " +
                "             console.error('Geocoding for destination failed:', status); " +
                "           } " +
                "         }); " +
                "      } else { " +
                "        console.error('Geocoding for origin failed:', status); " +
                "      } " +
                "    }); " +
                "  } else { " +
                "      console.error('Origin or destination is null. Path:', path); " +
                "  } " +
                "} ";

        webEngine.executeScript(jsFunction);
        // Pass the route data to the JavaScript function
        webEngine.executeScript("displayRoute(" + java.util.Arrays.toString(path.toArray()) + ")");
    }

    public void clearMap() {
        // Clear the map by removing the directions renderer
        webEngine.executeScript("if (window.directionsRenderer) { window.directionsRenderer.setMap(null); }");
    }
}