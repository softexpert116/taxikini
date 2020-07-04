package com.taxi.common;

import com.stfalcon.chatkit.commons.models.IUser;

/*
 * Created by troy379 on 04.04.17.
 */
public class User {

    public String id;
    public String firstname;
    public String lastname;
    public String type;
    public String email;
    public String photo;
    public String phone;
    public String password;
    public String car_num;
    public String license_num;
    public String license_pic;
    public String plate_num;
    public String car_pic;
    public String nric;
    public String token;

    public String online;
    public double lat;
    public double lng;


    public User(String id, String firstname, String lastname, String type, String email, String photo,
                String phone, String password, String car_num, String license_num, String plate_num,
                String license_pic, String car_pic, String nric, String token, String online, double lat, double lng) {
        this.id = id;
        this.type = type;
        this.firstname = firstname;
        this.lastname = lastname;
        this.type = type;
        this.email = email;
        this.photo = photo;
        this.phone = phone;
        this.password = password;
        this.car_num= car_num;
        this.license_num= license_num;
        this.plate_num= plate_num;
        this.license_pic= license_pic;
        this.car_pic = car_pic;
        this.nric = nric;
        this.token = token;

        this.online = online;
        this.lat = lat;
        this.lng = lng;
    }

}
