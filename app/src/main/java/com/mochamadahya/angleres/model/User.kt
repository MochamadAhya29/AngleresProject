package com.mochamadahya.angleres.model

import android.media.Image

class User {
    private var nickname: String=""
    private var nomortelepon: String=""
    private var address: String=""
    private var image: String=""

    constructor()
    constructor(nickname: String, nomortelepon: String, address: String, image: String) {
        this.nickname = nickname
        this.nomortelepon = nomortelepon
        this.address = address
    }

    fun getNickname() : String {
        return nickname
    }
    fun getNomorTelepon() : String {
        return nomortelepon
    }
    fun getAddress() : String {
        return address
    }

    fun getImage() : String {
        return image
    }

    fun setNickname(nickname: String) {
        this.nickname = nickname
    }
    fun setNomorTelepon(nomortelepon: String) {
        this.nomortelepon = nomortelepon
    }
    fun setAddress(address: String) {
        this.address = address
    }
    fun setImage(image: String) {
        this.image = image
    }
}