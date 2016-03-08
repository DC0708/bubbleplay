package com.example.mapdemo;

import com.google.android.gms.games.Player;

/**
 * Created by aniket-cr on 6/3/16.
 */
public class PlayerDetails {

        String name;
        String email;
        double distance;
        boolean box;
        String appID;

        PlayerDetails(String _name, String _email, double _distance, boolean _box, String _appID) {

            appID = _appID;
            name = _name;
            email = _email;
            distance = _distance;
            box = _box;
        }

        PlayerDetails(String _name, String _appID)
        {
            appID = _appID;
            name = _name;
            email = "email";
            distance = 5.0;
        }

}
