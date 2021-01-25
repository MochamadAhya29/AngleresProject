package com.mochamadahya.angleres.model

class Post {
    private  var postid: String = ""
    private var postimage: String = ""
    private var postvideo: String = ""
    private var publisher: String = ""
    private var judul: String = ""
    private var description: String = ""
    private var numberlikes: String = ""

    constructor()
    constructor(
        postid: String,
        postimage: String,
        postvideo: String,
        publisher: String,
        judul: String,
        description: String,
        numberlikes: String
    ) {
        this.postid = postid
        this.postimage = postimage
        this.postvideo= postvideo
        this.publisher = publisher
        this.judul = judul
        this.description = description
        this.numberlikes = numberlikes
    }

    //get
    fun getPostId(): String{
        return postid
    }

    fun getPostImage(): String{
        return postimage
    }

    fun getPostVideo(): String{
        return postvideo
    }

    fun getPublisher(): String{
        return publisher
    }

    fun getJudul(): String{
        return judul
    }
    fun getDescription(): String{
        return description
    }

    fun getNumberLikes(): String{
        return numberlikes
    }



    //set
    fun setPostId(postid: String){
        this.postid = postid
    }
    fun setNumberLikes(numberlikes: String){
        this.numberlikes = numberlikes
    }
    fun setPostImage(postimage: String){
        this.postimage = postimage
    }
    fun setPostVideo(postvideo: String){
        this.postvideo = postvideo
    }
    fun setPublisher(publisher: String){
        this.publisher = publisher
    }
    fun setJudul(judul: String){
        this.judul = judul
    }
    fun setDescription(description: String){
        this.description = description
    }

}