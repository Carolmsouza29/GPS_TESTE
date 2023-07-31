package com.example.gps;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//Padroniza as informações do Json, define como mostrar a página com as informações
    public class GooglePtPercurso {
        private String json;

        public GooglePtPercurso(String jsonString) {

            this.json = jsonString;
        }

        public List<PadronizacaoRec> placedurationspaces() {
            List<PadronizacaoRec> routeSteps = new ArrayList<>();

            try {
                JSONObject json = new JSONObject(this.json);

                JSONArray legs = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
                JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");

                int durationn = legs.getJSONObject(0).getJSONObject("duration").getInt("value");
                int distancee = legs.getJSONObject(0).getJSONObject("distance").getInt("value");
                routeSteps.add(new PadronizacaoRec(distancee, durationn));

                for (int i = 0; i < steps.length(); i++) {
                    JSONObject step = steps.getJSONObject(i);
                    JSONObject startLocation = step.getJSONObject("start_location");

                    double lat = startLocation.getDouble("lat");
                    double lng = startLocation.getDouble("lng");
                    LatLng startPoint = new LatLng(lat, lng);

                    JSONObject duration = step.getJSONObject("duration");
                    String durationText = duration.getString("text");
                    int durationValue = duration.getInt("value");

                    JSONObject distance = step.getJSONObject("distance");
                    String distanceText = distance.getString("text");
                    int distanceValue = distance.getInt("value");
                    Log.d("allincluded", "Trecho "+ i +" distance: " + distanceValue +  " duration " + durationValue);
                    PadronizacaoRec routeStep = new PadronizacaoRec(distanceValue, durationValue);
                    routeSteps.add(routeStep);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routeSteps;
        }
}
