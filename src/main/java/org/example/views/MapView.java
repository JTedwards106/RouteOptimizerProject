package org.example.views;

import com.google.maps.GeoApiContext;
import com.google.maps.model.*;
import com.google.maps.RoutesApi;
import com.google.maps.errors.ApiException;
import javafx.scene.web.WebEngine;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

// Embedded Google Maps integration.
public class MapView {
    private WebEngine webEngine;
    private String apiKey;

    public MapView(WebEngine webEngine, String apiKey) {
        this.webEngine = webEngine;
        this.apiKey = apiKey;
    }

    public void loadMap() {
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
                        "</script>" +
                        "<script src=\"https://maps.googleapis.com/maps/api/js?key=" + apiKey + "&callback=initMap\" async defer></script>" +
                        "</body>" +
                        "</html>"
        );
    }

    public void displayRoute(List<String> path) {
        // Convert town names to LatLng using Google Maps Geocoding API within JavaScript
        // This is necessary because we can't directly pass Java objects to JavaScript.
        // The path is now a list of place names
        if (path == null || path.isEmpty()) {
            System.out.println("Path is null or empty");
            return;
        }

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

                "  if (origin && destination) { " +  // Check that origin and destination are not null
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
        webEngine.executeScript("if (window.directionsRenderer) { window.directionsRenderer.setMap(null); }");
    }
}
